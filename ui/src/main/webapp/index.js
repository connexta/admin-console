import inject from 'react-tap-event-plugin'
import React from 'react'
import { render } from 'react-dom'
import { Router, createMemoryHistory, hashHistory, RouterContext, match } from 'react-router'

import { routes } from './app'

if (!window.SERVER_RENDER) {
  inject()
}

if (process.env.NODE_ENV === 'production') {
  if (!window.SERVER_RENDER) {
    render(<Router history={hashHistory} routes={routes} />, document.getElementById('root'))
  }
}

if (process.env.NODE_ENV !== 'production') {
  const AppContainer = require('react-hot-loader').AppContainer

  render(
    <AppContainer errorReporter={({ error }) => { throw error }}>
      <Router history={hashHistory} routes={routes} />
    </AppContainer>,
    document.getElementById('root'))

  module.hot.accept('./app', () => {
    // If you use Webpack 2 in ES modules mode, you can
    // use <App /> here rather than require() a <NextApp />.
    try {
      const routes = require('./app').routes
      render(
        <AppContainer errorReporter={({ error }) => { throw error }}>
          <Router history={hashHistory} routes={routes} />
        </AppContainer>,
        document.getElementById('root'))
    } catch (e) {}
  })
}

export default ({ html, path }, done) => {
  const ReactDOMServer = require('react-dom/server')
  const history = createMemoryHistory({ basename: '#' })

  match({ routes, location: path, history }, (err, redirectLocation, renderProps) => {
    if (err) {
      done(err)
    } else {
      let root = ReactDOMServer.renderToString(<RouterContext {...renderProps} />)

      // fix issue with inline fallback styles and react
      // https://github.com/facebook/react/issues/2020
      root = root.replace(/(;|")([^":\n]+):([^";()]+);/g,
        (match, _, key, values) =>
          _ + values.split(',').map((v) => key + ':' + v).join(';') + ';')

      const pattern = /.*{{{[\s\S]*}}}.*/
      done(null, html.replace(pattern, root))
    }
  })
}
