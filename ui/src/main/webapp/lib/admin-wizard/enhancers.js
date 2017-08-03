import React from 'react'

import { fromJS, Map } from 'immutable'
import { getFriendlyMessage } from 'graphql-errors'

import Spinner from 'components/Spinner'

import Paper from 'material-ui/Paper'

export const withConfigs = (Component) => ({ state, setState, props }) => {
  state = Map(state)

  const onEdit = (id) => (value) => setState(state.set(id, value))
  const setDefaults = (config) =>
    setState(fromJS(config).merge(state.filter((v) => v !== undefined && v !== '')))

  return (
    <Component
      configs={state.toJS()}
      onEdit={onEdit}
      setDefaults={setDefaults}
      {...props}
    />
  )
}

export const withOptions = (Component) => ({ state, setState, props }) => {
  state = Map(state)
  const setOptions = (opts) => setState(opts)

  return (
    <Component
      options={state.toJS()}
      setOptions={setOptions}
      {...props}
    />
  )
}

export const withErrors = (Component) => ({ state = [], setState, props }) => {
  const onError = (messages = []) => {
    const errors = messages.map(({ message: code, ...rest }) => ({
      message: getFriendlyMessage(code),
      ...rest
    }))

    setState(errors)
  }

  return (
    <Component
      errors={state}
      onError={onError}
      {...props}
    />
  )
}

export const withLoading = (Component) => ({ state = false, setState, props }) => {
  const onStartSubmit = () => setState(true)
  const onEndSubmit = () => setState(false)

  return (
    <Spinner submitting={state}>
      <Component
        onStartSubmit={onStartSubmit}
        onEndSubmit={onEndSubmit}
        {...props}
      />
    </Spinner>
  )
}

export const withPaper = (Component) => ({ props }) => (
  <Paper style={{
    margin: '20px 0',
    padding: '40px',
    position: 'relative'
  }}>
    <Component {...props} />
  </Paper>
)
