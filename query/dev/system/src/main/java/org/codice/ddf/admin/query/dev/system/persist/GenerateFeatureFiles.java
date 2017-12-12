package org.codice.ddf.admin.query.dev.system.persist;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.common.DirectoryField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.query.dev.system.dependency.BundleUtils;
import org.codice.ddf.admin.query.dev.system.fields.BundleField;
import org.codice.ddf.admin.query.dev.system.fields.ServiceField;
import org.codice.ddf.admin.query.dev.system.fields.ServiceReferenceField;
import org.codice.ddf.admin.query.dev.system.fields.ServiceReferenceListField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class GenerateFeatureFiles extends BaseFunctionField<BooleanField> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateFeatureFiles.class);

  public static final String DEFAULT_PKG_FEATURE_FILE_NAME = "pkg-dependencies-feature.xml";
  public static final String DEFAULT_SERVICE_FEATURE_FILE_NAME = "feature.xml";

  public static final String FUNCTION_NAME = "generateFeatures";
  public static final String DESCRIPTION = "TODO";
  private static final BooleanField RETURN_TYPE = new BooleanField();
  private static final Set<String> ERROR_CODES = ImmutableSet.of(DefaultMessages.FAILED_PERSIST);

  private static final String PKG_DEP_FEATURE_DESCRIPTION = "Auto generated feature using package dependencies of bundle at runtime.";

  private static final String SERVICE_DEP_FEATURE_DESCRIPTION = "Auto generated feature using service dependencies of bundle at runtime.";

  private static final String PKG_DEP_FEATURE_NAME_EXTENSION = "-pkg-dependencies";
  private static final String SERVICE_DEP_FEATURE_NAME_EXTENSION = "";

  public static final String SAVE_DIR = "saveDir";

  private DirectoryField saveDir;
  private IntegerField.ListImpl bundleIds;
  private BundleUtils bundleUtils;
  private Map<Integer, BundleField> allBundles;

  public GenerateFeatureFiles(BundleUtils bundleUtils) {
    super(FUNCTION_NAME, DESCRIPTION);
    this.bundleUtils = bundleUtils;
    bundleIds = new IntegerField.ListImpl();
    saveDir = new DirectoryField(SAVE_DIR).validateDirectoryExists();
  }

  boolean excludeThirdParty = false;
  boolean excludeDdf = false;
  boolean excludeOptionalServices = false;
  boolean createUberThirdPartyFeature = true;

  @Override
  public BooleanField performFunction() {
    allBundles =
        bundleUtils
            .getAllBundleFields()
            .stream()
            .collect(Collectors.toMap(BundleField::id, bundle -> bundle));

    List<FeatureToPrint> pkgDepFeatures =
        allBundles
            .values()
            .stream()
            .map(this::createFeatureFromPkgDeps)
            .collect(Collectors.toList());

    List<FeatureToPrint> serviceDepFeatures =
        allBundles
            .values()
            .stream()
            .map(this::createFeatureFromServiceDeps)
            .filter(feat -> !feat.serviceRefDepFeaturesNames().isEmpty() || !feat.serviceRefListDepFeaturesNames().isEmpty() || !feat.originalBundle().services().isEmpty())
            .collect(Collectors.toList());

    String pkgFeatureFile = FeatureToPrint.createFeatureFile(pkgDepFeatures);
    String serviceFeatureFile = FeatureToPrint.createFeatureFile(serviceDepFeatures);

    String savePath = saveDir.getValue() == null ? System.getProperty("ddf.home") : saveDir.getValue();

    try {
      Files.write(Paths.get(savePath, DEFAULT_PKG_FEATURE_FILE_NAME), pkgFeatureFile.getBytes());
      Files.write(Paths.get(savePath, DEFAULT_SERVICE_FEATURE_FILE_NAME), serviceFeatureFile.getBytes());
    } catch(Exception e) {
      LOGGER.error("Failed to save feature file.", e);
      addErrorMessage(failedPersistError());
    }

    return new BooleanField(!containsErrorMsgs());
  }

  public FeatureToPrint createFeatureFromPkgDeps(BundleField bundleField) {
    List<String> importFeatureNames = bundleField
        .importedPackages()
        .stream()
        .map(pkg -> allBundles.get(pkg.bundleId()))
        .filter(this::includeBundle)
        .map(GenerateFeatureFiles::getPkgDependencyFeatureName)
        .collect(Collectors.toList());

    return FeatureToPrint.newFeature(getPkgDependencyFeatureName(bundleField), PKG_DEP_FEATURE_DESCRIPTION, bundleField)
        .startBundle(bundleField.location())
        .pkgDepFeatures(importFeatureNames);
  }

  public FeatureToPrint createFeatureFromServiceDeps(BundleField bundleField) {

    List<String> serviceRefFeatureNames =
        bundleField
            .serviceRefs()
            .stream()
            .filter(this::includeServiceRef)
            .map(ServiceReferenceField::service)
            .filter(this::includeService)
            .map(ser -> allBundles.get(ser.bundleId()))
            .map(GenerateFeatureFiles::getServiceFeatureName)
            .collect(Collectors.toList());

    List<String> serviceRefListFeatureNames = bundleField
            .serviceRefLists()
            .stream()
            .filter(this::includeServiceListRef)
            .map(ServiceReferenceListField::services)
            .flatMap(List::stream)
            .filter(this::includeService)
            .map(ser -> allBundles.get(ser.bundleId()))
            .map(GenerateFeatureFiles::getServiceFeatureName)
            .collect(Collectors.toList());

    return FeatureToPrint.newFeature(getServiceFeatureName(bundleField), SERVICE_DEP_FEATURE_DESCRIPTION, bundleField)
        .startFeature(getPkgDependencyFeatureName(bundleField))
        .serviceRefDepFeatures(serviceRefFeatureNames)
        .serviceRefListFeatures(serviceRefListFeatureNames);
  }

  public boolean includeBundle(BundleField bundleField){
    if(excludeThirdParty && (!bundleField.location().contains("ddf") || !bundleField.location().contains("codice"))) {
      return false;
    } else {
      return true;
    }
  }

  public boolean includeService(ServiceField service) {
    BundleField bundleOfService = allBundles.get(service);
    if(excludeThirdParty && (!bundleOfService.location().contains("ddf") || !bundleOfService.location().contains("codice"))) {
      return false;
    } else {
      return true;
    }
  }

  public boolean includeServiceRef(ServiceReferenceField ref) {
    if(excludeOptionalServices && !ref.isRequired()) {
      return false;
    } else {
      return true;
    }
  }

  public boolean includeServiceListRef(ServiceReferenceListField ref) {
    if(excludeOptionalServices && !ref.isRequired()) {
      return false;
    } else {
      return true;
    }
  }

  public static String getPkgDependencyFeatureName(BundleField bundleField) {
    return bundleField.bundleName() + PKG_DEP_FEATURE_NAME_EXTENSION;
  }

  public static String getServiceFeatureName(BundleField bundleField) {
    return bundleField.bundleName() + SERVICE_DEP_FEATURE_NAME_EXTENSION;
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ERROR_CODES;
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(saveDir, bundleIds);
  }

  @Override
  public BooleanField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new GenerateFeatureFiles(bundleUtils);
  }

  public static class FeatureToPrint {

    private static final String FEATURE_FILE_HEADER = "<features name=\"${project.artifactId}-${project.version}\"\n"
            + "          xmlns=\"http://karaf.apache.org/xmlns/features/v1.3.0\"\n"
            + "          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "          xsi:schemaLocation=\"http://karaf.apache.org/xmlns/features/v1.3.0 http://karaf.apache.org/xmlns/features/v1.3.0\">\n\n";

    private static final String FEATURE_FILE_FOOTER = "\n</features>";

    private String name;
    private String version;
    private String install;
    private String description;
    private BundleField originalBundle;
    private List<String> bundles;
    private List<String> features;
    private List<String> pkgDepFeatNames;
    private List<String> serviceRefDepFeaturesNames;
    private List<String> serviceRefListDepFeaturesNames;

    public FeatureToPrint(String name, String description, String version, String install, BundleField originalBundle) {
      this.name = name;
      this.description = description;
      this.version = version;
      this.install = install;
      this.originalBundle = originalBundle;
      bundles = new ArrayList<>();
      features = new ArrayList<>();
      pkgDepFeatNames = new ArrayList<>();
      serviceRefDepFeaturesNames = new ArrayList<>();
      serviceRefListDepFeaturesNames = new ArrayList<>();
    }

    public FeatureToPrint startBundle(String bundleLocation) {
      bundles.add(bundleLocation);
      return this;
    }

    public FeatureToPrint startFeature(String featureName) {
      features.add(featureName);
      return this;
    }

    public FeatureToPrint pkgDepFeatures(List<String> featNames) {
        this.pkgDepFeatNames.addAll(featNames);
        return this;
    }

    public FeatureToPrint serviceRefDepFeatures(List<String> featNames) {
      this.serviceRefDepFeaturesNames.addAll(featNames);
      return this;
    }

    public FeatureToPrint serviceRefListFeatures(List<String> featNames) {
      this.serviceRefListDepFeaturesNames.addAll(featNames);
      return this;
    }

    public List<String> pkgDepFeatNames() {
      return pkgDepFeatNames;
    }

    public List<String> serviceRefDepFeaturesNames() {
      return serviceRefDepFeaturesNames;
    }

    public List<String> serviceRefListDepFeaturesNames() {
      return serviceRefListDepFeaturesNames;
    }

    public BundleField originalBundle() {
      return originalBundle;
    }

    public String createFeature(){
      StringBuilder sb = new StringBuilder();
      sb.append(
              String.format(
                      "\t<feature name=\"%s\" install=\"%s\" version=\"%s\" description=\"%s\">",
                      name, install, version, description));

      if(!pkgDepFeatNames().isEmpty()) {
        sb.append("\n\t\t<!-- Package Dependencies -->\n");
        String pkgBodyDep = pkgDepFeatNames().stream()
                .distinct()
                .map(FeatureToPrint::formatFeatureName)
                .collect(Collectors.joining("\n"));
        sb.append(pkgBodyDep);
      }

      if(!serviceRefDepFeaturesNames().isEmpty()) {
        if(!pkgDepFeatNames().isEmpty()) {
          sb.append("\n");
        }

        sb.append("\n\t\t<!-- Service Reference Dependencies -->\n");
        String serviceRefBody = serviceRefDepFeaturesNames().stream()
                .distinct()
                .map(FeatureToPrint::formatFeatureName)
                .collect(Collectors.joining("\n"));
        sb.append(serviceRefBody);
      }

      if(!serviceRefListDepFeaturesNames().isEmpty()) {
        if(!pkgDepFeatNames().isEmpty() || !serviceRefDepFeaturesNames().isEmpty()) {
          sb.append("\n");
        }

        sb.append("\n\t\t<!-- Service Reference List Dependencies -->\n");
        String serviceRefListBody = serviceRefListDepFeaturesNames().stream()
                .distinct()
                .map(FeatureToPrint::formatFeatureName)
                .collect(Collectors.joining("\n"));
        sb.append(serviceRefListBody);
      }

      if(!bundles.isEmpty()){
        if(!pkgDepFeatNames().isEmpty() || !serviceRefDepFeaturesNames().isEmpty() || !serviceRefListDepFeaturesNames().isEmpty()) {
          sb.append("\n");
        }

        sb.append("\n");
        String bundleBody = bundles.stream()
                .distinct()
                .map(FeatureToPrint::formatBundleLocation)
                .collect(Collectors.joining("\n"));
        sb.append(bundleBody);
      }

      if(!features.isEmpty()){
        if(!pkgDepFeatNames().isEmpty() || !serviceRefDepFeaturesNames().isEmpty() || !serviceRefListDepFeaturesNames().isEmpty() || !bundles.isEmpty()) {
          sb.append("\n");
        }

        sb.append("\n");
        String featureBody = features.stream()
                .distinct()
                .map(FeatureToPrint::formatFeatureName)
                .collect(Collectors.joining("\n"));
        sb.append(featureBody);
      }

      sb.append("\n\t</feature>");

      return sb.toString();
    }

    private static String formatFeatureName(String featName) {
      return "\t\t<feature>" + featName + "</feature>";
    }

    private static String formatBundleLocation(String location) {
      return "\t\t<bundle>" + location + "</bundle>";
    }

    public static FeatureToPrint newFeature(String name, String description, BundleField originalBundle){
      return new FeatureToPrint(name, description, "${project.version}", "manual", originalBundle);
    }

    public static String createFeatureFile(List<FeatureToPrint> features) {
      return FEATURE_FILE_HEADER + features.stream().map(FeatureToPrint::createFeature).collect(Collectors.joining("\n\n")) + FEATURE_FILE_FOOTER;
    }
  }
}
