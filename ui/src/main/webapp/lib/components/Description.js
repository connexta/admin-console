import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

const defaultStyle = ({
  fontSize: '14px',
  position: 'relative',
  textAlign: 'center',
  margin: '10px 15px'
})

const Description = ({ children, muiTheme, style = {} }) => (
  <div style={{ color: muiTheme.palette.textColor, ...defaultStyle, ...style }}>{children}</div>
)

export default muiThemeable()(Description)
