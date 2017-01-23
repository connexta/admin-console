import { post } from 'redux-fetch'

const prefix = (...args) => '/' + ['admin', 'beta', 'config'].concat(args).join('/')

const api = (method) => (opts) => async (dispatch) => {
  const {
    configHandlerId,
    configurationType,
    config,
    ...rest
  } = opts

  const url = prefix(method, configHandlerId, opts[method + 'Id'])
  const body = JSON.stringify({ configurationType, ...config })

  return dispatch(post(url, { body, ...rest }))
}

export const test = api('test')
export const probe = api('probe')
export const persist = api('persist')

