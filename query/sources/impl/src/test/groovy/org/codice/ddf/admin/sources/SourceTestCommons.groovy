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
package org.codice.ddf.admin.sources

import org.codice.ddf.admin.common.fields.common.*
import org.codice.ddf.admin.common.services.ServiceCommons
import org.codice.ddf.admin.sources.fields.FactoryPid
import org.codice.ddf.admin.sources.fields.ServicePid
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField

class SourceTestCommons {

    static final ENDPOINT_URL = SourceConfigUnionField.ENDPOINT_URL_FIELD

    static final CREDENTIALS = CredentialsField.DEFAULT_FIELD_NAME

    static final USERNAME = CredentialsField.USERNAME

    static final PASSWORD = CredentialsField.PASSWORD

    static final ADDRESS = AddressField.DEFAULT_FIELD_NAME

    static final PORT = PortField.DEFAULT_FIELD_NAME

    static final HOSTNAME = HostnameField.DEFAULT_FIELD_NAME

    static final URL = UrlField.DEFAULT_FIELD_NAME

    static final SERVICE_PID = ServicePid.DEFAULT_FIELD_NAME

    static final FACTORY_PID_KEY = ServiceCommons.FACTORY_PID_KEY

    static final SERVICE_PID_KEY = ServiceCommons.SERVICE_PID_KEY

    static final SOURCE_NAME = SourceConfigUnionField.SOURCE_NAME_FIELD

    static final FACTORY_PID = FactoryPid.DEFAULT_FIELD_NAME

    static final SOURCE_CONFIG = SourceConfigUnionField.FIELD_NAME

    static final ID = 'id'

    static F_PID = "testFactoryPid"

    static S_PID = "testServicePid"

    static S_PID_1 = "testServicePid1"

    static S_PID_2 = "testServicePid2"

    static SOURCE_ID_1 = "testId1"

    static SOURCE_ID_2 = "testId2"

    static TEST_USERNAME = "admin"

    static TEST_PASSWORD = "admin"

    static TEST_SOURCENAME = "testSourceName"

    static discoverByAddressActionArgs = [
        (ADDRESS)    : [
            (PORT)    : 8993,
            (HOSTNAME): "localhost"
        ],
        (CREDENTIALS): [
            (USERNAME): TEST_USERNAME,
            (PASSWORD): TEST_PASSWORD
        ]
    ]

    static discoverByUrlActionArgs = [
        (ENDPOINT_URL): "http://localhost:8993/sevices/csw",
        (CREDENTIALS) : [
            (USERNAME): TEST_USERNAME,
            (PASSWORD): TEST_PASSWORD
        ]
    ]

    static baseManagedServiceConfigs = [
        (S_PID_1): [
            (PASSWORD)       : TEST_PASSWORD,
            (ID)             : SOURCE_ID_1,
            (FACTORY_PID_KEY): F_PID,
            (SERVICE_PID_KEY): S_PID_1,
            (USERNAME)       : TEST_USERNAME
        ],
        (S_PID_2): [
            (PASSWORD)       : TEST_PASSWORD,
            (ID)             : SOURCE_ID_2,
            (FACTORY_PID_KEY): F_PID,
            (SERVICE_PID_KEY): S_PID_2,
            (USERNAME)       : TEST_USERNAME
        ]
    ]

    static configToBeDeleted = [
            (PASSWORD)                    : TEST_PASSWORD,
            (ID)                          : SOURCE_ID_1,
            (FACTORY_PID_KEY)             : F_PID,
            (SERVICE_PID_KEY)             : S_PID,
            (USERNAME)                    : TEST_USERNAME
    ]

    static saveConfigActionArgs = [
        (SOURCE_CONFIG): [
            (ENDPOINT_URL): "https://localhost:8993",
            (FACTORY_PID) : F_PID,
            (SOURCE_NAME) : TEST_SOURCENAME,
            (CREDENTIALS) : [
                (USERNAME): TEST_USERNAME,
                (PASSWORD): TEST_PASSWORD
            ]
        ]
    ]

    static refreshSaveConfigActionArgs() {
        saveConfigActionArgs = [
            (SOURCE_CONFIG): [
                (ENDPOINT_URL): "https://localhost:8993",
                (FACTORY_PID) : F_PID,
                (SOURCE_NAME) : TEST_SOURCENAME,
                (CREDENTIALS) : [
                    (USERNAME): TEST_USERNAME,
                    (PASSWORD): TEST_PASSWORD
                ]
            ]
        ]
    }

    static refreshDiscoverByAddressActionArgs() {
        discoverByAddressActionArgs = [
            (ADDRESS)    : [
                (PORT)    : 8993,
                (HOSTNAME): "localhost"
            ],
            (CREDENTIALS): [
                (USERNAME): TEST_USERNAME,
                (PASSWORD): TEST_PASSWORD
            ]
        ]
    }

    static refreshDiscoverByUrlActionArgs() {
        discoverByUrlActionArgs = [
            (ENDPOINT_URL): "http://localhost:8993/sevices/csw",
            (CREDENTIALS) : [
                (USERNAME): TEST_USERNAME,
                (PASSWORD): TEST_PASSWORD
            ]
        ]
    }
}
