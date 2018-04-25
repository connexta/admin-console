package org.codice.ddf.admin.query.dev.system.dependency;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.query.dev.system.graph.BundleStateField;
import org.osgi.framework.Bundle;

public class BundleStateRecorder {

  private Bundle bundle;
  private List<BundleStateField> previousStates;
  private BundleStateField currentState;

  public BundleStateRecorder(Bundle bundle) {
    this.bundle = bundle;
    previousStates = new ArrayList<>();
  }

  public void recordState(long currentTime) {
    int newState = bundle.getState();

    if(currentState == null) {
      currentState = new BundleStateField(bundle.getLocation(), bundle.getState(), currentTime);
    }

    if(newState != currentState.state()) {
      currentState.endTime(currentTime);
      previousStates.add(currentState);
      currentState = new BundleStateField(bundle.getLocation(), newState, currentTime);
    }
  }

  public List<BundleStateField> getPreviousStates() {
    return previousStates;
  }
}
