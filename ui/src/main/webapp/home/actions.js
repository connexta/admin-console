import { poll } from 'redux-polling'
import { get, post } from 'redux-fetch'

// actions

export const setConfigs = (id, value) => ({ type: 'HOME/SET_CONFIGS', id, value })
export const beginDelete = (servicePid) => ({ type: 'HOME/BEGIN_SUBMITTING_CONFIGS', servicePid })
export const endDelete = (servicePid) => ({ type: 'HOME/END_SUBMITTING_CONFIGS', servicePid })
export const setErrors = (servicePid, errors) => ({ type: 'HOME/SET_ERRORS', servicePid, errors })
export const clearErrors = (servicePid) => ({ type: 'HOME/CLEAR_ERRORS', servicePid })

// async actions

export const retrieve = (configHandlerId) => async (dispatch) => {
  const res = await dispatch(get('/admin/beta/config/configurations/' + configHandlerId))
  const json = await res.json()

  if (res.status === 200) {
    dispatch(setConfigs(configHandlerId, json))
  }
}

export const refresh = poll('home', () => (dispatch) =>
  Promise.all([
    dispatch(retrieve('sources')),
    dispatch(retrieve('ldap'))
  ])
)

export const deleteConfig = ({ configurationHandlerId, configurationType, factoryPid, servicePid }) => async (dispatch) => {
  dispatch(beginDelete(servicePid))
  const url = '/admin/beta/config/persist/' + configurationHandlerId + '/delete'
  const body = JSON.stringify({configurationType, factoryPid, servicePid})

  const res = await dispatch(post(url, {body}))
  const json = await res.json()

  if (containsErrors(json)) {
    dispatch(setErrors(servicePid, filterErrors(json.messages)))
    dispatch(endDelete(servicePid))
  } else {
    dispatch(refresh())
  }
}

// utility methods

const containsErrors = (json) => {
  return json.messages.some((message) => (message.type === 'FAILURE'))
}

const filterErrors = (messages) => {
  return messages.filter((message) => (message.type === 'FAILURE'))
}
