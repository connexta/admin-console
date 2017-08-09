/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.ldap.discover;

import static org.codice.ddf.admin.ldap.commons.LdapMessages.userAttributeNotFoundError;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.TestFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapMessages;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.LdapAttributeName;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.security.common.SecurityMessages;
import org.codice.ddf.admin.security.common.SecurityValidation;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;
import org.codice.ddf.admin.security.common.services.StsServiceProperties;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.SearchScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class LdapTestClaimMappings extends TestFunctionField {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapTestClaimMappings.class);

    public static final String FIELD_NAME = "testClaimMappings";

    public static final String DESCRIPTION =
            "Tests whether the attributes mapped to claims exist on users beneath the user base.";

    public static final String USER_NAME_ATTRIBUTE = "userNameAttribute";

    public static final String BASE_USER_DN = "baseUserDn";

    private StsServiceProperties stsServiceProperties;

    private LdapConnectionField conn;

    private LdapBindUserInfo bindInfo;

    private LdapAttributeName usernameAttribute;

    private LdapDistinguishedName baseUserDn;

    private ClaimsMapEntry.ListImpl claimMappings;

    private LdapTestingUtils utils;

    private final ConfiguratorSuite configuratorSuite;

    public LdapTestClaimMappings(ConfiguratorSuite configuratorSuite) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorSuite = configuratorSuite;

        conn = new LdapConnectionField().useDefaultRequired();
        bindInfo = new LdapBindUserInfo().useDefaultRequired();
        usernameAttribute = new LdapAttributeName(USER_NAME_ATTRIBUTE).isRequired(true);
        baseUserDn = new LdapDistinguishedName(BASE_USER_DN);
        baseUserDn.isRequired(true);
        claimMappings = new ClaimsMapEntry.ListImpl();
        claimMappings.useDefaultRequired();
        claimMappings.isRequired(true);

        updateArgumentPaths();

        stsServiceProperties = new StsServiceProperties();
        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(conn, bindInfo, usernameAttribute, baseUserDn, claimMappings);
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new LdapTestClaimMappings(configuratorSuite);
    }

    @Override
    public void validate() {
        super.validate();

        if (containsErrorMsgs()) {
            return;
        }

        List<StringField> claimArgs = claimMappings.getList()
                .stream()
                .map(ClaimsMapEntry::claimField)
                .collect(Collectors.toList());

        addReportMessages(SecurityValidation.validateStsClaimsExist(claimArgs,
                configuratorSuite.getServiceActions(),
                stsServiceProperties));

        // TODO: 7/7/17 - tbatie - Currently the ClaimsMapEntry contains a StringField as a value. It really should be a LdapAttributeName. Fix this once there is a generic way to create MapField objects that contain different value field.

        claimMappings.getList()
                .stream()
                .map(ClaimsMapEntry::claimValueField)
                .map(claim -> LdapAttributeName.validate(claim.getValue(), claim.path()))
                .forEach(this::addErrorMessages);
    }

    @Override
    public BooleanField performFunction() {
        try (LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn,
                bindInfo)) {
            addReportMessages(connectionAttempt);

            if (containsErrorMsgs()) {
                return new BooleanField(false);
            }

            Connection ldapConnection = connectionAttempt.getResult();

            addReportMessages(utils.checkDirExists(baseUserDn, ldapConnection));

            // Short-circuit return here, if either the user or group directory does not exist
            if (containsErrorMsgs()) {
                return new BooleanField(false);
            }

            claimMappings.getList()
                    .stream()
                    .map(ClaimsMapEntry::claimValueField)
                    .filter(claim -> !mappingAttributeFound(ldapConnection, claim.getValue()))
                    .forEach(claim -> addErrorMessage(userAttributeNotFoundError(claim.path())));
        } catch (IOException e) {
            LOGGER.warn("Error closing LDAP connection", e);
        }

        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of(SecurityMessages.INVALID_CLAIM_TYPE,
                LdapMessages.CANNOT_BIND,
                LdapMessages.DN_DOES_NOT_EXIST,
                LdapMessages.USER_ATTRIBUTE_NOT_FOUND,
                DefaultMessages.FAILED_TEST_SETUP,
                DefaultMessages.CANNOT_CONNECT);
    }

    /**
     * Checks the baseUserDn for users.
     *
     * @param ldapConnection connection used to query ldap
     * @param mapAttribute   the attribute to check against the ldap users
     */
    private boolean mappingAttributeFound(Connection ldapConnection, String mapAttribute) {
        return !utils.getLdapQueryResults(ldapConnection,
                baseUserDn.getValue(),
                Filter.and(Filter.present(usernameAttribute.getValue()),
                        Filter.present(mapAttribute))
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1)
                .isEmpty();
    }

    /**
     * Intentionally scoped as private.
     * This is a test support method to be invoked by Spock tests which will elevate scope as needed
     * in order to execute. If Java-based unit tests are ever needed, this scope will need to be
     * updated to package-private.
     *
     * @param utils Ldap support utilities
     */
    private void setTestingUtils(LdapTestingUtils utils) {
        this.utils = utils;
    }

    /**
     * Intentionally scoped as private.
     * This is a test support method to be invoked by Spock tests which will elevate scope as needed
     * in order to execute. If Java-based unit tests are ever needed, this scope will need to be
     * updated to package-private.
     *
     * @param stsServiceProperties service properties for mocking
     */
    private void setStsServiceProperties(StsServiceProperties stsServiceProperties) {
        this.stsServiceProperties = stsServiceProperties;
    }
}
