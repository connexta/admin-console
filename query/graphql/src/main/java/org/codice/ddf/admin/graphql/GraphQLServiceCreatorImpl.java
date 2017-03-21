package org.codice.ddf.admin.graphql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.codice.ddf.admin.api.action.ActionCreator;
import org.codice.ddf.security.common.Security;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import ddf.security.Subject;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class GraphQLServiceCreatorImpl implements GraphQLServiceCreator {

    private String[] GRAPHQL_INTERFACES = new String[] {GraphQLQueryProvider.class.getName(),
            GraphQLMutationProvider.class.getName()};
    private List<GraphQLQueryProvider> graphqlProviders;

    private Map<String, ServiceRegistration> createdServices;

    public GraphQLServiceCreatorImpl() {
        createdServices = new HashMap<>();
    }

    public void bindCreator(ActionCreator creator) {
        try {
            System.out.println("Binding creator: " + creator.name());
            ServiceRegistration registration = executeAsSystem(() -> getBundleContext().registerService(GRAPHQL_INTERFACES, new GraphQLProviderImpl(creator), null));
            createdServices.put(creator.name(), registration);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void unbindCreator(ActionCreator creator) {
        try {
            System.out.println("Unbinding creator: " + (creator == null ? "null" : creator.name()));
            executeAsSystem(() -> {
                createdServices.get(creator.name()).unregister();
                return true;
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private <T> T executeAsSystem(Callable<T> func) {
        Subject systemSubject = Security.runAsAdmin(() -> Security.getInstance()
                .getSystemSubject());
        if (systemSubject == null) {
            System.out.println("Could not get system user to create managed services");
            throw new RuntimeException("Could not get system user to create managed services");
        }
        return systemSubject.execute(func);
    }

    public BundleContext getBundleContext(){
        return FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
    }

    public void setGraphqlProviders(List<GraphQLQueryProvider> graphqlProviders) {
        this.graphqlProviders = graphqlProviders;
    }
}
