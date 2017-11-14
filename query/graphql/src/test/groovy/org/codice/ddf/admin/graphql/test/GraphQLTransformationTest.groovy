package org.codice.ddf.admin.graphql.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.google.common.net.HttpHeaders
import graphql.validation.ValidationErrorType
import groovy.json.JsonBuilder
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField
import org.codice.ddf.admin.common.fields.base.scalar.StringField
import org.codice.ddf.admin.common.fields.test.TestEnumField
import org.codice.ddf.admin.common.fields.test.TestFieldProvider
import org.codice.ddf.admin.common.fields.test.TestHiddenField
import org.codice.ddf.admin.common.fields.test.TestObjectField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.graphql.servlet.ExtendedOsgiGraphQLServlet
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Shared
import spock.lang.Specification

class GraphQLTransformationTest extends Specification {

    static STATUS_OK = 200

    static STATUS_INVALID_REQUEST = 400

    static MISSING_CONTENT_LENGTH = 411

    static PAYLOAD_TOO_LARGE = 413

    static TEST_OBJECT_NAME = TestObjectField.FIELD_NAME

    static STRING_ARG_VALUE = TestObjectField.SAMPLE_STRING_VALUE

    static INTEGER_ARG_VALUE = TestObjectField.SAMPLE_INTEGER_VALUE

    static BOOLEAN_ARG_VALUE = TestObjectField.SAMPLE_BOOLEAN_VALUE

    static LIST_ARG_VALUE = TestObjectField.SAMPLE_LIST_VALUE

    static ENUM_ARG_VALUE = TestEnumField.EnumA.ENUM_A

    static HIDDEN_FIELD_ARG_VALUE = TestObjectField.SAMPLE_HIDDEN_FIELD_VALUE

    static HIDDEN_FIELD_RETURN_VALUE = TestHiddenField.HIDDEN_FLAG

    static INNER_OBJECT_ARG_VALUE = [
            (TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME): TestObjectField.InnerTestObjectField.TEST_VALUE,
            (TestObjectField.HIDDEN_FIELD_NAME)            : TestObjectField.InnerTestObjectField.TEST_HIDDEN_FIELD_VALUE
    ]

    static INNER_OBJECT_RETURN_VALUE = [
            (TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME): TestObjectField.InnerTestObjectField.TEST_VALUE,
            (TestObjectField.HIDDEN_FIELD_NAME)            : HIDDEN_FIELD_RETURN_VALUE
    ]

    static STRING = StringField.DEFAULT_STRING_FIELD_NAME

    static BOOLEAN = BooleanField.DEFAULT_BOOLEAN_FIELD_NAME

    static INTEGER = IntegerField.DEFAULT_INTEGER_FIELD_NAME

    static LIST = TestFieldProvider.LIST_FIELD_NAME

    static ENUMERATION = TestEnumField.DEFAULT_FIELD_NAME

    static INNER_OBJECT = TestObjectField.INNER_OBJECT_FIELD_NAME

    static HIDDEN_FIELD = TestHiddenField.HIDDEN_FIELD_NAME

    static FUNCTION_NAME = TestFieldProvider.TEST_FUNCTION_NAME

    static GRAPHQL_QUERY = 'query'

    static GRAPHQL_VARIABLES = 'variables'

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    ExtendedOsgiGraphQLServlet servlet

    MockHttpServletRequest request

    MockHttpServletResponse response

    final List<String> returnErrorMessageFunctionPath =
            [FUNCTION_NAME, TestFieldProvider.PATH_1, TestFieldProvider.PATH_2, TestFieldProvider.PATH_3, TestFieldProvider.RETURN_ERRORS_FUNCTION_NAME]

    final List<String> requiredArgFunctionPath = [FUNCTION_NAME, TestFieldProvider.REQUIRED_ARG_FUNCTION_NAME]

