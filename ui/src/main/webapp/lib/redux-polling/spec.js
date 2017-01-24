import { expect } from 'chai'

import thunk from 'redux-thunk'
import { createStore, applyMiddleware } from 'redux'

import reducer, { isPolling } from '../../reducer'
import { poll, stopPolling } from './'

describe('poll', () => {
  it('should poll at correct interval', (done) => {
    const store = createStore(reducer, applyMiddleware(thunk))

    let count = 0
    const id = 'some-id'
    const asyncAction = () => async () => {
      count++
      const polling = isPolling(store.getState(), id)
      if (!polling) {
        done(Error('invalid polling state'))
      }
    }

    const pollingAction = poll(id, asyncAction, { interval: 1 })

    store.dispatch(pollingAction())

    setTimeout(() => {
      expect(count).to.be.above(2)
      const beforeStopping = count
      store.dispatch(stopPolling({ id }))
      expect(count).to.be.equal(beforeStopping)
      expect(isPolling(store.getState(), 'some-id')).to.be.false
      done()
    }, 25)
  })
})
