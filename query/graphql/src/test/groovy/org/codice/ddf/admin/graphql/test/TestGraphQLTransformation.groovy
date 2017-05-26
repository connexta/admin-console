package org.codice.ddf.admin.graphql.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Charsets
import com.google.common.io.Resources
import graphql.validation.ValidationErrorType
import groovy.json.JsonBuilder
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.api.report.Message.MessageType
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.graphql.GraphQLServletImpl
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Shared
import spock.lang.Specification

class TestGraphQLTransformation extends Specification {

    public static final int STATUS_OK = 200

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    GraphQLServletImpl servlet
    MockHttpServletRequest request
    MockHttpServletResponse response

    final STRING_ARG_VALUE = "STRING_ARG_VAL"
    final INTEGER_ARG_VALUE = 999
    final BOOLEAN_ARG_VALUE = true
    final LIST_ARG_VALUE = ["entry1", "entry2", "entry3"]
    final ENUM_ARG_VALUE = "ENUM_A"

    final List<String> returnErrorMessageFunctionPath = ["testing", "path", "path2", "path3", "returnErrorsAndWarnings"]

    final List<String> requiredArgFunctionPath =  ["testing", "requiredArg"]

    def queryVars =
            [
                    stringArg : STRING_ARG_VALUE,
                    integerArg: INTEGER_ARG_VALUE,
                    booleanArg: BOOLEAN_ARG_VALUE,
                    listArg   : LIST_ARG_VALUE,
                    enumArg   : ENUM_ARG_VALUE
            ]

    def setup() {
        servlet = new GraphQLServletImpl()
        servlet.setFieldProviders(Arrays.asList(new TestFieldProvider()))
        request = new MockHttpServletRequest()
        response = new MockHttpServletResponse()

    }

    def "successfully retrieve graphql schema"() {
        setup:
        request.setPathInfo('/schema.json')

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == 200
        getResponseContent().errors == null
        getResponseContent().warnings == null
        getResponseContent().data.__schema != null
    }


    def "successfully retrieve all supported base field types"() {
        setup:
        request.addParameter('query', getQuery('GetBaseFieldTypesQuery'))

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == 200
        getResponseContent().errors == null
        getResponseContent().warnings == null
        getResponseContent().data ==
                [
                        testing: [
                                getInteger: TestFieldProvider.GetInt.GET_INT_VALUE,
                                getBoolean: TestFieldProvider.GetBoolean.GET_BOOLEAN_VALUE,
                                getString : TestFieldProvider.GetString.GET_STRING_VALUE,
                                getList   : [
                                        TestFieldProvider.GetList.ENTRY_1_VALUE.getValue(),
                                        TestFieldProvider.GetList.ENTRY_2_VALUE.getValue(),
                                        TestFieldProvider.GetList.ENTRY_3_VALUE.getValue()
                                ],
                                getEnum   : TestFieldProvider.GetEnum.GET_ENUM_VALUE
                        ]
                ]

    }

    def "successfully pass all supported base field types as args"() {
        setup:
        request.addParameter('query', getQuery('MultipleArgumentsQuery'))
        request.addParameter('variables', getVariables())

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == 200
        getResponseContent().errors == null
        getResponseContent().warnings == null
        getResponseContent().data ==
                [
                        testing: [
                                multipleArgs: [
                                        string: STRING_ARG_VALUE,
                                        boolean: BOOLEAN_ARG_VALUE,
                                        integer: INTEGER_ARG_VALUE,
                                        list: LIST_ARG_VALUE,
                                        enumeration: ENUM_ARG_VALUE
                                ]
                        ]
                ]

    }

    def "test errors/warning paths and that data can still be returned"() {
        setup:
        request.addParameter('query', getQuery('MessagePathsQuery'))
        request.addParameter('variables', getVariables())

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == 200
        getResponseContent().errors.size() == 14
        getResponseContent().errors as Set == [
                createArgumentError(["testObj"]),
                createArgumentError(["testObj", "integer"]),
                createArgumentError(["testObj", "boolean"]),
                createArgumentError(["testObj", "string"]),
                createArgumentError(["testObj", "enumeration"]),
                createArgumentError(["testObj", "list"]),
                createArgumentError(["testObj", "list", ListField.INDEX_DELIMETER + 0]),
                createReturnValueError([]),
                createReturnValueError(["integer"]),
                createReturnValueError(["boolean"]),
                createReturnValueError(["string"]),
                createReturnValueError(["list"]),
                createReturnValueError(["list", ListField.INDEX_DELIMETER + 0]),
                createReturnValueError(["enumeration"])
        ] as Set

        getResponseContent().warnings == [
                createReturnValueWarning([]),
        ]
        getResponseContent().data ==
                [
                        testing: [
                                path: [
                                    path2: [
                                            path3:[
                                                    returnErrorsAndWarnings: TestObject.createSampleTestObject().getValue()
                                            ]
                                    ]
                                ]

                        ]
                ]
    }

