import React from 'react'

import muiThemeable from 'material-ui/styles/muiThemeable'

const isInIframe = () => {
  return window !== window.top
}

let BackdropView = ({ muiTheme, children, ...rest }) => {
  let fixed = {
    backgroundColor: muiTheme.palette.backdropColor
  }

  if (isInIframe()) {
    fixed.borderRadius = '4px'
    fixed.height = '100%'
  } else {
    fixed.minHeight = '100vh'
  }

  return (
    <div style={fixed} {...rest}>
      {children}
    </div>
  )
}

export default muiThemeable()(BackdropView)
