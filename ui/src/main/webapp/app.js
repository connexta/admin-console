import React from 'react'
import { Provider, connect } from 'react-redux'
import { ApolloProvider } from 'react-apollo'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import { Router, hashHistory } from 'react-router'

import store from './store'
import client from './client'
import Exception from './containers/exceptions'
import Ldap from './wizards/ldap'
import Sources from './wizards/sources'
import { Home } from './home'
import Wcpm from './security/wcpm'

import ddfSources from 'ddf-sources'
import Backdrop from 'components/Backdrop'
import AdminAppBar from 'admin-app-bar'

import { getTheme } from 'admin-app-bar/reducer'

import getMuiTheme from 'material-ui/styles/getMuiTheme'

function graphQLFetcher (graphQLParams) {
  return window.fetch(window.location.origin + '/admin/beta/graphql', {
    method: 'post',
    credentials: 'same-origin',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(graphQLParams)
  }).then(response => response.json())
}

const GraphiQL = () => {
  require('graphiql/graphiql.css')
  const GraphiQL = require('graphiql')
  return <div style={{
    position: 'fixed',
    top: 64,
    left: 0,
    bottom: 0,
    right: 0
  }}>
    <GraphiQL fetcher={graphQLFetcher} />
  </div>
}
var DevTools

if (process.env.NODE_ENV === 'production') {
  DevTools = () => null
}

if (process.env.NODE_ENV !== 'production') {
  DevTools = require('./containers/dev-tools').default
}

const ConnectedMuiThemeProvider = connect((state) => ({
  muiTheme: getMuiTheme(getTheme(state))
}))(MuiThemeProvider)

const App = ({ children }) => (
  <Provider store={store}>
    <ApolloProvider client={client}>
      <ConnectedMuiThemeProvider>
        <div>
          <Backdrop>
            <AdminAppBar />
            <div style={{ maxWidth: 960, margin: '0 auto', padding: '0 20px' }}>{children}</div>
            <Exception />
          </Backdrop>
          <DevTools />
        </div>
      </ConnectedMuiThemeProvider>
    </ApolloProvider>
  </Provider>
)

export const routes = {
  path: '/',
  component: App,
  indexRoute: { component: Home },
  childRoutes: [
    { path: 'ldap', component: Ldap },
    { path: 'sources', component: Sources(ddfSources) },
    { path: 'web-context-policy-manager', component: Wcpm },
    { path: 'graphiql', component: GraphiQL }
  ]
}

export default () => (
  <Router history={hashHistory} routes={routes} />
)
