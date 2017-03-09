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
package org.codice.ddf.admin.security.ldap.embedded.persist;

import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.LDAP_USE_CASE;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.FAILED_PERSIST;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_PERSIST;
import static org.codice.ddf.admin.api.services.EmbeddedLdapServiceProperties.ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE;
import static org.codice.ddf.admin.api.services.EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE;
import static org.codice.ddf.admin.api.services.EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE;
import static org.codice.ddf.admin.api.services.EmbeddedLdapServiceProperties.EMBEDDED_LDAP_FEATURE;
import static org.codice.ddf.admin.api.services.LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE;
import static org.codice.ddf.admin.api.services.LdapLoginServiceProperties.LDAP_LOGIN_FEATURE;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.AUTHENTICATION;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.AUTHENTICATION_AND_ATTRIBUTE_STORE;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.ldap.EmbeddedLdapConfiguration;
import org.codice.ddf.admin.api.handler.method.PersistMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class DefaultEmbeddedLdapPersistMethod extends PersistMethod<EmbeddedLdapConfiguration> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DefaultEmbeddedLdapPersistMethod.class);

    public static final String DEFAULT_CONFIGURATIONS_ID = "defaults";

    public static final String DESCRIPTION =
            "Starts up the Opendj Embedded App and installs default realm and/or attribute store configurations.";

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(LDAP_USE_CASE);

    public static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(SUCCESSFUL_PERSIST,
            "Successfully started and saved Embedded LDAP configurations.");

    public static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(FAILED_PERSIST,
            "Failed to start Embedded LDAP or install a default configuration file.");

    private final Configurator configurator;

    public DefaultEmbeddedLdapPersistMethod(ConfiguratorFactory configuratorFactory) {
        super(DEFAULT_CONFIGURATIONS_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                null,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);

        this.configurator = configuratorFactory.getConfigurator();
    }

    @Override
    public Report persist(EmbeddedLdapConfiguration configuration) {
        switch (configuration.ldapUseCase()) {
        case AUTHENTICATION:
            configurator.startFeature(EMBEDDED_LDAP_FEATURE);
            configurator.startFeature(LDAP_LOGIN_FEATURE);
            configurator.startFeature(DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE);
            break;
        case ATTRIBUTE_STORE:
            configurator.startFeature(EMBEDDED_LDAP_FEATURE);
            configurator.startFeature(LDAP_CLAIMS_HANDLER_FEATURE);
            configurator.startFeature(DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE);
            break;
        case AUTHENTICATION_AND_ATTRIBUTE_STORE:
            configurator.startFeature(EMBEDDED_LDAP_FEATURE);
            configurator.startFeature(LDAP_LOGIN_FEATURE);
            configurator.startFeature(LDAP_CLAIMS_HANDLER_FEATURE);
            configurator.startFeature(ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE);
            break;
        default:
            LOGGER.debug("Unrecognized LDAP use case \"{}\". No commits will be made. ",
                    configuration.ldapUseCase());
            return Report.createReport(SUCCESS_TYPES, FAILURE_TYPES, null, FAILED_PERSIST);
        }
        OperationReport report = configurator.commit("Embedded LDAP saved with details: {}",
                configuration.toString());

        return Report.createReport(SUCCESS_TYPES,
                FAILURE_TYPES,
                null,
                report.containsFailedResults() ? FAILED_PERSIST : SUCCESSFUL_PERSIST);
    }
}
