import inject from 'react-tap-event-plugin'
import React from 'react'
import ReactDOM from 'react-dom'
import deepForceUpdate from 'react-deep-force-update'
import { AppContainer } from 'react-hot-loader'
import generator from 'static-site-generator'

import App, { routes } from './app'

const render = Component =>
  ReactDOM.render(
    <AppContainer>
      <Component />
    </AppContainer>,
    document.getElementById('root'))

if (!window.SERVER_RENDER) {
  inject()
}

if (process.env.NODE_ENV === 'production') {
  if (!window.SERVER_RENDER) {
    render(App)
  }
}

if (process.env.NODE_ENV !== 'production') {
  let instance = render(App)
  module.hot.accept('./app', () => { instance = render(require('./app').default) })
  window.forceUpdateThemeing = () => setTimeout(() => deepForceUpdate(instance), 0)
}

export default generator(routes)
