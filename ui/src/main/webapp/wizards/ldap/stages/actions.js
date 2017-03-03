import { post } from 'redux-fetch'

import { getAllConfig } from '../../../reducer'
import { setMessages, clearMessages } from 'admin-wizard/actions'

export const setProbeValue = (value) => ({ type: 'SET_PROBE_VALUE', value })

export const probe = (url) => async (dispatch, getState) => {
  dispatch(clearMessages())

  const config = getAllConfig(getState())
  const body = JSON.stringify({ configurationType: 'ldap', ...config })

  const res = await dispatch(post(url, { body }))
  const json = await res.json()

  if (res.status === 400) {
    dispatch(setMessages('directory-settings', json.messages))
  } else if (res.status === 200) {
    dispatch(setProbeValue(json.probeResults.ldapQueryResults))
  }
}
