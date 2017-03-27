package org.codice.ddf.admin.graphql;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codice.ddf.admin.api.action.ActionCreator;
import org.codice.ddf.admin.graphql.service.GraphQLProviderImpl;

import graphql.annotations.EnhancedExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.servlet.DefaultGraphQLContextBuilder;
import graphql.servlet.ExecutionStrategyProvider;
import graphql.servlet.GraphQLContext;
import graphql.servlet.GraphQLContextBuilder;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLServlet;
import graphql.servlet.GraphQLVariables;



public class GraphQLServletImpl extends GraphQLServlet {

    private List<ActionCreator> actionCreators = new ArrayList<>();
    private GraphQLContextBuilder contextBuilder = new DefaultGraphQLContextBuilder();
    private ExecutionStrategyProvider executionStrategyProvider = new EnhancedExecutionStrategyProvider();

    private GraphQLSchema schema;
    private GraphQLSchema readOnlySchema;

    public GraphQLServletImpl() {
        updateSchema();
    }

    private void updateSchema() {
        GraphQLObjectType.Builder object = newObject().name("query");

        List<GraphQLProviderImpl> providers = actionCreators.stream()
                .map(GraphQLProviderImpl::new)
                .collect(Collectors.toList());

        for (GraphQLQueryProvider provider : providers) {
            GraphQLObjectType query = provider.getQuery();
            object.field(newFieldDefinition().
                    type(query).
                    staticValue(provider.context()).
                    name(provider.getName()).
                    description(query.getDescription()).
                    build());
        }
        // TODO: tbatie - 3/23/17 - Investigate type providers


//        Set<GraphQLType> types = new HashSet<>();
//        for (GraphQLTypesProvider typesProvider : typesProviders) {
//            types.addAll(typesProvider.getTypes());
//        }
//
//        types.add(() -> "query");
//        readOnlySchema = newSchema().query(object.build()).build(types);

        boolean noMutations = providers.stream()
                .map(GraphQLProviderImpl::getMutations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()).isEmpty();

        if (noMutations) {
            schema = readOnlySchema;
        } else {
            GraphQLObjectType.Builder mutationObject = newObject().name("mutation");

            for (GraphQLMutationProvider provider : providers) {
                provider.getMutations().forEach(mutationObject::field);
            }

            GraphQLObjectType mutationType = mutationObject.build();
            if (mutationType.getFieldDefinitions().size() == 0) {
                schema = readOnlySchema;
            } else {
                schema = newSchema().query(object.build()).mutation(mutationType).build();
            }
        }
    }
    @Override
    protected GraphQLContext createContext(Optional<HttpServletRequest> request,
            Optional<HttpServletResponse> response) {
        return contextBuilder.build(request, response);
    }

    @Override
    protected ExecutionStrategy getExecutionStrategy() {
        return executionStrategyProvider.getExecutionStrategy();
    }

    @Override
    protected Map<String, Object> transformVariables(GraphQLSchema schema, String query,
            Map<String, Object> variables) {
        return new GraphQLVariables(schema, query, variables);
    }

    @Override
    public GraphQLSchema getSchema() {
        return schema;
    }

    @Override
    public GraphQLSchema getReadOnlySchema() {
//        return readOnlySchema;
        return schema;
    }

    public void bindCreator(ActionCreator creator) {
        updateSchema();
    }

    public void unbindCreator(ActionCreator creator) {
        updateSchema();
    }

    public void setActionCreators(List<ActionCreator> actionCreators) {
        this.actionCreators = actionCreators;
        updateSchema();
    }

    public class EnhancedExecutionStrategyProvider implements ExecutionStrategyProvider {
        @Override
        public ExecutionStrategy getExecutionStrategy() {
            return new EnhancedExecutionStrategy();
        }
    }
}
