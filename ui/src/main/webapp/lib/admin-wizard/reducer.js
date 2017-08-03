import { combineReducers } from 'redux-immutable'
import { List, Map } from 'immutable'

import sub from 'redux-submarine'

export const submarine = sub()
export const getCurrentStage = (state) => submarine(state).getIn(['stages']).last()
export const next = (stage) => ({ type: 'admin-wizard/PUSH_STAGE', stage })
export const prev = () => ({ type: 'admin-wizard/POP_STAGE' })

const stages = (state = List.of('introduction-stage'), { type, stage }) => {
  switch (type) {
    case 'admin-wizard/PUSH_STAGE':
      return state.push(stage)
    case 'admin-wizard/POP_STAGE':
      return state.pop()
    case 'admin-wizard/CLEAR_SHARED':
      return List.of('introduction-stage')
    default:
      return state
  }
}

export const setShared = (key, value) =>
  ({ type: `admin-wizard/SET_SHARED_${key.toUpperCase()}`, key, value })
export const clearShared = () => ({ type: 'admin-wizard/CLEAR_SHARED' })
export const getShared = (state) => submarine(state).get('shared')

const shared = (state = Map(), { type, key, value }) => {
  if (type.match('admin-wizard/SET_SHARED') !== null) {
    return state.set(key, value)
  } else if (type === 'admin-wizard/CLEAR_SHARED') {
    return state.clear()
  } else {
    return state
  }
}

export const setLocal = (key, value) =>
  ({ type: `admin-wizard/SET_LOCAL_${key.toUpperCase()}`, key, value })
export const clearLocal = () => ({ type: 'admin-wizard/CLEAR_LOCAL' })
export const getLocal = (state) => submarine(state).get('local')

const local = (state = Map(), { type, key, value }) => {
  if (type.match('admin-wizard/SET_LOCAL') !== null) {
    return state.set(key, value)
  } else if (type === 'admin-wizard/CLEAR_LOCAL') {
    return state.clear()
  } else {
    return state
  }
}

export default combineReducers({ shared, local, stages })
