import React from 'react'

import { connect } from 'react-redux'
import Mount from 'react-mount'
import { isSubmitting, start, end } from 'redux-fetch'

import { getDisplayedLdapStage, getAllConfig, getMessages, getAllowSkip } from './reducer'

import {
  // sync
  setDefaults,
  setOptions,
  setMessages,
  clearWizard,
  clearMessages,
  prevStage,
  nextStage
} from './actions'

const WizardView = (props) => {
  const {
    wizardId,
    clearWizard,
    stages,
    stageId,
    ...rest
  } = props

  return (
    <Mount key={wizardId} off={clearWizard}>
      {(stages[stageId] !== undefined)
        ? React.createElement(stages[stageId], rest)
        : <div>Cannot Find stage with id = {stageId}</div>}
    </Mount>
  )
}

const mapStateToProps = (state, { wizardId }) => {
  const stageId = getDisplayedLdapStage(state)

  return {
    stageId,
    configs: getAllConfig(state),
    submitting: isSubmitting(state, wizardId),
    messages: getMessages(state, stageId),
    allowSkip: getAllowSkip(state, stageId)
  }
}

const mapDispatchToProps = (dispatch, { wizardId }) => ({
  setDefaults: (arg) => dispatch(setDefaults(arg)),
  setOptions: (arg) => dispatch(setOptions(arg)),
  clearWizard: () => dispatch(clearWizard()),
  prev: () => dispatch(prevStage()),
  next: (arg) => dispatch(nextStage(arg)),
  onError: (messages) => dispatch((dispatch, getState) => {
    const stageId = getDisplayedLdapStage(getState())
    dispatch(setMessages(stageId, messages))
  }),
  onStartSubmit: () => dispatch(start(wizardId)),
  onEndSubmit: () => dispatch(end(wizardId)),
  onClearErrors: () => dispatch(clearMessages())
})

const Wizard = connect(mapStateToProps, mapDispatchToProps)(WizardView)

export const createWizard = (wizardId, stages) =>
  (props) => <Wizard wizardId={wizardId} stages={stages} {...props} />

