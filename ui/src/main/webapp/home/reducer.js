import { combineReducers } from 'redux-immutable'
import { Map } from 'immutable'
import sub from 'redux-submarine'

export const submarine = sub()
export const getSourceConfigs = (state) => submarine(state).getIn(['configs', 'sources'])
export const getLdapConfigs = (state) => submarine(state).getIn(['configs', 'ldap'])
export const getSubmittingPids = (state) => submarine(state).getIn(['submitting']).toJS()
export const getConfigErrors = (state, servicePid) => submarine(state).getIn(['errors', servicePid], [])

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
