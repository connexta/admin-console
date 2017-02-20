import { combineReducers } from 'redux-immutable'
import { Map } from 'immutable'

export const getSourceConfigs = (state) => state.getIn(['home', 'configs', 'sources'])
export const getLdapConfigs = (state) => state.getIn(['home', 'configs', 'ldap'])
export const getSubmittingPids = (state) => state.getIn(['home', 'submitting']).toJS()
export const getConfigErrors = (state, servicePid) => state.getIn(['home', 'errors', servicePid], [])

const configs = (state = Map(), { type, id, value = [] }) => {
  switch (type) {
    case 'HOME/SET_CONFIGS':
      return state.set(id, value)
    default:
      return state
  }
}

const submitting = (state = Map(), { type, servicePid, value = [] }) => {
  switch (type) {
    case 'HOME/BEGIN_SUBMITTING_CONFIGS':
      return state.set(servicePid, true)
    case 'HOME/END_SUBMITTING_CONFIGS':
      return state.delete(servicePid)
    case 'HOME/SET_CONFIGS':
      return state.filter((v, key) => (value.some((config) => (config.servicePid === key))))
    default:
      return state
  }
}

const errors = (state = Map(), { type, servicePid, errors = [] }) => {
  switch (type) {
    case 'HOME/SET_ERRORS':
      return state.set(servicePid, errors)
    case 'HOME/CLEAR_ERRORS':
      return state.delete(servicePid)
    case 'HOME/CLEAR_ALL_ERRORS':
      return state.clear()
    default:
      return state
  }
}

export default combineReducers({ configs, submitting, errors })
