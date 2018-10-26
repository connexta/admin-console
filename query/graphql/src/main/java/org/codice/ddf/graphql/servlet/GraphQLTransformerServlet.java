/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.graphql.servlet;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalNotification;
import graphql.servlet.GraphQLProvider;
import graphql.servlet.OsgiGraphQLHttpServlet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.codice.ddf.admin.api.Events;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.graphql.transform.GraphQLTransformCommons;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphQLTransformerServlet extends OsgiGraphQLHttpServlet implements EventHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLTransformerServlet.class);
  private static final long CACHE_EXPIRATION_IN_SECONDS = 1;
  private static final long CACHE_CLEANUP_INVOCATION_IN_SECONDS = 1;

  private static final String BINDING_FIELD_PROVIDER = "GraphQL servlet binding field provider %s";
  private static final String UNBINDING_FIELD_PROVIDER =
      "GraphQL servlet unbinding field provider %s";

  private final Cache<String, Object> cache;
  private final ScheduledExecutorService scheduler;
  private GraphQLProvider graphQLProvider;
  private List<FieldProvider> fieldProviders;

  public GraphQLTransformerServlet() {
    super();
    setExecutionStrategyProvider(new ExecutionStrategyProviderImpl());
    setErrorHandler(new GraphQLErrorHandlerImpl());
    setInstrumentationProvider(new QueryValidationInstrumentationProvider());

    cache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(CACHE_EXPIRATION_IN_SECONDS, TimeUnit.SECONDS)
            .removalListener(this::refreshSchemaOnExpire)
            .build();

    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(
        cache::cleanUp,
        CACHE_CLEANUP_INVOCATION_IN_SECONDS,
        CACHE_CLEANUP_INVOCATION_IN_SECONDS,
        TimeUnit.SECONDS);
  }

  @Override
  public void destroy() {
    scheduler.shutdownNow();
  }

  @Override
  public void handleEvent(Event event) {
    if (Events.REFRESH_SCHEMA.equals(event.getTopic())) {
      triggerSchemaRefresh((String) event.getProperty(Events.EVENT_REASON));
    }
  }

  private void triggerSchemaRefresh(String refreshReason) {
    LOGGER.trace("GraphQL schema refresh requested. Cause: {}", refreshReason);
    cache.put(Events.REFRESH_SCHEMA, true);
  }

  /**
   * Refreshes the schema periodically once the cache invalidates if a REFRESH_SCHEMA event was
   * added to the cache. This allows multiple threads to ask for a schema refresh while only
   * refreshing the schema once.
   *
   * @param notification
   */
  private void refreshSchemaOnExpire(RemovalNotification notification) {
    if (notification.getCause() == RemovalCause.EXPIRED) {
      refreshSchema();
    }
  }

  // Synchronized just in case the schema is still updating when another refresh is called
  // The performance decrease by the `synchronized` is negligible because of the periodic cache
  // invalidation implementation
  private synchronized void refreshSchema() {
    LOGGER.trace("Refreshing GraphQL schema.");
    if (graphQLProvider != null) {
      unbindProvider(graphQLProvider);
    }
    graphQLProvider = GraphQLTransformCommons.createGraphQLProvider(fieldProviders);
    bindProvider(graphQLProvider);
    LOGGER.trace("Finished refreshing GraphQL schema.");
  }

  public void bindFieldProvider(FieldProvider fieldProvider) {
    triggerSchemaRefresh(String.format(BINDING_FIELD_PROVIDER, fieldProvider.getFieldType()));
  }

  public void unbindFieldProvider(FieldProvider fieldProvider) {
    triggerSchemaRefresh(
        String.format(
            UNBINDING_FIELD_PROVIDER, fieldProvider == null ? "" : fieldProvider.getFieldType()));
  }

  public void setFieldProviders(List<FieldProvider> fieldProviders) {
    this.fieldProviders = fieldProviders;
    triggerSchemaRefresh("Initializing graphQLServlet.");
  }
}