    def queryVars =
            [
                    stringArg     : STRING_ARG_VALUE,
                    integerArg    : INTEGER_ARG_VALUE,
                    booleanArg    : BOOLEAN_ARG_VALUE,
                    listArg       : LIST_ARG_VALUE,
                    enumArg       : ENUM_ARG_VALUE,
                    innerObjectArg: INNER_OBJECT_ARG_VALUE,
                    hiddenFieldArg: HIDDEN_FIELD_ARG_VALUE
            ]

    def setup() {
        servlet = new ExtendedOsgiGraphQLServlet()
        servlet.setFieldProviders([new TestFieldProvider()])
        servlet.refreshSchema()
        request = new MockHttpServletRequest()
        response = new MockHttpServletResponse()
    }

    def 'successfully retrieve GraphQL schema'() {
        setup:
        request.setPathInfo('/schema.json')

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsMap().errors == null
        getResponseContentAsMap().data.__schema != null
    }


    def 'successfully retrieve all supported base field types'() {
        setup:
        request.addParameter(GRAPHQL_QUERY, getQuery('GetBaseFieldTypesQuery'))

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsMap().errors == null
        getResponseContentAsMap().data ==
                [
                        (FUNCTION_NAME): [
                                (TestFieldProvider.GET_INT_FUNCTION_NAME)   : TestFieldProvider.GetInt.GET_INT_VALUE,
                                (TestFieldProvider.GET_BOOL_FUNCTION_NAME)  : TestFieldProvider.GetBoolean.GET_BOOLEAN_VALUE,
                                (TestFieldProvider.GET_STRING_FUNCTION_NAME): TestFieldProvider.GetString.GET_STRING_VALUE,
                                (TestFieldProvider.GET_LIST_FUNCTION_NAME)  : [
                                        TestFieldProvider.GetList.ENTRY_1_VALUE.getSanitizedValue(),
                                        TestFieldProvider.GetList.ENTRY_2_VALUE.getSanitizedValue(),
                                        TestFieldProvider.GetList.ENTRY_3_VALUE.getSanitizedValue()
                                ],
                                (TestFieldProvider.GET_ENUM_FUNCTION_NAME)  : TestFieldProvider.GetEnum.GET_ENUM_VALUE
                        ]
                ]
    }

    def 'successfully pass all supported base field types as args'() {
        setup:
        request.addParameter(GRAPHQL_QUERY, getQuery('MultipleArgumentsQuery'))
        request.addParameter(GRAPHQL_VARIABLES, getVariables())

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsMap().errors == null
        getResponseContentAsMap().data ==
                [
                        (FUNCTION_NAME): [
                                (TestFieldProvider.MULTIPLE_ARGS_FUNCTION_NAME): [
                                        (STRING)     : STRING_ARG_VALUE,
                                        (BOOLEAN)    : BOOLEAN_ARG_VALUE,
                                        (INTEGER)    : INTEGER_ARG_VALUE,
                                        (LIST)       : LIST_ARG_VALUE,
                                        (ENUMERATION): ENUM_ARG_VALUE
                                ]
                        ]
                ]
    }

    def 'Data is not returned when there are errors'() {
        setup:
        request.addParameter(GRAPHQL_QUERY, getQuery('MessagePathsQuery'))
        request.addParameter(GRAPHQL_VARIABLES, getVariables())

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsMap().errors.size() == 7
        getResponseContentAsMap().errors as Set == [
                createError([TEST_OBJECT_NAME]),
                createError([TEST_OBJECT_NAME, INTEGER]),
                createError([TEST_OBJECT_NAME, BOOLEAN]),
                createError([TEST_OBJECT_NAME, STRING]),
                createError([TEST_OBJECT_NAME, ENUMERATION]),
                createError([TEST_OBJECT_NAME, LIST]),
                createError([TEST_OBJECT_NAME, LIST, 0])
        ] as Set

        getResponseContentAsMap().data ==
                [
                        (FUNCTION_NAME): [
                                (TestFieldProvider.PATH_1): [
                                        (TestFieldProvider.PATH_2): [
                                                (TestFieldProvider.PATH_3): [
                                                        (TestFieldProvider.RETURN_ERRORS_FUNCTION_NAME): null
                                                ]
                                        ]
                                ]

                        ]
                ]
    }

