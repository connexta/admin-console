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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.AUTHENTICATION;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.AUTHENTICATION_AND_ATTRIBUTE_STORE;
import static org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE;
import static org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties.LDAP_LOGIN_FEATURE;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.ldap.fields.config.LdapUseCase;
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions;

import com.google.common.collect.ImmutableList;

public class InstallEmbeddedLdap extends BaseFunctionField<BooleanField> {

    public static final String NAME = "installEmbeddedLdap";

    public static final String DESCRIPTION =
            "Installs the internal embedded LDAP. Used for testing purposes only. LDAP port: 1389, LDAPS port: 1636, ADMIN port: 4444";

    private LdapUseCase useCase;

    private final ConfiguratorFactory configuratorFactory;

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
        case AUTHENTICATION:
            configurator.add(featureActions.start(EmbeddedLdapServiceProperties.EMBEDDED_LDAP_FEATURE));
            configurator.add(featureActions.start(EmbeddedLdapServiceProperties.EMBEDDED_LDAP_FEATURE));
            configurator.add(featureActions.start(LDAP_LOGIN_FEATURE));
            configurator.add(featureActions.start(EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE));
            break;
        case ATTRIBUTE_STORE:
            configurator.add(featureActions.start(EmbeddedLdapServiceProperties.EMBEDDED_LDAP_FEATURE));
            configurator.add(featureActions.start(LDAP_CLAIMS_HANDLER_FEATURE));
            configurator.add(featureActions.start(EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE));
            break;
        case AUTHENTICATION_AND_ATTRIBUTE_STORE:
            configurator.add(featureActions.start(EmbeddedLdapServiceProperties.EMBEDDED_LDAP_FEATURE));
            configurator.add(featureActions.start(LDAP_LOGIN_FEATURE));
            configurator.add(featureActions.start(LDAP_CLAIMS_HANDLER_FEATURE));
            configurator.add(featureActions.start(EmbeddedLdapServiceProperties.ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE));
            break;
        }

        OperationReport report = configurator.commit("Installed Embedded LDAP");

        if (report.containsFailedResults()) {
            addResultMessage(failedPersistError());
        }

        return new BooleanField(report.containsFailedResults());
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new InstallEmbeddedLdap(configuratorFactory, featureActions);
    }
}
