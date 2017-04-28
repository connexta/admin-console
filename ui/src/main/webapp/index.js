import inject from 'react-tap-event-plugin'
import React from 'react'
import ReactDOM from 'react-dom'
import { createMemoryHistory, RouterContext, match } from 'react-router'
import deepForceUpdate from 'react-deep-force-update'
import { AppContainer } from 'react-hot-loader'

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
