import { get } from 'redux-fetch'

export const setSystemUsageProperties = (properties) => ({ type: 'SYSTEM_USAGE/SET_PROPERTIES', properties })
export const acceptSystemUsage = () => ({ type: 'SYSTEM_USAGE/ACCEPTED' })

export const fetchSystemUsageProperties = ({ url }) => async (dispatch, getState) => {
  try {
    const res = await dispatch(get(url))

    if (res.status === 200) {
      const json = await res.json()
      dispatch(setSystemUsageProperties(json.value.configurations[0].properties))
    } else {
      dispatch(setSystemUsageProperties({}))
    }
  } catch (e) {
    dispatch(setSystemUsageProperties({}))
  }
}
