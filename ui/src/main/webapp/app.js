import React, { Component } from 'react'
import { Provider, connect } from 'react-redux'
import muiThemeable from 'material-ui/styles/muiThemeable'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'

import store from './store'

import Exception from './containers/exceptions'
import Ldap from './wizards/ldap'
import Sources from './wizards/sources'
import { Home } from './home'
import Wcpm from './adminTools/webContextPolicyManager'

import { Link } from 'react-router'

import AppBar from 'material-ui/AppBar'

import IconButton from 'material-ui/IconButton'
import HomeIcon from 'material-ui/svg-icons/action/home'
import PaletteIcon from 'material-ui/svg-icons/image/palette'
import CloseIcon from 'material-ui/svg-icons/navigation/close'

import Banners from 'system-usage/Banners'
import Modal from 'system-usage/Modal'
import Drawer from 'material-ui/Drawer'
import AdminPalette from 'react-swatch/admin-palette'

import { getTheme } from './reducer'
import { updateThemeColor, setThemePreset } from './actions'

import getMuiTheme from 'material-ui/styles/getMuiTheme'

var DevTools

if (process.env.NODE_ENV === 'production') {
  DevTools = () => null
}

if (process.env.NODE_ENV !== 'production') {
  DevTools = require('./containers/dev-tools').default
}

const LinkHomeIcon = (props) => (
  <Link to='/'>
    <HomeIcon {...props} />
  </Link>
)

const isInIframe = () => {
  return window !== window.top
}

let BackdropView = ({ muiTheme, children, ...rest }) => {
  let fixed = {
    backgroundColor: muiTheme.palette.backdropColor,
    minHeight: '100vh',
    top: 0,
    left: 0,
    bottom: 0,
    right: 0
  }

  if (isInIframe()) {
    fixed.borderRadius = '4px'
  }

  return (
    <div style={fixed} {...rest}>
      {children}
    </div>
  )
}

const Backdrop = muiThemeable()(BackdropView)

const ConnectedMuiThemeProvider = connect((state) => ({
  muiTheme: getMuiTheme(getTheme(state))
}))(MuiThemeProvider)

const getAppBarStyle = () => (
  isInIframe() ? { borderRadius: '4px 4px 0px 0px' } : {}
)

class AppView extends Component {
  constructor (props) {
    super(props)
    this.state = { isArtDrawerOpen: false }
  }

  openArtDrawer () {
    this.setState({ isArtDrawerOpen: true })
  }

  closeArtDrawer () {
    this.setState({ isArtDrawerOpen: false })
  }

  render () {
    const {
      children,
      theme,
      updateColor,
      setThemePreset
    } = this.props
    const {
      isArtDrawerOpen
    } = this.state

    return (
      <Banners>
        <Modal />
        <Backdrop>
          <AppBar
            style={getAppBarStyle()}
            iconElementLeft={
              <IconButton>
                <LinkHomeIcon />
              </IconButton>
            }
            iconElementRight={
              !isInIframe() ? (
                <IconButton onTouchTap={() => this.openArtDrawer()}>
                  <PaletteIcon />
                </IconButton>
              ) : null
            }
          />
          <Drawer width={281} openSecondary open={isArtDrawerOpen}>
            <IconButton onTouchTap={() => this.closeArtDrawer()}>
              <CloseIcon />
            </IconButton>
            <AdminPalette theme={theme} updateColor={updateColor} setThemePreset={setThemePreset} />
          </Drawer>
          <div style={{maxWidth: 960, margin: '0 auto'}}>{children}</div>
          <Exception />
        </Backdrop>
        <DevTools />
      </Banners>
    )
  }
}
const App = connect((state) => ({
  theme: getTheme(state)
}), (dispatch) => ({
  updateColor: (path) => (color) => {
    dispatch(updateThemeColor(path, color))
    window.forceUpdateThemeing()
  },
  setThemePreset: (themeName) => {
    dispatch(setThemePreset(themeName))
    window.forceUpdateThemeing()
  }
}))(AppView)

const ConnectedApp = (props) => (
  <Provider store={store}>
    <ConnectedMuiThemeProvider>
      <App {...props} />
    </ConnectedMuiThemeProvider>
  </Provider>
)

export const routes = {
  path: '/',
  component: ConnectedApp,
  indexRoute: { component: Home },
  childRoutes: [
    { path: 'ldap', component: Ldap },
    { path: 'sources', component: Sources },
    { path: 'web-context-policy-manager', component: Wcpm }
  ]
}
