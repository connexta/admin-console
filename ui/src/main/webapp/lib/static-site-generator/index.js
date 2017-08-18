import React from 'react'

import { renderToStaticMarkup } from 'react-dom/server'
import { createMemoryHistory, RouterContext, match } from 'react-router'

import Html from './html'

export default (routes) => ({ path, webpackStats }, done) => {
  const history = createMemoryHistory({ basename: '#' })
  match({ routes, location: path, history }, (err, redirectLocation, renderProps) => {
    if (err) {
      done(err)
    } else {
      let root = renderToStaticMarkup(
        <Html assets={Object.keys(webpackStats.compilation.assets)}>
          <RouterContext {...renderProps} />
        </Html>
      )

      // fix issue with inline fallback styles and react
      // https://github.com/facebook/react/issues/2020
      root = root.replace(/(;|")([^":\n]+):([^";()]+);/g,
        (match, _, key, values) =>
          _ + values.split(',').map((v) => key + ':' + v).join(';') + ';')

      done(null, '<!DOCTYPE html>' + root)
    }
  })
}
