import React from 'react'

import { Map } from 'immutable'

import { createWizard } from 'admin-wizard'

import WelcomeStage from './stages/welcome'
import DiscoveryStage from './stages/discovery'
import SourceSelectionStage from './stages/source-selection'
import ConfirmationStage from './stages/confirmation'
import CompletedStage from './stages/completed'

const stageMapping = {
  'introduction-stage': WelcomeStage,
  discoveryStage: DiscoveryStage,
  sourceSelectionStage: SourceSelectionStage,
  confirmationStage: ConfirmationStage,
  completedStage: CompletedStage
}

const withDiscoveryType = (Component) => ({ state = 'hostnamePort', setState, props }) => {
  return (
    <Component
      discoveryType={state}
      setDiscoveryType={setState}
      {...props}
    />
  )
}

const withDiscoveredEndpoints = (Component) => ({ state, setState, props }) => (
  <Component
    discoveredEndpoints={Map(state).toJS()}
    setDiscoveredEndpoints={setState}
    {...props}
  />
)

const withChosenEndpoint = (Component) => ({ state, setState, props }) => (
  <Component
    chosenEndpoint={state}
    type={state}
    setChosenEndpoint={setState}
    {...props}
  />
)

const opts = {
  shared: {
    type: withDiscoveryType,
    endpoints: withDiscoveredEndpoints,
    chosen: withChosenEndpoint
  }
}

const Wizard = createWizard('sources', stageMapping, opts)

export default (sources) => () => <Wizard sources={sources} />
