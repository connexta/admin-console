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
package org.codice.ddf.admin.ldap.actions.embedded;

import static org.codice.ddf.admin.ldap.actions.embedded.EmbeddedLdapServiceProperties.ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE;
import static org.codice.ddf.admin.ldap.actions.embedded.EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE;
import static org.codice.ddf.admin.ldap.actions.embedded.EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE;
import static org.codice.ddf.admin.ldap.actions.embedded.EmbeddedLdapServiceProperties.EMBEDDED_LDAP_FEATURE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN_AND_ATTRIBUTE_STORE;
import static org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE;
import static org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties.LDAP_LOGIN_FEATURE;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.message.ErrorMessage;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.ldap.fields.config.LdapUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

// TODO: tbatie - 4/4/17 - Move embedded ldap to a seperate action creator
public class InstallEmbeddedLdap extends BaseAction<BooleanField> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallEmbeddedLdap.class);

    public static final String NAME = "installEmbeddedLdap";
    public static final String DESCRIPTION = "Installs the internal embedded LDAP. Used for testing purposes only. LDAP port: 1389, LDAPS port: 1636, ADMIN port: 4444";

    private LdapUseCase useCase;
    private Configurator configurator;

    public InstallEmbeddedLdap(Configurator configurator) {
        super(NAME, DESCRIPTION, new BooleanField());
        useCase = new LdapUseCase();
        this.configurator = configurator;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(useCase);
    }

    @Override
    public BooleanField performAction() {
        // TODO: tbatie - 4/4/17 - This should return back the setup config
        switch (useCase.getValue()) {
        case LOGIN:
            configurator.startFeature(EMBEDDED_LDAP_FEATURE);
            configurator.startFeature(LDAP_LOGIN_FEATURE);
            configurator.startFeature(DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE);
            break;
        case ATTRIBUTE_STORE:
            configurator.startFeature(EMBEDDED_LDAP_FEATURE);
            configurator.startFeature(LDAP_CLAIMS_HANDLER_FEATURE);
            configurator.startFeature(DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE);
            break;
        case LOGIN_AND_ATTRIBUTE_STORE:
            configurator.startFeature(EMBEDDED_LDAP_FEATURE);
            configurator.startFeature(LDAP_LOGIN_FEATURE);
            configurator.startFeature(LDAP_CLAIMS_HANDLER_FEATURE);
            configurator.startFeature(ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE);
            break;
        default:
            LOGGER.debug("Unrecognized LDAP use case \"{}\". No commits will be made. ",
                    useCase.getValue());
            // TODO: tbatie - 4/4/17 - change this to specify the arg that was unknown
            addMessage(new ErrorMessage("FAILED_PERSIST"));
            return new BooleanField(false);
        }

        OperationReport report = configurator.commit();

        if(report.containsFailedResults()) {
            addMessage(new ErrorMessage("CANNOT_INSTALL"));
            return new BooleanField(false);

        }

        return new BooleanField(true);
    }
}
