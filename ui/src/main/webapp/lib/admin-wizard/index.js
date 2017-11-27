import React from 'react'

import { connect } from 'react-redux'

import {
  getCurrentStage,
  prev,
  next,
  setShared,
  clearShared,
  getShared,
  setLocal,
  clearLocal,
  getLocal
} from './reducer'

import {
  withConfigs,
  withOptions,
  withPaper,
  withErrors,
  withLoading
} from './enhancers'

import WizardView from './wizard'

const id = (v) => v

const mapStateToProps = (root, { wizardId, rootSelector = id }) => {
  const state = rootSelector(root)
  const stageId = getCurrentStage(state)

  return {
    stageId,
    local: getLocal(state),
    shared: getShared(state)
  }
}

const mapDispatchToProps = {
  setShared,
  clearShared,
  setLocal,
  clearLocal,
  prev,
  next
}

const Wizard = connect(mapStateToProps, mapDispatchToProps)(WizardView)

const localEnhancers = {
  errors: withErrors,
  loading: withLoading,
  paper: withPaper,
  options: withOptions
}

const sharedEnhancers = {
  configs: withConfigs
}

const createLocalEnhancer = (enhancer, name = Date.now().toString()) => (Component) => {
  Component = enhancer(Component)

  return (props) => {
    const state = props.local.get(name)
    const setState = (v) => props.setLocal(name, v)
    return <Component state={state} setState={setState} props={props} />
  }
}

const createSharedEnhancer = (enhancer, name = Date.now().toString()) => (Component) => {
  Component = enhancer(Component)

  return (props) => {
    const state = props.shared.get(name)
    const setState = (v) => props.setShared(name, v)
    return <Component state={state} setState={setState} props={props} />
  }
}

export const createWizard = (wizardId, stages, { shared = {} } = {}) => {
  const enhanced = Object.keys(stages).reduce((o, stageId) => {
    o[stageId] = stages[stageId]

    Object.keys(localEnhancers).forEach((name) => {
      o[stageId] = createLocalEnhancer(localEnhancers[name], name)(o[stageId])
    })

    Object.keys(shared).forEach((name) => {
      o[stageId] = createSharedEnhancer(shared[name], name)(o[stageId])
    })

    Object.keys(sharedEnhancers).forEach((name) => {
      o[stageId] = createSharedEnhancer(sharedEnhancers[name], name)(o[stageId])
    })

    return o
  }, {})

  return (props) => <Wizard wizardId={wizardId} stages={enhanced} {...props} />
}

