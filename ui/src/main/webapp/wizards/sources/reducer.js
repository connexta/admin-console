import { combineReducers } from 'redux-immutable'
import { fromJS, Map } from 'immutable'
import sub from 'redux-submarine'

const currentStage = (state = 'welcomeStage', { type, stage }) => {
  switch (type) {
    case 'SOURCES/CHANGE_STAGE':
      return stage
    case 'CLEAR_WIZARD': // also make clear config info
      return 'welcomeStage'
    default:
      return state
  }
}

const chosenEndpoint = (state = '', { type, endpointKey }) => {
  switch (type) {
    case 'SOURCES/SET_CHOSEN_ENDPOINT':
      return endpointKey
    case 'CLEAR_WIZARD':
      return ''
    default:
      return state
  }
}

const discoveredEndpoints = (state = Map(), { type, endpointConfigs }) => {
  switch (type) {
    case 'SOURCES/SET_DISCOVERED_ENDPOINTS':
      return fromJS(endpointConfigs)
    case 'CLEAR_WIZARD':
      return Map()
    default:
      return state
  }
}

const errors = (state = Map(), { type, stageId, errorList }) => {
  switch (type) {
    case 'SOURCES/SET_ERRORS':
      return state.set(stageId, errorList)
    case 'SOURCES/CLEAR_ERRORS':
    case 'SOURCES/CHANGE_STAGE':
    case 'CLEAR_WIZARD':
      return Map()
    default:
      return state

  }
}

const isSubmitting = (state = false, { type }) => {
  switch (type) {
    case 'SOURCES/START_SUBMITTING':
      return true
    case 'SOURCES/END_SUBMITTING':
      return false
    case 'CLEAR_WIZARD':
      return false
    default:
      return state
  }
}

const discoveryType = (state = 'hostnamePort', { type, value }) => {
  switch (type) {
    case 'SOURCES/SET_DISCOVERY_TYPE':
      return value
    default:
      return state
  }
}

export const submarine = sub()
export const getIsSubmitting = (state) => submarine(state).get('isSubmitting')
export const getDiscoveryType = (state) => submarine(state).get('discoveryType')
export const getStage = (state) => submarine(state).get('currentStage')
export const getErrors = (state, stageId) => submarine(state).getIn(['errors', stageId], [])
export const getSourceName = (state) => state.getIn(['wizard', 'config', 'sourceName', 'value'])
export const getDiscoveredEndpoints = (state) => submarine(state).get('discoveredEndpoints').toJS()
export const getChosenEndpoint = (state) => submarine(state).get('chosenEndpoint')

export default combineReducers({
  currentStage,
  isSubmitting,
  discoveryType,
  discoveredEndpoints,
  chosenEndpoint,
  errors
})

