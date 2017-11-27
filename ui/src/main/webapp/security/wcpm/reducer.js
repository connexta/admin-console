import { combineReducers } from 'redux-immutable'

import { fromJS, Map, List, Record } from 'immutable'

export const isEditing = (state) => state.get('editing') !== null
export const hasSubmitted = (state) => state.get('submitted')
export const editingToken = (state) => state.get('editing')
export const getValue = (state) => state.get('value')
export const getErrors = (state) => state.get('errors')
export const getNotification = (state) => state.get('notification')

export const edit = (token, value) => ({ type: 'wcpm/EDIT', token, value })
export const cancel = () => ({ type: 'wcpm/CANCEL' })
export const update = (path, value) => ({ type: 'wcpm/UPDATE', path, value })
export const error = (message, errors) => ({ type: 'wcpm/ERROR', message, errors })
export const submit = () => ({ type: 'wcpm/SUBMIT' })
export const dismiss = () => ({ type: 'wcpm/DISMISS' })

const editing = (state = null, { type, token }) => {
  switch (type) {
    case 'wcpm/EDIT':
      return token
    case 'wcpm/CANCEL':
      return null
    default:
      return state
  }
}

const value = (state = null, { type, path, value }) => {
  switch (type) {
    case 'wcpm/EDIT':
      return fromJS(value)
    case 'wcpm/CANCEL':
      return null
    case 'wcpm/UPDATE':
      if (value === undefined) {
        return state.deleteIn(path)
      } else {
        return state.setIn(path, value)
      }
    default:
      return state
  }
}

const submitted = (state = false, { type }) => {
  switch (type) {
    case 'wcpm/EDIT':
    case 'wcpm/CANCEL':
      return false
    case 'wcpm/SUBMIT':
      return true
    default:
      return state
  }
}

const notification = (state = null, { type, message = null }) => {
  switch (type) {
    case 'wcpm/CANCEL':
    case 'wcpm/DISMISS':
      return null
    case 'wcpm/ERROR':
      return message
    default:
      return state
  }
}

const Errors = Record({
  paths: [],
  authTypes: [],
  realm: [],
  claimMapping: [],
  all: []
})

const errors = (state = Map(), { type, errors = [] }) => {
  switch (type) {
    case 'wcpm/EDIT':
    case 'wcpm/CANCEL':
      return state.clear()
    case 'wcpm/ERROR':
      const groups = [
        'paths',
        'authTypes',
        'realm',
        'claimMapping'
      ]

      return new Errors(
        List(errors).groupBy(({ path }) =>
          groups.find((group) => path.includes(group) || 'all')))

    default:
      return state
  }
}

export default combineReducers({ editing, value, submitted, errors, notification })
