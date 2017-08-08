import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

const styles = {
  title: {
    fontSize: '18px',
    position: 'relative',
    textAlign: 'center',
    margin: '10px 0px'
  }
}

const Title = ({ children, muiTheme }) => (
  <p style={{ ...styles.title, color: muiTheme.palette.textColor }}>
    {children}
  </p>
)

export default muiThemeable()(Title)
