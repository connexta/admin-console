import React from 'react'

import { expect } from 'chai'

import { mount } from 'enzyme'

import { createStore, compose, applyMiddleware } from 'redux'
import { Provider } from 'react-redux'

import client from './client'
import { ApolloProvider } from 'react-apollo'

import reducer, { getTheme } from './reducer'

import MuiThemeProvider from 'admin-app-bar/MuiThemeProvider'

const Shell = ({ store, client, children }) => (
  <div style={{ maxWidth: 800, margin: '100px auto' }}>
    <Provider store={store}>
      <ApolloProvider client={client}>
        <MuiThemeProvider rootSelector={getTheme}>
          <div>
            {children}
          </div>
        </MuiThemeProvider>
      </ApolloProvider>
    </Provider>
  </div>
)

const doTo = (select, prop, ...args) => ({ type: 'action', select, prop, args })

export const assert = (select, prop, ...args) => ({ type: 'assert', select, prop, args })
export const wait = (select, timeout = 10000) => ({ type: 'wait', select, timeout })
export const click = (select) => doTo(select, 'onClick')
export const edit = (select, ...args) => doTo(select, 'onEdit', ...args)
export const change = (select, value) => doTo(select, 'onChange', value)
export const next = () => click('Next')

const sleep = (ms = 0) => new Promise(resolve => setTimeout(resolve, ms))

export default async (Component, actions, opts = {}) => {
  if (window.location.search.match('debug')) {
    opts.attachTo = window.document.getElementById('here')
    opts.delta = 100
    opts.verbose = true
  }

  const {
    state,          // initial redux store state
    attachTo,       // dom node to render into
    delta = 0,      // time to wait between actions
    verbose = false // log all actions to console?
  } = opts

  const store = createStore((state, action) => {
    if (action.type === 'RESET') {
      return action.state
    }

    return reducer(state, action)
  }, state, compose(applyMiddleware(client.middleware())))

  const log = (...args) => {
    if (verbose) {
      console.log(...args)
    }
  }

  const wrapper = window.wrapper = mount(
    <Shell client={client} store={store}>
      <Component />
    </Shell>,
    { attachTo }
  )

  const find = (wrapper, select) => {
    if (Array.isArray(select)) {
      return select.reduce(find, wrapper)
    }

    if (Number.isInteger(select)) {
      if (select < 0) {
        return wrapper.at(wrapper.length + select)
      }

      return wrapper.at(select)
    }

    if (typeof select === 'object') {
      return wrapper.findWhere((el) => {
        const props = el.props()
        return !Object.keys(select).some((key) => select[key] !== props[key])
      })
    }

    return wrapper.find(select)
  }

  for (let i = 0; i < actions.length; i++) {
    const action = actions[i]
    if (typeof action === 'function') {
      action(wrapper)
    } else {
      log(action)
      const { type, select, prop, args } = action

      if (delta > 0) {
        await sleep(delta)
      }
      switch (type) {
        case 'assert': {
          const found = find(wrapper, select)

          if (found.length !== 1) {
            throw new Error(`No unique element found for selector: ${JSON.stringify(select)}\n ${found.debug()}`)
          }

          expect(found.prop(prop))
            .to.deep.equal(
              args[0],
              `Property ${JSON.stringify(prop)}
              from selector ${JSON.stringify(select)}
              with value ${JSON.stringify(found.prop(prop))}
              did not equal ${JSON.stringify(args[0])}`.replace(/\s+/g, ' '))

          break
        }
        case 'wait': {
          const start = Date.now()

          while (true) {
            const diff = Date.now() - start

            if (diff > action.timeout) {
              throw new Error(`Timeout waiting for selector: ${JSON.stringify(select)}, ${diff} ms elapsed`)
            }

            log(`Waiting for ${JSON.stringify(select)} for ${diff} ms`)

            if (find(wrapper, select).length > 0) {
              break
            }

            await sleep(100)
          }
          break
        }
        case 'action': {
          const found = find(wrapper, select)

          if (found.length === 0) {
            throw new Error(`No such element found for selector: ${JSON.stringify(select)}\n ${found.debug()}`)
          }

          if (found.length !== 1) {
            throw new Error(`No unique element found for selector: ${JSON.stringify(select)}\n ${found.debug()}`)
          }

          const fn = found.prop(prop)

          if (typeof fn !== 'function') {
            throw new Error(`Unable to complete action: ${found} is not a function`)
          }

          fn(...args)

          break
        }
        default:
          throw new Error(`Unknown action type ${type}`)
      }
    }
  }

  return { wrapper, state: store.getState() }
}
