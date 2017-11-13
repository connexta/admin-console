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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.karaf.features.Feature;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.DirectoryField;
import org.codice.ddf.admin.query.dev.system.dependency.FeatureUtils;
import org.codice.ddf.admin.query.dev.system.fields.FeatureField;
import org.codice.ddf.admin.query.dev.system.graph.DependencyEdge;
import org.codice.ddf.admin.query.dev.system.graph.FeatureGraphProvider;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.graph.DirectedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateFeatureDependencyGraph extends BaseFunctionField<BooleanField> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateFeatureDependencyGraph.class);

  public static final String DEFAULT_GRAPH_NAME = "featureDependenciesGraph.graphml";
  public static final String FUNCTION_NAME = "createFeatureGraph";
  private static final String DESCRIPTION =
      "Saves a graph called \'"
          + DEFAULT_GRAPH_NAME
          + "\' consisting of bundles and features as the vertices."
          + "By default, the graph save path is under ddf.home. The file format is in graphml. Look here for more information: http://graphml.graphdrawing.org/";

  private static final BooleanField RETURN_TYPE = new BooleanField();
  private static final Set<String> ERROR_CODES =
      ImmutableSet.of(DIRECTORY_DOES_NOT_EXIST, FAILED_PERSIST);
  public static final String SAVE_DIR = "saveDir";

  private DirectoryField saveDir;

  private static final FeatureGraphProvider.FeatureOrBundleVertexAttributeProvider
      FEATURE_BUNDLE_VERTEX_PROV =
          new FeatureGraphProvider.FeatureOrBundleVertexAttributeProvider();
  private FeatureUtils featureUtils;

  // Leaving nodes as Field since they can either be a FeatureField or a BundleField
  private GraphMLExporter<Field, DependencyEdge> exporter;

  public CreateFeatureDependencyGraph(FeatureUtils featureUtils) {
    super(FUNCTION_NAME, DESCRIPTION);
    saveDir = new DirectoryField(SAVE_DIR).validateDirectoryExists();

    this.featureUtils = featureUtils;
    exporter = new GraphMLExporter<>();
    exporter.setVertexAttributeProvider(FEATURE_BUNDLE_VERTEX_PROV);
  }

  @Override
  public BooleanField performFunction() {
    DirectedGraph<Field, DependencyEdge> graph =
        createFeatureDependenciesGraph(featureUtils.getAllFeatures());
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

  private DirectedGraph<Field, DependencyEdge> createFeatureDependenciesGraph(
      List<FeatureField> features) {
    DirectedGraph<Field, DependencyEdge> graph = new DirectedPseudograph<>(DependencyEdge.class);
    FEATURE_BUNDLE_VERTEX_PROV
        .getAttributes()
        .forEach(
            attri ->
                exporter.registerAttribute(
                    attri.getAttriName(), attri.getCategory(), attri.getType()));
    features.forEach(graph::addVertex);

    Map<String, FeatureField> featuresById =
        features.stream().collect(Collectors.toMap(FeatureField::id, f -> f));

    for (FeatureField feature : features) {
      for (String featId : feature.featDeps()) {

        try {
          Feature feat = featureUtils.getFeature(featId);
          if (feat != null && featuresById.containsKey(feat.getId())) {
            graph.addEdge(feature, featuresById.get(feat.getId()), DependencyEdge.create(null));
          } else {
            LOGGER.error(
                "Failed to find feature {} while creating feature dependency graph.", featId);
          }
        } catch (Exception e) {
          LOGGER.error(
              "Failed to find feature {} while creating feature dependency graph.", featId, e);
        }
      }

      feature
          .bundleDeps()
          .forEach(
              dep -> {
                graph.addVertex(dep);
                graph.addEdge(feature, dep, DependencyEdge.create(null));
              });
    }

    return graph;
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
  public FunctionField<BooleanField> newInstance() {
    return new CreateFeatureDependencyGraph(featureUtils);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ERROR_CODES;
  }
}
