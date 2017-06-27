import React from 'react'

import { connect } from 'react-redux'
import Mount from 'react-mount'

import {
  getDisplayedLdapStage,
  getAllConfig,
  getMessages,
  isSubmitting
} from './reducer'

import { getFriendlyMessage } from 'graphql-errors'

import {
  // sync
  setDefaults,
  setOptions,
  setMessages,
  clearWizard,
  clearMessages,
  prevStage,
  nextStage,
  start,
  end
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
    messages: getMessages(state, stageId)
  }
}

const mapDispatchToProps = (dispatch, { wizardId, ids = [] }) => ({
  setDefaults: (arg) => dispatch(setDefaults(arg)),
  setOptions: (arg) => dispatch(setOptions(arg)),
  clearWizard: () => dispatch(clearWizard()),
  prev: (arg) => dispatch((dispatch, getState) => {
    const stageId = getDisplayedLdapStage(getState())
    dispatch(clearMessages())
    dispatch(setMessages(stageId, []))
    dispatch(prevStage())
  }),
  next: (arg) => dispatch((dispatch, getState) => {
    const stageId = getDisplayedLdapStage(getState())
    dispatch(clearMessages())
    dispatch(setMessages(stageId, []))
    dispatch(nextStage(arg))
  }),
  onError: (messages) => dispatch((dispatch, getState) => {
    dispatch(clearMessages())

    const stageId = getDisplayedLdapStage(getState())

    const errors = messages.map(({ path = [], ...rest }) => {
      const id = path[path.length - 1]
      if (ids.includes(id)) {
        return { configFieldId: id, ...rest }
      } else {
        return { ...rest }
      }
    }).map(({ message: code, ...rest }) => ({
      message: getFriendlyMessage(code),
      ...rest
    }))

    dispatch(setMessages(stageId, errors))
  }),
  onStartSubmit: () => dispatch(start(wizardId)),
  onEndSubmit: () => dispatch(end(wizardId))
})

const Wizard = connect(mapStateToProps, mapDispatchToProps)(WizardView)

export const createWizard = (wizardId, stages, ids) =>
  (props) => <Wizard wizardId={wizardId} stages={stages} ids={ids} {...props} />

