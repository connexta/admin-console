import React from 'react'

import { renderToString } from 'react-dom/server'
import { createMemoryHistory, RouterContext, match } from 'react-router'

export default (routes) => ({ html, path }, done) => {
  const history = createMemoryHistory({ basename: '#' })
  match({ routes, location: path, history }, (err, redirectLocation, renderProps) => {
    if (err) {
      done(err)
    } else {
      let root = renderToString(<RouterContext {...renderProps} />)

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
