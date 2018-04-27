import React from 'react'
import { Provider } from 'react-redux'
import { ApolloProvider } from 'react-apollo'
import { Router, hashHistory } from 'react-router'

import {
  getWizard,
  getWcpm,
  getTheme
} from './reducer'

import store from './store'
import client from './client'
import Ldap from './wizards/ldap'
import Sources from './wizards/sources'
import { Home } from './home'
import Wcpm from './security/wcpm'

import ddfSources from 'ddf-sources'
import Backdrop from 'components/Backdrop'
import AdminAppBar from 'admin-app-bar'
import GraphiQL from 'admin-graphiql'
import DevTools from 'admin-redux-devtools'
import MuiThemeProvider from 'admin-app-bar/MuiThemeProvider'

import fonts from 'webpack-fonts'

const App = ({ children }) => (
  <Provider store={store}>
    <ApolloProvider client={client}>
      <MuiThemeProvider rootSelector={getTheme}>
        <div className={fonts.roboto}>
          <Backdrop>
            <AdminAppBar rootSelector={(state) => state.get('theme')} />
            <div style={{ maxWidth: 960, margin: '0 auto', padding: '0 20px' }}>{children}</div>
          </Backdrop>
          <DevTools />
        </div>
      </MuiThemeProvider>
    </ApolloProvider>
  </Provider>
)

const SourcesWizard = Sources(ddfSources)

export const routes = {
  path: '/',
  component: App,
  indexRoute: { component: Home },
  childRoutes: [
    { path: 'ldap', component: () => <Ldap rootSelector={getWizard} /> },
    {
      path: 'sources',
      component: () => {
        return <SourcesWizard rootSelector={getWizard} />
      }
    },
    { path: 'web-context-policy-manager', component: () => <Wcpm rootSelector={getWcpm} /> },
    { path: 'graphiql', component: GraphiQL }
  ]
}

export default () => (
  <Router history={hashHistory} routes={routes} />
)
