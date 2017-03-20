import { combineReducers } from 'redux-immutable'
import { fromJS, Map } from 'immutable'
import sub from 'redux-submarine'

const systemUsageProperties = (state = Map(), { type, properties }) => {
  switch (type) {
    case 'SYSTEM_USAGE/SET_PROPERTIES':
      return fromJS(properties)
    default:
      return state
  }
}

const systemUsageAccepted = (state = false, { type }) => {
  switch (type) {
    case 'SYSTEM_USAGE/ACCEPTED':
      return true
    default:
      return state
  }
}

export const submarine = sub()
export const getSystemUsageProperties = (state) => submarine(state).get('systemUsageProperties').toJS()
export const getSystemUsageAccepted = (state) => submarine(state).get('systemUsageAccepted')

export default combineReducers({ systemUsageProperties, systemUsageAccepted })