    def 'fail to query with missing required field'() {
        setup:
        request.addParameter(GRAPHQL_QUERY, getQuery('MissingRequiredArgumentQuery'))

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsMap().data == null
        getResponseContentAsMap().errors.get(0).validationErrorType == ValidationErrorType.MissingFieldArgument.name()
    }

    def 'fail to query with missing inner required fields of object argument'() {
        setup:
        request.addParameter(GRAPHQL_QUERY, getQuery('MissingRequiredInnerFieldsArgumentQuery'))
        request.addParameter(GRAPHQL_VARIABLES, getVariables())

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsMap().data == [
                (FUNCTION_NAME): [
                        (TestFieldProvider.REQUIRED_ARG_FUNCTION_NAME): null
                ]
        ]
        getResponseContentAsMap().errors as Set == ([
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, INTEGER]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, BOOLEAN]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, STRING]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, ENUMERATION]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, LIST]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, INNER_OBJECT, TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, INNER_OBJECT, HIDDEN_FIELD]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, HIDDEN_FIELD]),
        ] as Set)
    }

    def 'successfully query with all required fields satisfied and that data is returned correctly'() {
        setup:
        request.addParameter(GRAPHQL_QUERY, getQuery('SatisfiedRequiredFieldsQuery'))
        request.addParameter(GRAPHQL_VARIABLES, getVariables())

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsMap().errors == null
        getResponseContentAsMap().data ==
                [
                        (FUNCTION_NAME): [
                                (TestFieldProvider.REQUIRED_ARG_FUNCTION_NAME): [
                                        (STRING)      : STRING_ARG_VALUE,
                                        (INTEGER)     : INTEGER_ARG_VALUE,
                                        (BOOLEAN)     : BOOLEAN_ARG_VALUE,
                                        (LIST)        : LIST_ARG_VALUE,
                                        (ENUMERATION) : ENUM_ARG_VALUE,
                                        (INNER_OBJECT): INNER_OBJECT_RETURN_VALUE,
                                        (HIDDEN_FIELD): HIDDEN_FIELD_RETURN_VALUE
                                ]
                        ]
                ]
    }

    def "batched request with 1 query works"() {
        setup:

        def query = [
                query: getQuery('SatisfiedRequiredFieldsQuery'),
                variables: getVariables()
        ]

        def reqContent = toJson([query]).bytes
        request.setContent(reqContent)
        request.addHeader(HttpHeaders.CONTENT_LENGTH, reqContent.size())

        when:
        servlet.doPost(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsList()[0].errors == null
        getResponseContentAsList()[0].data == [
                (FUNCTION_NAME): [
                        (TestFieldProvider.REQUIRED_ARG_FUNCTION_NAME): [
                                (STRING)      : STRING_ARG_VALUE,
                                (INTEGER)     : INTEGER_ARG_VALUE,
                                (BOOLEAN)     : BOOLEAN_ARG_VALUE,
                                (LIST)        : LIST_ARG_VALUE,
                                (ENUMERATION) : ENUM_ARG_VALUE,
                                (INNER_OBJECT): INNER_OBJECT_RETURN_VALUE,
                                (HIDDEN_FIELD): HIDDEN_FIELD_RETURN_VALUE
                        ]
                ]
        ]
    }

    def "batched request with 1 valid query and 1 invalid query"() {
        setup:

        def goodQuery = [
                query: getQuery('SatisfiedRequiredFieldsQuery'),
                variables: getVariables()
        ]

        def badQuery = [
                query: getQuery('MissingRequiredInnerFieldsArgumentQuery'),
                variables: getVariables()
        ]

        def reqContent = toJson([goodQuery, badQuery]).bytes
        request.setContent(reqContent)
        request.addHeader(HttpHeaders.CONTENT_LENGTH, reqContent.size())

        when:
        servlet.doPost(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsList()[0].errors == null
        getResponseContentAsList()[0].data == [
                (FUNCTION_NAME): [
                        (TestFieldProvider.REQUIRED_ARG_FUNCTION_NAME): [
                                (STRING)      : STRING_ARG_VALUE,
                                (INTEGER)     : INTEGER_ARG_VALUE,
                                (BOOLEAN)     : BOOLEAN_ARG_VALUE,
                                (LIST)        : LIST_ARG_VALUE,
                                (ENUMERATION) : ENUM_ARG_VALUE,
                                (INNER_OBJECT): INNER_OBJECT_RETURN_VALUE,
                                (HIDDEN_FIELD): HIDDEN_FIELD_RETURN_VALUE
                        ]
                ]
        ]

        getResponseContentAsList()[1].errors as Set == ([
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, INTEGER]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, BOOLEAN]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, STRING]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, ENUMERATION]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, LIST]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, INNER_OBJECT, TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, INNER_OBJECT, TestObjectField.HIDDEN_FIELD_NAME]),
                createMissingRequiredFieldError(requiredArgFunctionPath, [TEST_OBJECT_NAME, HIDDEN_FIELD])
        ] as Set)

        getResponseContentAsList()[1].data == [
                testing: [
                        requiredArg: null
                ]
        ]
    }

    def 'successfully retrieve error codes without any errors'() {
        setup:
        request.addParameter(GRAPHQL_QUERY, getQuery('GetErrorCodes'))

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == STATUS_OK
        getResponseContentAsMap().errors == null
    }

    def 'fail when content-length exceeds allowed amount'() {
        setup:
        request.setContent(toJson([query: ""]).bytes)
        request.addHeader(HttpHeaders.CONTENT_LENGTH, 1_000_001)

        when:
        servlet.doPost(request, response)

        then:
        response.getStatus() == PAYLOAD_TOO_LARGE
    }

    def 'fail when content-length is invalid'() {
        setup:
        request.setContent(toJson([query: ""]).bytes)
        request.addHeader(HttpHeaders.CONTENT_LENGTH, "INVALID_VALUE")

        when:
        servlet.doPost(request, response)

        then:
        response.getStatus() == STATUS_INVALID_REQUEST
    }

    def 'fail when content-length is absent'() {
        setup:
        request.setContent(toJson([query: ""]).bytes)

        when:
        servlet.doPost(request, response)

        then:
        response.getStatus() == MISSING_CONTENT_LENGTH
    }

    def getResponseContentAsMap() {
        mapper.readValue(response.getContentAsByteArray(), Map)
    }

    List getResponseContentAsList() {
        mapper.readValue(response.getContentAsByteArray(), List)
    }

    def toJson(obj) {
        new JsonBuilder(obj).toPrettyString()
    }

    def getVariables() {
        new JsonBuilder(queryVars).toString()
    }

    def getQuery(String queryName) {
        return Resources.toString(this.getClass().getResource('/queries/' + queryName), Charsets.UTF_8)
    }

    def createPath(String functionPath, String additionalPath, boolean isArg) {
        return isArg ? functionPath + additionalPath : functionPath + additionalPath;
    }

    def createError(List<String> argPath) {
        return createMessage(TestFieldProvider.ERROR_MSG, returnErrorMessageFunctionPath, argPath)
    }

    def createMissingRequiredFieldError(List<String> functionPath, List<String> argPath) {
        return createMessage(DefaultMessages.MISSING_REQUIRED_FIELD, functionPath, argPath)
    }

    def createMessage(String code, List<String> functionPath, List<String> additionalPath) {
        List<String> path = functionPath + additionalPath

        return [
                path: path,
                message: code,
                "locations": [],
                "errorType": "DataFetchingException",
                "extensions": null
        ]
    }
}
