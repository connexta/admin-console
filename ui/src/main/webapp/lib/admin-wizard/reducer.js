import { combineReducers } from 'redux-immutable'
import { fromJS, List, Map } from 'immutable'

export const getConfig = (state, id) => state.getIn(['config'].concat(id), Map()).toJS()
export const getAllConfig = (state) => state.get('config').map((config) => config.get('value')).toJS()
export const getMessages = (state, id) => state.getIn(['messages', id], List()).toJS()
export const getProbeValue = (state) => state.getIn(['probeValue'])
export const getDisplayedLdapStage = (state) => state.getIn(['ldapDisplayedStages']).last()
export const getAllowSkip = (state, stageId) => state.getIn(['allowSkip', stageId])

// TODO: add reducer checks for the wizardClear action to reset the state to defaults
const config = (state = Map(), { type, id, value, values, messages, options }) => {
  switch (type) {
    case 'EDIT_CONFIG':
      return state.setIn([id, 'value'], value)
    case 'SET_CONFIG_SOURCE':
      return state.mergeDeep(fromJS(value).map((value) => fromJS({ value })))
    case 'SET_DEFAULTS':
      return fromJS(values).map((value) => fromJS({ value })).merge(state)
    case 'SET_OPTIONS':
      return state.mergeDeep(fromJS(options).map((options) => fromJS({ options })))
    case 'CLEAR_CONFIG':
      return state.clear()
    case 'SET_MESSAGES':
      const errs = fromJS(messages)
        .filter((v) => v.has('configFieldId'))
        .reduce((m, v) => m.set(v.get('configFieldId'), Map.of('message', v)), Map())

      return state.mergeDeep(errs)
    case 'CLEAR_MESSAGES':
      return state.map((config) => config.delete('message'))
    case 'CLEAR_WIZARD':
      return Map()
  }

  return state
}

const messages = (state = Map(), { type, id, messages }) => {
  switch (type) {
    case 'SET_MESSAGES':
      const msgs = fromJS(messages)
        .filter((m) => !m.has('configFieldId'))

      return state.updateIn([id], List(), m => m.concat(msgs))
    case 'CLEAR_MESSAGES':
      return state.delete(id)
    case 'CLEAR_WIZARD':
      return Map()
    default:
      return state
  }
}

const probeValue = (state = [], { type, value }) => {
  switch (type) {
    case 'SET_PROBE_VALUE':
      return value
    case 'CLEAR_WIZARD':
      return Map()
    default:
      return state
  }
}

const ldapDisplayedStages = (state = List.of('introduction-stage'), { type, stage }) => {
  switch (type) {
    case 'LDAP_ADD_STAGE':
      return state.push(stage)
    case 'LDAP_REMOVE_STAGE':
      return state.pop()
    case 'CLEAR_WIZARD':
      return List.of('introduction-stage')
    default:
      return state
  }
}

const allowSkip = (state = Map(), { type, stageId }) => {
  switch (type) {
    case 'ALLOW_SKIP':
      return state.set(stageId, true)
    case 'DONT_ALLOW_SKIP':
    case 'EDIT_CONFIG':
    case 'LDAP_ADD_STAGE':
    case 'LDAP_REMOVE_STAGE':
      return Map()
    default:
      return state
  }
}

export default combineReducers({
  config,
  probeValue,
  messages,
  ldapDisplayedStages,
  allowSkip
})

