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
 */
package org.codice.ddf.admin.api.validation;

import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_REALM;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createInvalidFieldMsg;
import static org.codice.ddf.admin.api.validation.ValidationUtils.validateString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.forgerock.i18n.LocalizedIllegalArgumentException;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.Filter;

import com.google.common.collect.ImmutableList;

public class LdapValidationUtils {
    // TODO RAP 26 Jan 17: Shouldn't be public/used elsewhere
    // The constants in here should either all be private or they should be relocated
    // to a common repository class/interface that all the ldap configuration classes can access
    public static final String LDAPS = "ldaps";

    public static final String START_TLS = "startTls";

    public static final String SIMPLE = "Simple";

    //  These fields are not currently supported for binding
    //  public static final String SASL = "SASL";
    //  public static final String GSSAPI_SASL = "GSSAPI SASL";
    public static final String DIGEST_MD5_SASL = "Digest MD5 SASL";

    public static final String AUTHENTICATION = "authentication";

    public static final String ATTRIBUTE_STORE = "attributeStore";

    public static final String AUTHENTICATION_AND_ATTRIBUTE_STORE =
            "authenticationAndAttributeStore";

    static final String NONE = "none";

    private static final List<String> BIND_METHODS = ImmutableList.of(SIMPLE, DIGEST_MD5_SASL);

    private static final ImmutableList<String> LDAP_ENCRYPTION_METHODS = ImmutableList.of(LDAPS,
            START_TLS,
            NONE);

    private static final ImmutableList<String> LDAP_USE_CASES = ImmutableList.of(AUTHENTICATION,
            ATTRIBUTE_STORE,
            AUTHENTICATION_AND_ATTRIBUTE_STORE);

    // TODO RAP 26 Jan 17: More constants that should be centralized and for which we should
    // come up with a solution to better share between JS and Java
    static final String ACTIVE_DIRECTORY = "activeDirectory";

    static final String OPEN_LDAP = "openLdap";

    static final String OPEN_DJ = "openDj";

    static final String EMBEDDED = "embeddedLdap";

    static final String UNKNOWN = "unknown";

    private static final ImmutableList<String> LDAP_TYPES = ImmutableList.of(ACTIVE_DIRECTORY,
            OPEN_LDAP,
            OPEN_DJ,
            EMBEDDED,
            UNKNOWN);

    public static List<ConfigurationMessage> validateEncryptionMethod(String encryptionMethod,
            String configId) {
        List<ConfigurationMessage> errors = validateString(encryptionMethod, configId);
        if (errors.isEmpty() && LDAP_ENCRYPTION_METHODS.stream()
                .noneMatch(encryptionMethod::equalsIgnoreCase)) {
            errors.add(createInvalidFieldMsg(String.format(
                    "Unknown encryption method [%s]. Encryption method must be one of: [%s]",
                    encryptionMethod,
                    String.join(",", LDAP_ENCRYPTION_METHODS)), configId));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateDn(String dn, String configId) {
        List<ConfigurationMessage> errors = validateString(dn, configId);
        if (errors.isEmpty()) {
            try {
                DN.valueOf(dn);
            } catch (LocalizedIllegalArgumentException e) {
                errors.add(createInvalidFieldMsg(String.format("Invalid DN [%s]", dn), configId));
            }

        }

        return errors;
    }

    public static List<ConfigurationMessage> validateBindUserMethod(String bindMethod,
            String configId) {
        List<ConfigurationMessage> errors = validateString(bindMethod, configId);
        if (errors.isEmpty() && BIND_METHODS.stream()
                .noneMatch(bindMethod::equalsIgnoreCase)) {
            errors.add(createInvalidFieldMsg(String.format(
                    "Unknown bind method [%s]. Bind method must be one of: [%s]",
                    bindMethod,
                    String.join(",", BIND_METHODS)), configId));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateLdapQuery(String query, String configId) {
        List<ConfigurationMessage> errors = validateString(query, configId);
        if (errors.isEmpty()) {
            try {
                Filter.valueOf(query);
            } catch (LocalizedIllegalArgumentException e) {
                errors.add(createInvalidFieldMsg(String.format(
                        "Badly formatted LDAP query filter: %s",
                        e.getMessage()), configId));
            }
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateLdapType(String ldapType, String configId) {
        List<ConfigurationMessage> errors = validateString(ldapType, configId);
        if (errors.isEmpty() && LDAP_TYPES.stream()
                .noneMatch(ldapType::equalsIgnoreCase)) {
            errors.add(createInvalidFieldMsg(String.format(
                    "Unknown LDAP type [%s]. LDAP type must be one of: [%s]",
                    ldapType,
                    String.join(",", LDAP_TYPES)), configId));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateLdapUseCase(String ldapUseCase,
            String configId) {
        List<ConfigurationMessage> errors = validateString(ldapUseCase, configId);
        if (errors.isEmpty() && LDAP_USE_CASES.stream()
                .noneMatch(ldapUseCase::equalsIgnoreCase)) {
            errors.add(createInvalidFieldMsg(String.format(
                    "Unknown LDAP use case [%s]. LDAP use case must be one of: [%s]",
                    ldapUseCase,
                    String.join(",", LDAP_USE_CASES)), configId));
        }
        return errors;
    }

    public static List<ConfigurationMessage> validateBindRealm(LdapConfiguration configuration) {
        List<ConfigurationMessage> errors = new ArrayList<>();
        if(configuration.bindUserMethod() != null && configuration.bindUserMethod().equals(LdapValidationUtils.DIGEST_MD5_SASL)) {
            errors.addAll(configuration.validate(Arrays.asList(BIND_REALM)));
        }
        return errors;
    }
}
