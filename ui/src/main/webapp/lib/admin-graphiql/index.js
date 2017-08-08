import React from 'react'

import GraphiQL from 'graphiql'
import 'graphiql/graphiql.css'

const graphQLFetcher = (graphQLParams) => (
  window.fetch(window.location.origin + '/admin/hub/graphql', {
    method: 'post',
    credentials: 'same-origin',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(graphQLParams)
  }).then(response => response.json())
)

export default () => (
  <div style={{
    position: 'fixed',
    top: 64,
    left: 0,
    bottom: 0,
    right: 0
  }}>
    <GraphiQL fetcher={graphQLFetcher} />
  </div>
)
