package org.codice.ddf.admin.component.api;

public interface Component {

  String getId();

  String getDescription();

  boolean start();
}
