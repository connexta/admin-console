import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'

import PaletteIcon from 'material-ui/svg-icons/image/palette'
import CloseIcon from 'material-ui/svg-icons/navigation/close'
import HomeIcon from 'material-ui/svg-icons/action/home'

import AppBar from 'material-ui/AppBar'
import IconButton from 'material-ui/IconButton'
import Drawer from 'material-ui/Drawer'

let AdminPalette

if (process.env.NODE_ENV === 'production') {
  AdminPalette = () => null
}

if (process.env.NODE_ENV !== 'production') {
  AdminPalette = require('react-swatch/admin-palette').default
}

import { updateThemeColor, setThemePreset } from './actions'
import { getTheme } from './reducer'

const LinkHomeIcon = (props) => (
  <Link to='/'>
    <HomeIcon {...props} />
  </Link>
)

const isInIframe = () => {
  return window !== window.top
}

const getAppBarStyle = () => (
  isInIframe() ? { borderRadius: '4px 4px 0px 0px' } : {}
)

class AppBarView extends Component {
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
      theme,
      updateColor,
      setThemePreset
    } = this.props
    const {
      isArtDrawerOpen
    } = this.state

    return (
      <div>
        <AppBar
          style={getAppBarStyle()}
          iconElementLeft={
            <IconButton>
              <LinkHomeIcon />
            </IconButton>
          }
          iconElementRight={
            (process.env.NODE_ENV !== 'production') ? (
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
      </div>
    )
  }
}

export default connect((state) => ({
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
}))(AppBarView)
