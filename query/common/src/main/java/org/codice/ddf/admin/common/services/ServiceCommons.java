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
package org.codice.ddf.admin.common.services;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.noExistingConfigError;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang.text.StrSubstitutor;
import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.Events;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.report.ReportImpl;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.OperationReport;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceCommons {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCommons.class);

  public static final String SERVICE_PID_KEY = "service.pid";

  public static final String FACTORY_PID_KEY = "service.factoryPid";

  // A flag to indicate if a service being updated has a password of "secret". If so, the
  // password will not be updated.
  public static final String FLAG_PASSWORD = "secret";

  private final ConfiguratorSuite configuratorSuite;

  public ServiceCommons(ConfiguratorSuite configuratorSuite) {
    this.configuratorSuite = configuratorSuite;
  }

  public String resolveProperty(String str) {
    return StrSubstitutor.replaceSystemProperties(str);
  }

  public List<String> resolveProperties(String... list) {
    return Arrays.stream(list).map(this::resolveProperty).collect(Collectors.toList());
  }

  public ReportImpl createManagedService(Map<String, Object> serviceProps, String factoryPid) {
    ReportImpl report = new ReportImpl();

    Configurator configurator = configuratorSuite.getConfiguratorFactory().getConfigurator();
    configurator.add(configuratorSuite.getManagedServiceActions().create(factoryPid, serviceProps));

    // TODO RAP 13 Jul 17: Blank out password in the parameters passed here
    if (configurator
        .commit("Service saved with details [{}]", serviceProps.toString())
        .containsFailedResults()) {
      report.addResultMessage(failedPersistError());
    }

    return report;
  }

  public ReportImpl updateService(PidField servicePid, Map<String, Object> newConfig) {
    ReportImpl report = new ReportImpl();

    report.addMessages(serviceConfigurationExists(servicePid));
    if (report.containsErrorMsgs()) {
      return report;
    }

    String pid = servicePid.getValue();
    Configurator configurator = configuratorSuite.getConfiguratorFactory().getConfigurator();
    configurator.add(configuratorSuite.getServiceActions().build(pid, newConfig, true));

    // TODO RAP 13 Jul 17: Blank out password in the parameters passed here
    OperationReport operationReport =
        configurator.commit(
            "Updated config with pid [{}] and new service properties [{}]",
            pid,
            newConfig.toString());
    if (operationReport.containsFailedResults()) {
      report.addResultMessage(failedPersistError());
    }

    return report;
  }

  public ReportImpl deleteService(PidField servicePid) {
    ReportImpl report = new ReportImpl();

    Configurator configurator = configuratorSuite.getConfiguratorFactory().getConfigurator();
    configurator.add(configuratorSuite.getManagedServiceActions().delete(servicePid.getValue()));
    if (configurator
        .commit("Deleted service with pid [{}].", servicePid.getValue())
        .containsFailedResults()) {
      report.addResultMessage(failedPersistError());
    }
    return report;
  }

  /**
   * Determines whether the service identified by the {@code servicePid} exists.
   *
   * @param servicePid identifier of the service
   * @return
   */
  public ReportImpl serviceConfigurationExists(PidField servicePid) {
    ReportImpl report = new ReportImpl();
    if (!serviceConfigurationExists(servicePid.getValue())) {
      report.addResultMessage(noExistingConfigError());
    }
    return report;
  }

  /**
   * Checks if the given pid retrieves any properties. If no properties are found or the properties
   * are empty then fail.
   *
   * @param servicePid
   * @return with the serviceExists or not
   */
  public boolean serviceConfigurationExists(String servicePid) {
    return !configuratorSuite.getServiceActions().read(servicePid).isEmpty();
  }

  public static <T> T mapValue(Map<String, Object> props, String key) {
    return props.get(key) == null ? null : (T) props.get(key);
  }

  public static class ServicePropertyBuilder {

    private Map<String, Object> serviceProperties;

    public ServicePropertyBuilder() {
      serviceProperties = new HashMap<>();
    }

    public ServicePropertyBuilder put(String key, Object object) {
      serviceProperties.put(key, object);
      return this;
    }

    public ServicePropertyBuilder putPropertyIfNotNull(String key, Field field) {
      if (field.getValue() != null) {
        put(key, field.getValue());
      }
      return this;
    }

    public Map<String, Object> build() {
      return serviceProperties;
    }
  }

  public static void updateGraphQLSchema(Class clazz, String eventReason) {
    getEventAdmin(clazz).postEvent(getUpdateSchemaEvent(eventReason));
  }

  public static Event getUpdateSchemaEvent(String eventReason) {
    return new Event(Events.REFRESH_SCHEMA, ImmutableMap.of(Events.EVENT_REASON, eventReason));
  }

  public static EventAdmin getEventAdmin(Class clazz) {
    return getBundleContext(clazz)
        .getService(getBundleContext(clazz).getServiceReference(EventAdmin.class));
  }

  public static BundleContext getBundleContext(Class clazz) {
    return FrameworkUtil.getBundle(clazz).getBundleContext();
  }
}
