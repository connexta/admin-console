package org.codice.ddf.admin.graphql.test

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.introspection.IntrospectionQuery
import org.codice.ddf.admin.graphql.GraphQLServletImpl
import spock.lang.Shared
import spock.lang.Specification
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import java.nio.file.Files
import java.nio.file.Paths

class TestGraphQLTransformation extends Specification {

    public static final int STATUS_OK = 200

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    GraphQLServletImpl servlet
    MockHttpServletRequest request
    MockHttpServletResponse response

    def setup() {
        servlet = new GraphQLServletImpl()
        servlet.setFieldProviders(Arrays.asList(new TestFieldProvider()))
        request = new MockHttpServletRequest()
        response = new MockHttpServletResponse()

        String schemaResult = servlet.executeQuery(IntrospectionQuery.INTROSPECTION_QUERY)
        if(!System.getProperty("target.path") != null) {
            Files.write(Paths.get(System.getProperty("target.path"), "schema.json"),
                    schemaResult == null ? "".getBytes() : schemaResult.getBytes())
        }
    }

    def "get graphql schema"() {
        setup:
        request.setPathInfo('/schema.json')

        when:
        servlet.doGet(request, response)

        then:
        response.getStatus() == 200
        getResponseContent().data.__schema != null
    }


//    testProvider {
//        recursion: TestProvider
//        staticField: TestObject {
//            string: String
//		      int: Integer
//            list: [String]
//            enum: Enum
//        },
//        multipleArgs(string, int, list, enum): TestObject
//        requiredArg(arg: !TestObject {
//            string: !String
//		      int: !Integer
//            list: ![!String]
//            enum: !Enum
//        }): TestObjectPayload
//    }
//Test return object, scalar, interface, enum, list from function
//Test obj as arg and return type
//Test object, scalar arg
//Test recursion
//Test error/warning message paths
//Test required
//Test graphql exception is transformed to message
    Map<String, Object> getResponseContent() {
        mapper.readValue(response.getContentAsByteArray(), Map)
    }
}
