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
package org.codice.ddf.admin.ldap.embedded;

import static org.codice.ddf.admin.ldap.embedded.EmbeddedLdapServiceProperties.ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE;
import static org.codice.ddf.admin.ldap.embedded.EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE;
import static org.codice.ddf.admin.ldap.embedded.EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE;
import static org.codice.ddf.admin.ldap.embedded.EmbeddedLdapServiceProperties.EMBEDDED_LDAP_FEATURE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN_AND_ATTRIBUTE_STORE;
import static org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE;
import static org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties.LDAP_LOGIN_FEATURE;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.ldap.fields.config.LdapUseCase;
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class InstallEmbeddedLdap extends BaseFunctionField<BooleanField> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallEmbeddedLdap.class);

    public static final String NAME = "installEmbeddedLdap";

    public static final String DESCRIPTION =
            "Installs the internal embedded LDAP. Used for testing purposes only. LDAP port: 1389, LDAPS port: 1636, ADMIN port: 4444";

    private LdapUseCase useCase;

    private ConfiguratorFactory configuratorFactory;

    private final FeatureActions featureActions;

    public InstallEmbeddedLdap(ConfiguratorFactory configuratorFactory,
            FeatureActions featureActions) {
        super(NAME, DESCRIPTION, new BooleanField());
        this.configuratorFactory = configuratorFactory;
        this.featureActions = featureActions;
        useCase = new LdapUseCase();
        updateArgumentPaths();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(useCase);
    }

    @Override
    public BooleanField performFunction() {
        // TODO: tbatie - 4/4/17 - This should return back the setup config
        Configurator configurator = configuratorFactory.getConfigurator();
        switch (useCase.getValue()) {
        case LOGIN:
            configurator.add(featureActions.start(EMBEDDED_LDAP_FEATURE));
            configurator.add(featureActions.start(LDAP_LOGIN_FEATURE));
            configurator.add(featureActions.start(DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE));
            break;
        case ATTRIBUTE_STORE:
            configurator.add(featureActions.start(EMBEDDED_LDAP_FEATURE));
            configurator.add(featureActions.start(LDAP_CLAIMS_HANDLER_FEATURE));
            configurator.add(featureActions.start(
                    DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE));
            break;
        case LOGIN_AND_ATTRIBUTE_STORE:
            configurator.add(featureActions.start(EMBEDDED_LDAP_FEATURE));
            configurator.add(featureActions.start(LDAP_LOGIN_FEATURE));
            configurator.add(featureActions.start(LDAP_CLAIMS_HANDLER_FEATURE));
            configurator.add(featureActions.start(ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE));
            break;
        default:
            LOGGER.debug("Unrecognized LDAP use case \"{}\". No commits will be made. ",
                    useCase.getValue());
            // TODO: tbatie - 4/4/17 - change this to specify the arg that was unknown
            addResultMessage(new ErrorMessageImpl("FAILED_PERSIST"));
            return new BooleanField(false);
        }

        OperationReport report = configurator.commit("Installed Embedded LDAP");

        if (report.containsFailedResults()) {
            addResultMessage(new ErrorMessageImpl("CANNOT_INSTALL"));
            return new BooleanField(false);

        }

        return new BooleanField(true);
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new InstallEmbeddedLdap(configuratorFactory, featureActions);
    }
}
