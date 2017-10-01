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
package org.codice.ddf.admin.ldap.discover

import com.google.common.collect.ImmutableMap
import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.api.report.FunctionReport
import org.codice.ddf.admin.common.fields.base.scalar.StringField
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.HostnameField
import org.codice.ddf.admin.common.fields.common.PortField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.ldap.TestLdapServer
import org.codice.ddf.admin.ldap.commons.LdapMessages
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName
import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField
import org.codice.ddf.admin.security.common.SecurityMessages
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry
import org.codice.ddf.admin.security.common.services.StsServiceProperties
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions
import spock.lang.Specification

import static org.codice.ddf.admin.ldap.LdapTestingCommons.*
import static org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField.CLAIMS_MAPPING

class LdapTestClaimMappingsSpec extends Specification {
    static final List<Object> FUNCTION_PATH = [LdapTestClaimMappings.FIELD_NAME]
    static TestLdapServer server
    LdapTestClaimMappings action
    ServiceActions serviceActions
    StsServiceProperties stsServiceProperties
    Map<String, Object> args
    def badPaths
    def baseMsg
    private StringField userAttribute
    private LdapDistinguishedName baseDn
    private ListField<ClaimsMapEntry> claimMappings
    LdapTestingUtils utilsMock
    boolean ldapConnectionIsClosed


    def setupSpec() {
        server = TestLdapServer.getInstance().useSimpleAuth()
        server.startListening()
    }

    def cleanupSpec() {
        server.shutdown()
        server = null
    }

