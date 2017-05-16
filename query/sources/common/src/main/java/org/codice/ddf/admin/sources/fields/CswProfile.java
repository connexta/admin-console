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
package org.codice.ddf.admin.sources.fields;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class CswProfile extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "cswProfile";

    public static final String TYPE_NAME = "CswProfile";

    public static final String DESCRIPTION =
            "CSW application profile specifying the capabilities of the CSW server when federating to other systems.";

    public static final String CSW_SPEC_PROFILE_FEDERATED_SOURCE =
            "CswFederatedSource";

    public static final String CSW_FEDERATION_PROFILE_SOURCE = "DDFCswFederatedSource";

    public static final String GMD_CSW_ISO_FEDERATED_SOURCE = "GmdCswFederatedSource";

    public CswProfile() {
        this(null);
    }

    public CswProfile(Field<String> cswProfile) {
        super(DEFAULT_FIELD_NAME,
                TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new DDFCswFederatedSource(),
                        new CswFederatedSource(),
                        new GmdCswFederatedSource()),
                cswProfile);
    }

    protected static final class CswFederatedSource extends StringField {

        public static final String DESCRIPTION = "CSW Specification Profile Federated Source that should be used when federating to an external CSW service.";

        public CswFederatedSource() {
            super(CSW_SPEC_PROFILE_FEDERATED_SOURCE, CSW_SPEC_PROFILE_FEDERATED_SOURCE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return CSW_SPEC_PROFILE_FEDERATED_SOURCE;
        }
    }

    protected static final class DDFCswFederatedSource extends StringField {

        public static final String DESCRIPTION = "DDF's full fidelity CSW Federation Profile. Use this when federating to a DDF based system.";

        public DDFCswFederatedSource() {
            super(CSW_FEDERATION_PROFILE_SOURCE, CSW_FEDERATION_PROFILE_SOURCE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return CSW_FEDERATION_PROFILE_SOURCE;
        }
    }

    protected static final class GmdCswFederatedSource extends StringField {

        public static final String DESCRIPTION = "CSW Federated Source using the Geographic MetaData (GMD) format (ISO 19115:2003).";

        public GmdCswFederatedSource() {
            super(GMD_CSW_ISO_FEDERATED_SOURCE, GMD_CSW_ISO_FEDERATED_SOURCE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return GMD_CSW_ISO_FEDERATED_SOURCE;
        }
    }
}
