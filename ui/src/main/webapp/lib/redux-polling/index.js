import { Map } from 'immutable'

import sub from 'redux-submarine'

export const submarine = sub()
export const isPolling = (state, id) => submarine(state).has(id)

// actions
const startPolling = (id) => ({ type: 'redux-polling/START', id })
export const stopPolling = ({ id }) => ({ type: 'redux-polling/STOP', id })

const sleep = (time) => new Promise((resolve) => {
  setTimeout(() => resolve(), time)
})

// async actions
export const poll = (id, asyncAction, { interval = 5000 } = {}) => (...args) => async (dispatch, getState) => {
  if (isPolling(getState(), id)) {
    return
  }

  dispatch(startPolling(id))

  while (true) {
    await dispatch(asyncAction(...args))
    await sleep(interval)
    if (!isPolling(getState(), id)) {
      return
    }
  }
}

export default (state = Map(), { type, id } = {}) => {
  switch (type) {
    case 'redux-polling/START':
      return state.set(id, true)
    case 'redux-polling/STOP':
      return state.delete(id)
    default:
      return state
  }
}
