package org.codice.ddf.admin.graphql.test

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import org.boon.Boon
import org.codice.ddf.admin.api.report.ErrorMessage
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.graphql.transform.FunctionDataFetcherException
import spock.lang.Specification

class FunctionDataFetcherExceptionTest extends Specification {

    private static final String HIDDEN_FLAG = "*****"
    FunctionDataFetcherException fetcherException

    def 'Successfully hide passwords in logs'() {
        setup:
        Map given = [
                "password": "admin",
                "username": "admin"
        ]
        Map expected = [
                "password": HIDDEN_FLAG,
                "username": "admin"
        ]
        def expectedResult = Boon.toPrettyJson(ImmutableMap.of("functionName", "updateCswSource", "args", createArgs(expected), "errors", createErrors()))
        fetcherException = new FunctionDataFetcherException("updateCswSource", createArgs(given), createErrors())

        when:
        def result = fetcherException.filterString("updateCswSource", createArgs(given), createErrors())

        then:
        result == expectedResult
    }

    List<Object> createArgs(Map creds) {
        Map args = [
                "cswProfile"        : "null",
                "endpointUrl"       : "null",
                "pid"               : "",
                "cswSpatialOperator": "NO_FILTER",
                "sourceName"        : "",
                "cswOutputSchema"   : "null",
                "creds"             : creds
        ]

        return ImmutableList.of(args)
    }

    List<ErrorMessage> createErrors() {
        return ImmutableList.of(DefaultMessages.cannotConnectError(),
                DefaultMessages.noExistingConfigError(),
                DefaultMessages.failedPersistError(),
                DefaultMessages.unknownEndpointError())
    }
}