    def "fail to query with missing required field"() {
        setup:
        request.addParameter('query', getQuery('MissingRequiredArgumentQuery'))

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == 200
        getResponseContent().data == null
        getResponseContent().warnings == null
        getResponseContent().errors.get(0).validationErrorType == ValidationErrorType.MissingFieldArgument.name()
    }

    def "fail to query with missing inner required fields of object argument"() {
        setup:
        request.addParameter('query', getQuery('MissingRequiredInnerFieldsArgumentQuery'))
        request.addParameter('variables', getVariables())

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == 200
        getResponseContent().data == [
                testing: [
                        requiredArg: null
                ]
        ]
        getResponseContent().warnings == null
        getResponseContent().errors as Set ==([
                createMissingRequiredFieldError(requiredArgFunctionPath, ["testObj", "integer"]),
                createMissingRequiredFieldError(requiredArgFunctionPath, ["testObj", "boolean"]),
                createMissingRequiredFieldError(requiredArgFunctionPath, ["testObj", "string"]),
                createMissingRequiredFieldError(requiredArgFunctionPath, ["testObj", "enumeration"]),
                createMissingRequiredFieldError(requiredArgFunctionPath, ["testObj", "list"])
        ] as Set)
    }

    def "successfully query with all required fields satisfied and that data is returned correctly"() {
        setup:
        request.addParameter('query', getQuery('SatisfiedRequiredFieldsQuery'))
        request.addParameter('variables', getVariables())

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == 200
        getResponseContent().warnings == null
        getResponseContent().errors == null
        getResponseContent().data ==
                [
                        testing: [
                                requiredArg: [
                                        string     : STRING_ARG_VALUE,
                                        integer    : INTEGER_ARG_VALUE,
                                        boolean    : BOOLEAN_ARG_VALUE,
                                        list       : LIST_ARG_VALUE,
                                        enumeration: ENUM_ARG_VALUE
                                ]
                        ]
                ]
    }

    String toJson(obj) {
        new JsonBuilder(obj).toPrettyString()
    }

    String getVariables() {
        new JsonBuilder(queryVars).toString()
    }

    String getQuery(String queryName) {
        Resources.toString(this.getClass().getResource("/queries/" + queryName), Charsets.UTF_8)
    }

    Map<String, Object> getResponseContent() {
        mapper.readValue(response.getContentAsByteArray(), Map)
    }

    String createPath(String functionPath, String additionalPath, boolean isArg) {
        return isArg ? functionPath + FunctionField.ARGUMENT + additionalPath : functionPath + additionalPath;
    }

    Map<String, Object> createArgumentError(List<String> argPath) {
        return createMessage(MessageType.ERROR, TestFieldProvider.ARGUMENT_MSG, true, returnErrorMessageFunctionPath, argPath)
    }

    Map<String, Object> createReturnValueError(List<String> argPath) {
        return createMessage(MessageType.ERROR, TestFieldProvider.RETURN_VALUE_MSG, false, returnErrorMessageFunctionPath, argPath)
    }

    Map<String, Object> createReturnValueWarning(List<String> argPath) {
        return createMessage(MessageType.WARNING, TestFieldProvider.RETURN_VALUE_MSG, false, returnErrorMessageFunctionPath, argPath)
    }

    Map<String, Object> createMissingRequiredFieldError(List<String> functionPath, List<String> argPath) {
        return createMessage(MessageType.ERROR, DefaultMessages.MISSING_REQUIRED_FIELD, true, functionPath, argPath)
    }

    Map<String, Object> createMessage(MessageType msgType, String code, boolean isArgument, List<String> functionPath, List<String> additionalPath) {
        List<String> path = functionPath;
        if(isArgument) {
            path = path + FunctionField.ARGUMENT
        }

        path = path + additionalPath

        return [
                type: msgType.name(),
                code: code,
                path: path
        ]
    }
}
