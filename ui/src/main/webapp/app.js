import React from 'react'
import { Provider, connect } from 'react-redux'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'

import store from './store'

import Exception from './containers/exceptions'
import Ldap from './wizards/ldap'
import Sources from './wizards/sources'
import { Home } from './home'
import Wcpm from './adminTools/webContextPolicyManager'

import Banners from 'system-usage/Banners'
import Modal from 'system-usage/Modal'
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
  return <GraphiQL fetcher={graphQLFetcher} />
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
    <ConnectedMuiThemeProvider>
      <Banners>
        <Modal />
        <Backdrop>
          <AdminAppBar />
          <div style={{maxWidth: 960, margin: '0 auto'}}>{children}</div>
          <Exception />
        </Backdrop>
        <DevTools />
      </Banners>
    </ConnectedMuiThemeProvider>
  </Provider>
)

export const routes = {
  path: '/',
  component: App,
  indexRoute: { component: Home },
  childRoutes: [
    { path: 'ldap', component: Ldap },
    { path: 'sources', component: Sources },
    { path: 'web-context-policy-manager', component: Wcpm },
    { path: 'graphiql', component: GraphiQL }
  ]
}
