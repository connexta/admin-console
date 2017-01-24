import React from 'react'
import { Provider } from 'react-redux'
import store from './store'

import Exception from './containers/exceptions'
import Ldap from './wizards/ldap'
import Sources from './wizards/sources'
import { Home } from './home'
import Wcpm from './adminTools/webContextPolicyManager'

import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import AppBar from 'material-ui/AppBar'
import Flexbox from 'flexbox-react'

var DevTools

if (process.env.NODE_ENV === 'production') {
  DevTools = () => null
}

if (process.env.NODE_ENV !== 'production') {
  DevTools = require('./containers/dev-tools').default
}

const fixed = {
  position: 'fixed',
  top: 0,
  left: 0,
  bottom: 0,
  right: 0
}

const App = ({ children }) => (
  <MuiThemeProvider>
    <Provider store={store}>
      <div>
        <Flexbox flexDirection='column' height='100vh' style={fixed}>
          <AppBar title='Admin UI (BETA)' />
          <Flexbox flex='1' style={{ overflowY: 'scroll', width: '100%' }}>
            <div style={{ maxWidth: 960, margin: '0 auto' }}>{children}</div>
          </Flexbox>
          <Exception />
        </Flexbox>
        <DevTools />
      </div>
    </Provider>
  </MuiThemeProvider>
)

export const routes = {
  path: '/',
  component: App,
  indexRoute: { component: Home },
  childRoutes: [
    { path: 'ldap', component: Ldap },
    { path: 'sources', component: Sources },
    { path: 'web-context-policy-manager', component: Wcpm }
  ]
}
