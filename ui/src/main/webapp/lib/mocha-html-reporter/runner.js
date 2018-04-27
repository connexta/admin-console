import React from 'react'

import { Map, List } from 'immutable'

const reducer = (state, { type, args }) => {
  if (state === undefined) {
    return Map({ path: List(), tests: List() })
  }

  switch (type) {
    case 'start':
      return state.set('status', 'started')
    case 'end':
      return state.set('status', 'ended')
    case 'suite':
      return state.update('path', (path) => path.push(args[0].title))
    case 'suite end':
      return state.update('path', (path) => path.pop())
    case 'test':
      return state.update('tests', (tests) => tests.push(
        Map({
          path: state.get('path'),
          title: args[0].title,
          state: 'pending'
        })
      ))
    case 'pass':
      return state.updateIn(
        ['tests', state.get('tests').size - 1],
        (test) => test.merge({
          state: 'pass',
          duration: args[0].ctx.test.duration
        })
      )
    case 'fail':
      return state.updateIn(
        ['tests', state.get('tests').size - 1],
        (test) => test.merge({
          state: 'fail',
          error: args[1],
          duration: args[0].ctx.test.duration
        })
      )
    case 'pending':
      return state.updateIn(
        ['tests', state.get('tests').size - 1],
        (test) => test.merge({
          state: 'pending'
        })
      )
    default:
      return state
  }
}

const events = [
  'start', //  Execution started
  'end', // Execution complete
  'suite', // Test suite execution started
  'suite end', // All tests (and sub-suites) have finished
  'test', // Test execution started
  'test end', // Test completed
  'hook', // Hook execution started
  'hook end', // Hook complete
  'pass', // Test passed
  'fail', // Test failed
  'pending' // Test pending
]

export const withRunner = (mocha) => (Component) => class extends React.Component {
  constructor (props) {
    super(props)
    this.state = { total: 0, state: reducer(undefined, {}) }
    mocha.reporter((runner) => {
      this.setState({
        total: runner.total,
        state: reducer(undefined, {})
      })

      events.forEach((type) => {
        runner.on(type, (...args) => {
          this.setState(({ state }) => ({
            total: runner.total,
            state: reducer(state, { type, args })
          }))
        })
      })
    })
  }
  render () {
    return (
      <Component
        total={this.state.total}
        tests={this.state.state.get('tests')} />
    )
  }
}
