import * as api from 'admin-api'

import { getAllConfig } from '../../reducer'

export const setMessages = (id, messages) => ({ type: 'SET_MESSAGES', id, messages })
export const updateProgress = (id, value) => ({ type: 'UPDATE_PROGRESS', id, value })
export const clearMessages = (id) => ({ type: 'CLEAR_MESSAGES', id })
export const nextStage = ({ nextStageId: stage }) => ({ type: 'LDAP_ADD_STAGE', stage })
export const prevStage = () => ({ type: 'LDAP_REMOVE_STAGE' })
export const setOptions = (options) => ({type: 'SET_OPTIONS', options})
export const editConfig = (id, value) => ({ type: 'EDIT_CONFIG', id, value })
export const editConfigs = (values) => ({ type: 'EDIT_CONFIGS', values })
export const setDefaults = (values) => ({ type: 'SET_DEFAULTS', values })
export const clearWizard = () => ({ type: 'CLEAR_WIZARD' })
export const setSkippable = (stageId) => ({ type: 'ALLOW_SKIP', stageId })
export const setNotSkippable = () => ({ type: 'DONT_ALLOW_SKIP' })

export const probe = (options) => async (dispatch, getState) => {
  const { stageId, ...opts } = options

  const config = getAllConfig(getState())
  const res = await dispatch(api.probe({ config, id: stageId, ...opts }))
  const json = await res.json()

  if (res.status === 200) {
    let defaults = {}
    Object.keys(json.probeResults).forEach((key) => {
      defaults[key] = json.probeResults[key][0]
    })
    dispatch(setDefaults(defaults))
    dispatch(setOptions(json.probeResults))
  }
}

export const persist = (options) => async (dispatch, getState) => {
  const { stageId, nextStageId, ...opts } = options
  dispatch(clearMessages(stageId))

  const config = getAllConfig(getState())
  const res = await dispatch(api.persist({ config, id: stageId, ...opts }))
  const json = await res.json()

  if (res.status === 400) {
    dispatch(setMessages(stageId, json.messages))
  } else if (res.status === 200) {
    dispatch(setMessages(stageId, json.messages))
    if (nextStageId !== undefined) {
      dispatch(nextStage({ nextStageId }))
    }
  }
}

export const test = (options) => async (dispatch, getState) => {
  const { stageId, nextStageId, ...opts } = options
  dispatch(clearMessages(stageId))

  const config = getAllConfig(getState())
  const res = await dispatch(api.test({ config, id: stageId, ...opts }))
  const json = await res.json()

  if (res.status === 400) {
    dispatch(setMessages(stageId, json.messages))
    if (onlyHasWarnings(json.messages)) {
      dispatch(setSkippable(stageId))
    } else {
      dispatch(setNotSkippable())
    }
  } else if (res.status === 200) {
    dispatch(setMessages(stageId, json.messages))
    if (nextStageId !== undefined) {
      dispatch(nextStage({ nextStageId }))
    }
  }
}

const onlyHasWarnings = (messages) => {
  messages.forEach((message) => { if (message.type !== 'WARNING') return false })
  return true
}

