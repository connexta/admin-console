import { combineReducers } from 'redux-immutable'
import { fromJS, Map } from 'immutable'

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

// TODO: replace these absolute paths with relative ones like the other wizards
export const getConfigTypes = (state) => state.getIn(['sourceWizard', 'configTypes']).toJS()

export const getConfigTypeById = (state, id) => {
  const found = getConfigTypes(state).filter((config) => config.id === id)
  if (found.length > 0) {
    return found[0].name
  }
}

export const getSystemUsageProperties = (state) => state.getIn(['systemUsage', 'systemUsageProperties']).toJS()
export const getSystemUsageAccepted = (state) => state.getIn(['systemUsage', 'systemUsageAccepted'])

export default combineReducers({ systemUsageProperties, systemUsageAccepted })

