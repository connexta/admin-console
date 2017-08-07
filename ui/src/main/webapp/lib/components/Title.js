import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

const titleStyle = ({
  fontSize: '18px',
  position: 'relative',
  textAlign: 'center',
  margin: '10px 0px'
})

const Title = ({ children, muiTheme }) => (
  <p style={{ ...titleStyle, color: muiTheme.palette.textColor }}>
    {children}
  </p>
)

export default muiThemeable()(Title)
