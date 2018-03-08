package org.codice.ddf.admin.component.impl.common;

import java.util.List;
import java.util.Optional;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.ReportImpl;
import org.codice.ddf.admin.component.api.Component;

public class ComponentUtils {

  public Report<Component> matchComponent(List<Component> components, StringField componentId) {
    Optional<Component> component =
        components.stream().filter(cap -> cap.getId().equals(componentId.getValue())).findFirst();

    if (!component.isPresent()) {
      return new ReportImpl<>(Messages.componentNotFound(componentId.getPath()));
    }

    return new ReportImpl<>(component.get());
  }
}