    def setup() {
        utilsMock = new LdapTestConnectionSpec.LdapTestingUtilsMock()
        loadLdapTestProperties()

        serviceActions = Mock(ServiceActions)
        stsServiceProperties = Mock(StsServiceProperties)
        stsServiceProperties.getConfiguredStsClaims(_) >> ['claim1', 'claim2', 'claim3', 'claim4', 'claim5']
        def configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.serviceActions >> serviceActions

        action = new LdapTestClaimMappings(configuratorSuite)
        action.setTestingUtils(new LdapTestConnectionSpec.LdapTestingUtilsMock())
        action.setStsServiceProperties(stsServiceProperties)

        userAttribute = new StringField()
        userAttribute.setValue('uid')
        baseDn = new LdapDistinguishedName()
        baseDn.setValue(LDAP_SERVER_BASE_USER_DN)
        claimMappings = new ClaimsMapEntry.ListImpl()

        // Initialize bad paths
        baseMsg = [LdapTestClaimMappings.FIELD_NAME]
        badPaths = [badOrMissingUserNameAttrPath: baseMsg + [LdapDirectorySettingsField.USER_NAME_ATTRIBUTE],
                    missingUserPath             : baseMsg + [LdapDirectorySettingsField.BASE_USER_DN],
                    missingHostPath             : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME],
                    missingPortPath             : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME],
                    missingEncryptPath          : baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME, LdapEncryptionMethodField.DEFAULT_FIELD_NAME],
                    missingUsernamePath         : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME],
                    missingUserpasswordPath     : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.PASSWORD_FIELD_NAME],
                    missingBindMethodPath       : baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME, LdapBindMethod.DEFAULT_FIELD_NAME],
                    badClaimMappingPath         : baseMsg + [CLAIMS_MAPPING]
        ]
    }

    def cleanup() {
        ldapConnectionIsClosed = false
    }

    def 'fail on missing required fields'() {
        setup:

        when:
        FunctionReport report = action.execute(null, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == badPaths.size()
        report.getErrorMessages().count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == badPaths.size()

        report.getErrorMessages()*.path as Set == badPaths.values() as Set
    }

    def 'fail with invalid user dn'() {
        setup:
        baseDn.setValue('BAD')

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)   : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)      : simpleBindInfo().getValue(),
                (LdapTestClaimMappings.USER_NAME_ATTRIBUTE): userAttribute.getValue(),
                (LdapTestClaimMappings.BASE_USER_DN)       : baseDn.getValue(),
                (CLAIMS_MAPPING)                           : createClaimsMapping(ImmutableMap.of("claim1", "cn")).getValue()]

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.INVALID_DN
        } == 1

        report.getErrorMessages()*.getPath() as Set == [badPaths.missingUserPath] as Set
    }

    def 'fail to connect to LDAP'() {
        setup:

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)   : noEncryptionLdapConnectionInfo().port(666).getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)      : simpleBindInfo().getValue(),
                (LdapTestClaimMappings.USER_NAME_ATTRIBUTE): userAttribute.getValue(),
                (LdapTestClaimMappings.BASE_USER_DN)       : baseDn.getValue(),
                (CLAIMS_MAPPING)                           : createClaimsMapping(ImmutableMap.of("claim1", "cn")).getValue()]

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        !report.getResult().getValue()
        report.getErrorMessages().get(0).getCode() == DefaultMessages.CANNOT_CONNECT
        report.getErrorMessages().get(0).getPath() == baseMsg + [LdapConnectionField.DEFAULT_FIELD_NAME]
    }

    def 'fail to bind to LDAP'() {
        setup:

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)   : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)      : simpleBindInfo().password('badPassword').getValue(),
                (LdapTestClaimMappings.USER_NAME_ATTRIBUTE): userAttribute.getValue(),
                (LdapTestClaimMappings.BASE_USER_DN)       : baseDn.getValue(),
                (CLAIMS_MAPPING)                           : createClaimsMapping(ImmutableMap.of("claim1", "cn")).getValue()]

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        !report.getResult().getValue()
        report.getErrorMessages().get(0).getCode() == LdapMessages.CANNOT_BIND
        report.getErrorMessages().get(0).getPath() == baseMsg + [LdapBindUserInfo.DEFAULT_FIELD_NAME]
    }

    def 'fail when baseUserDN does not exist'() {
        setup:
        baseDn.setValue('ou=users,dc=example,dc=BAD')

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)   : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)      : simpleBindInfo().getValue(),
                (LdapTestClaimMappings.USER_NAME_ATTRIBUTE): userAttribute.getValue(),
                (LdapTestClaimMappings.BASE_USER_DN)       : baseDn.getValue(),
                (CLAIMS_MAPPING)                           : createClaimsMapping(ImmutableMap.of("claim1", "cn")).getValue()]

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.DN_DOES_NOT_EXIST
        } == 1

        report.getErrorMessages()*.path as Set == [badPaths.missingUserPath] as Set
    }

    def 'fail when bad sts claim keys are supplied for mapping'() {
        setup:
        def failedPaths = [badPaths.badClaimMappingPath + 0 + ClaimsMapEntry.KEY_FIELD_NAME,
                           badPaths.badClaimMappingPath + 1 + ClaimsMapEntry.KEY_FIELD_NAME] as Set

        def claimsMapping = createClaimsMapping(ImmutableMap.of("badclaim1", "cn",
                "badclaim2", "employeetype",
                "claim3", "sn"))
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)   : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)      : simpleBindInfo().getValue(),
                (LdapTestClaimMappings.USER_NAME_ATTRIBUTE): userAttribute.getValue(),
                (LdapTestClaimMappings.BASE_USER_DN)       : baseDn.getValue(),
                (CLAIMS_MAPPING)                           : claimsMapping.getValue()]

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 2
        report.getErrorMessages().count {
            it.getCode() == SecurityMessages.INVALID_CLAIM_TYPE
        } == 2

        report.getErrorMessages()*.path as Set == failedPaths
    }

    def 'fail when missing user attributes are supplied for mapping'() {
        setup:
        def failedPaths = [badPaths.badClaimMappingPath + 0 + ClaimsMapEntry.VALUE_FIELD_NAME,
                           badPaths.badClaimMappingPath + 2 + ClaimsMapEntry.VALUE_FIELD_NAME] as Set

        def claimsMapping = createClaimsMapping(ImmutableMap.of("claim1", "XXX",
                "claim2", "cn",
                "claim3", "YYY",
                "claim4", "employeetype"))
        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)   : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)      : simpleBindInfo().getValue(),
                (LdapTestClaimMappings.USER_NAME_ATTRIBUTE): userAttribute.getValue(),
                (LdapTestClaimMappings.BASE_USER_DN)       : baseDn.getValue(),
                (CLAIMS_MAPPING)                           : claimsMapping.getValue()]

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 2
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.USER_ATTRIBUTE_NOT_FOUND
        } == 2

        report.getErrorMessages()*.path as Set == failedPaths
    }


    def 'fail when claimsMapping value formats are incorrect'() {
        setup:
        def failedPaths = [badPaths.badClaimMappingPath + 0 + ClaimsMapEntry.VALUE_FIELD_NAME,
                           badPaths.badClaimMappingPath + 2 + ClaimsMapEntry.VALUE_FIELD_NAME,
                           badPaths.badClaimMappingPath + 3 + ClaimsMapEntry.VALUE_FIELD_NAME] as Set

        def claimsMapping = createClaimsMapping(ImmutableMap.of("claim1", "space in between",
                "claim2", "correct-format",
                "claim3", "2017",
                "claim4", "Speci@!Ch&recter#s",
                "claim5", "correctFormat"))

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)   : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)      : simpleBindInfo().getValue(),
                (LdapTestClaimMappings.USER_NAME_ATTRIBUTE): userAttribute.getValue(),
                (LdapTestClaimMappings.BASE_USER_DN)       : baseDn.getValue(),
                (CLAIMS_MAPPING)                           : claimsMapping.getValue()]

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 3
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.INVALID_USER_ATTRIBUTE
        } == 3

        report.getErrorMessages()*.path as Set == failedPaths
    }

    def 'fail when usernameAttribute format is incorrect'() {
        setup:
        def failedPaths = [badPaths.badOrMissingUserNameAttrPath] as Set

        def claimsMapping = createClaimsMapping(ImmutableMap.of("claim1", "sn",
                "claim2", "cn",
                "claim3", "employeetype"))

        def userAttribute = "bad form@t"

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)   : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)      : simpleBindInfo().getValue(),
                (LdapTestClaimMappings.USER_NAME_ATTRIBUTE): userAttribute,
                (LdapTestClaimMappings.BASE_USER_DN)       : baseDn.getValue(),
                (CLAIMS_MAPPING)                           : claimsMapping.getValue()]

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages().count {
            it.getCode() == LdapMessages.INVALID_USER_ATTRIBUTE
        } == 1

        report.getErrorMessages()*.path as Set == failedPaths
    }

    def 'pass when all user attributes found'() {
        setup:
        def claimsMapping = createClaimsMapping(ImmutableMap.of("claim1", "sn",
                "claim2", "cn",
                "claim3", "employeetype"))

        args = [(LdapConnectionField.DEFAULT_FIELD_NAME)   : noEncryptionLdapConnectionInfo().getValue(),
                (LdapBindUserInfo.DEFAULT_FIELD_NAME)      : simpleBindInfo().getValue(),
                (LdapTestClaimMappings.USER_NAME_ATTRIBUTE): userAttribute.getValue(),
                (LdapTestClaimMappings.BASE_USER_DN)       : baseDn.getValue(),
                (CLAIMS_MAPPING)                           : claimsMapping.getValue()]

        action.setTestingUtils(utilsMock)

        when:
        FunctionReport report = action.execute(args, FUNCTION_PATH)
        ldapConnectionIsClosed = utilsMock.getLdapConnectionAttempt().getResult().isClosed()

        then:
        report.getErrorMessages().empty
        report.getResult().getValue()
        ldapConnectionIsClosed
    }

    def 'Returns all the possible error codes correctly'(){
        when:
        def errorCodes = action.getFunctionErrorCodes()

        then:
        errorCodes.size() == 6
        errorCodes.contains(DefaultMessages.CANNOT_CONNECT)
        errorCodes.contains(DefaultMessages.FAILED_TEST_SETUP)
        errorCodes.contains(LdapMessages.USER_ATTRIBUTE_NOT_FOUND)
        errorCodes.contains(LdapMessages.DN_DOES_NOT_EXIST)
        errorCodes.contains(LdapMessages.CANNOT_BIND)
        errorCodes.contains(SecurityMessages.INVALID_CLAIM_TYPE)
    }

    private static ClaimsMapEntry.ListImpl createClaimsMapping(Map<String, String> claims) {
        def claimMappings = new ClaimsMapEntry.ListImpl()
        claims.each {
            claimMappings.add(new ClaimsMapEntry().key(it.key).value(it.value))
        }
        return claimMappings
    }
}
