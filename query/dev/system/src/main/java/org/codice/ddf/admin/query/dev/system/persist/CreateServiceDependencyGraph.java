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
package org.codice.ddf.admin.query.dev.system.persist;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.DIRECTORY_DOES_NOT_EXIST;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.FAILED_PERSIST;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.DirectoryField;
import org.codice.ddf.admin.query.dev.system.dependency.BundleUtils;
import org.codice.ddf.admin.query.dev.system.fields.BundleField;
import org.codice.ddf.admin.query.dev.system.fields.ServiceField;
import org.codice.ddf.admin.query.dev.system.fields.ServiceReferenceField;
import org.codice.ddf.admin.query.dev.system.fields.ServiceReferenceListField;
import org.codice.ddf.admin.query.dev.system.graph.BundleGraphProvider;
import org.codice.ddf.admin.query.dev.system.graph.DependencyEdge;
import org.codice.ddf.admin.query.dev.system.graph.ServiceReferenceGraphProvider;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.graph.DirectedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateServiceDependencyGraph extends BaseFunctionField<BooleanField> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateServiceDependencyGraph.class);

  public static final String DEFAULT_GRAPH_NAME = "serviceDependenciesGraph.graphml";

  public static final String FUNCTION_NAME = "createServiceDependenciesGraph";
  private static final String DESCRIPTION =
      "Saves a graph called \'"
          + DEFAULT_GRAPH_NAME
          + "\' consisting of bundles as the vertices and services as edges."
          + "By default, the graph save path is under ddf.home. The file format is in graphml. Look here for more information: http://graphml.graphdrawing.org/";

  private static final BooleanField RETURN_TYPE = new BooleanField();
  private static final Set<String> ERROR_CODES =
      ImmutableSet.of(DIRECTORY_DOES_NOT_EXIST, FAILED_PERSIST);

  public static final String SAVE_DIR = "saveDir";

  private static final BundleGraphProvider.BundleVertexAttributeProvider BUNDLE_VERTEX_PROV =
      new BundleGraphProvider.BundleVertexAttributeProvider();
  private static final ServiceReferenceGraphProvider.ServiceReferenceEdgeAttributeProvider
      SERVICE_EDGE_PROV = new ServiceReferenceGraphProvider.ServiceReferenceEdgeAttributeProvider();

  private GraphMLExporter<BundleField, DependencyEdge<ServiceReferenceField>> exporter;
  private BundleUtils bundleUtils;
  private DirectoryField saveDir;

  public CreateServiceDependencyGraph(BundleUtils bundleUtils) {
    super(FUNCTION_NAME, DESCRIPTION);
    saveDir = new DirectoryField(SAVE_DIR).validateDirectoryExists();

    this.bundleUtils = bundleUtils;
    exporter = new GraphMLExporter<>();
    exporter.setVertexAttributeProvider(BUNDLE_VERTEX_PROV);
    exporter.setEdgeAttributeProvider(SERVICE_EDGE_PROV);
    Stream.concat(
            BUNDLE_VERTEX_PROV.getAttributes().stream(), SERVICE_EDGE_PROV.getAttributes().stream())
        .forEach(
            attri ->
                exporter.registerAttribute(
                    attri.getAttriName(), attri.getCategory(), attri.getType()));
  }

  @Override
  public BooleanField performFunction() {
    DirectedGraph<BundleField, DependencyEdge<ServiceReferenceField>> graph =
        createServiceDepsGraph(bundleUtils.getAllBundleFields());
    String savePath =
        saveDir.getValue() == null ? System.getProperty("ddf.home") : saveDir.getValue();

    try {
      exporter.exportGraph(graph, Paths.get(savePath, DEFAULT_GRAPH_NAME).toFile());
    } catch (Exception e) {
      LOGGER.error("Failed to export features graph.", e);
      addErrorMessage(failedPersistError());
    }
    return new BooleanField(!containsErrorMsgs());
  }

  private DirectedGraph<BundleField, DependencyEdge<ServiceReferenceField>> createServiceDepsGraph(
      List<BundleField> bundles) {
    DirectedGraph graph = new DirectedPseudograph<>(ServiceReferenceField.class);
    bundles.forEach(graph::addVertex);
    bundles.forEach(bundle -> populateGraphWithServices(bundle, graph, bundles));
    return graph;
  }

  private void populateGraphWithServices(
      BundleField bundle, DirectedGraph graph, List<BundleField> allBundles) {
    bundle.serviceRefs().forEach(ref -> createEdgeFromServiceRef(bundle, ref, graph, allBundles));

    for (ServiceReferenceListField refList : bundle.serviceRefLists()) {
      for (ServiceField service : refList.services()) {
        ServiceReferenceField tempServRef =
            new ServiceReferenceField()
                .serviceInterface(refList.referenceListInterface())
                .filter(refList.filter())
                .resolution(refList.resolution())
                .service(service);

        createEdgeFromServiceRef(bundle, tempServRef, graph, allBundles);
      }
    }
  }

  private void createEdgeFromServiceRef(
      BundleField source,
      ServiceReferenceField edge,
      DirectedGraph graph,
      List<BundleField> allBundles) {
    Optional<BundleField> bf = BundleUtils.getBundleById(allBundles, edge.service().bundleId());
    if (bf.isPresent()) {
      graph.addEdge(source, bf.get(), DependencyEdge.create(edge));
    } else {
      LOGGER.warn(
          "Unable to find bundle for bundle id {} when creating service dependency graph.",
          edge.service().bundleId());
    }
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ERROR_CODES;
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(saveDir);
  }

  @Override
  public BooleanField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public FunctionField newInstance() {
    return new CreateServiceDependencyGraph(bundleUtils);
  }
}
