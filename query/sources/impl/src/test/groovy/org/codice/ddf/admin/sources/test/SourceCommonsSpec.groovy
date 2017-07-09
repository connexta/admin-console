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
package org.codice.ddf.admin.sources.test

import ddf.catalog.data.ContentType
import ddf.catalog.operation.QueryRequest
import ddf.catalog.operation.SourceResponse
import ddf.catalog.service.ConfiguredService
import ddf.catalog.source.Source
import ddf.catalog.source.SourceMonitor
import ddf.catalog.source.UnsupportedQueryException
import org.apache.cxf.jaxrs.client.WebClient
import org.codice.ddf.admin.common.fields.common.AddressField
import org.codice.ddf.admin.common.fields.common.CredentialsField
import org.codice.ddf.admin.common.fields.common.PidField
import org.codice.ddf.admin.common.fields.common.UrlField
import org.codice.ddf.admin.common.report.ReportImpl
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl
import org.codice.ddf.admin.common.services.ServiceCommons
import org.codice.ddf.admin.configurator.OperationReport
import org.codice.ddf.admin.sources.fields.type.SourceConfigField
import org.codice.ddf.admin.sources.utils.RequestUtils
import org.codice.ddf.cxf.SecureCxfClientFactory
import spock.lang.Specification

import javax.ws.rs.core.Response

class SourceCommonsSpec extends Specification {

    public static final ENDPOINT_URL = SourceConfigField.ENDPOINT_URL_FIELD_NAME

    public static final CREDENTIALS = CredentialsField.DEFAULT_FIELD_NAME

    public static final USERNAME = CredentialsField.USERNAME_FIELD_NAME

    public static final PASSWORD = CredentialsField.PASSWORD_FIELD_NAME

    public static final ADDRESS = AddressField.DEFAULT_FIELD_NAME

    public static final URL_NAME = UrlField.DEFAULT_FIELD_NAME

    public static final PID = PidField.DEFAULT_FIELD_NAME

    public static final FACTORY_PID_KEY = ServiceCommons.FACTORY_PID_KEY

    public static final SERVICE_PID_KEY = ServiceCommons.SERVICE_PID_KEY

    public static final SOURCE_NAME = SourceConfigField.SOURCE_NAME_FIELD_NAME

    public static final ID = 'id'

    public static final FLAG_PASSWORD = ServiceCommons.FLAG_PASSWORD

    public static final TEST_ERROR_CODE = "testErrorCode"

    public static final F_PID = "testFactoryPid"

    public static final S_PID = "testServicePid"

    public static final S_PID_1 = "testServicePid1"

    public static final S_PID_2 = "testServicePid2"

    public static final SOURCE_ID_1 = "testId1"

    public static final SOURCE_ID_2 = "testId2"

    public static final TEST_USERNAME = "admin"

    public static final TEST_PASSWORD = "admin"

    public static final TEST_SOURCENAME = "testSourceName"

    static getBaseDiscoverByAddressArgs() {
        return [
                (ADDRESS)    : new AddressField().hostname('localhost').port(8993).getValue(),
                (CREDENTIALS): new CredentialsField().username(TEST_USERNAME).password(TEST_PASSWORD).getValue()
        ]
    }

    static getBaseDiscoverByUrlArgs(String url) {
        return [
                (ADDRESS)    : new AddressField().url(url).getValue(),
                (CREDENTIALS): new CredentialsField().username(TEST_USERNAME).password(TEST_PASSWORD).getValue()
        ]
    }

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
            (PASSWORD)       : TEST_PASSWORD,
            (ID)             : SOURCE_ID_1,
            (FACTORY_PID_KEY): F_PID,
            (SERVICE_PID_KEY): S_PID,
            (USERNAME)       : TEST_USERNAME
    ]

    static class TestRequestUtils extends RequestUtils {

        SecureCxfClientFactory factory

        boolean endpointIsReachable

        TestRequestUtils(SecureCxfClientFactory mockFactory, boolean isReachable) {
            factory = mockFactory
            endpointIsReachable = isReachable
        }

        @Override
        protected SecureCxfClientFactory<WebClient> createFactory(String url, CredentialsField creds) {
            return factory
        }

        @Override
        public ReportImpl endpointIsReachable(UrlField urlField) {
            def report = new ReportImpl()
            if (!endpointIsReachable) {
                report.addArgumentMessage(new ErrorMessageImpl(TEST_ERROR_CODE))
            }
            return report
        }
    }

    def mockReport(boolean hasError) {
        def report = Mock(OperationReport)
        report.containsFailedResults() >> hasError
        return report
    }

    def createMockFactory(int statusCode, String responseBody) {
        def mockResponse = Mock(Response)
        mockResponse.getStatus() >> statusCode
        mockResponse.readEntity(String.class) >> responseBody

        def mockWebClient = Mock(WebClient)
        mockWebClient.get() >> mockResponse

        def mockFactory = Mock(SecureCxfClientFactory)
        mockFactory.getClient() >> mockWebClient

        return mockFactory
    }

    /**
     * Needed to be able to successfully test the case where a Source is casted to a ConfiguredService
     * for Source availabilities
     */
    static class TestSource implements Source, ConfiguredService {

        boolean availability
        String pid
        String sourceName

        TestSource(String pid, boolean availability) {
            this.pid = pid
            this.sourceName = TEST_SOURCENAME
            this.availability = availability
        }

        TestSource(String pid, String sourceName, boolean availability) {
            this.pid = pid
            this.sourceName = sourceName
            this.availability = availability
        }

        @Override
        String getConfigurationPid() {
            return pid
        }

        @Override
        void setConfigurationPid(String s) {

        }

        @Override
        boolean isAvailable() {
            return availability
        }

        @Override
        boolean isAvailable(SourceMonitor sourceMonitor) {
            return false
        }

        @Override
        SourceResponse query(QueryRequest queryRequest) throws UnsupportedQueryException {
            return null
        }

        @Override
        Set<ContentType> getContentTypes() {
            return null
        }

        @Override
        String getVersion() {
            return null
        }

        @Override
        String getId() {
            return sourceName
        }

        @Override
        String getTitle() {
            return null
        }

        @Override
        String getDescription() {
            return null
        }

        @Override
        String getOrganization() {
            return null
        }
    }
}
