import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

const styles = {
  message: {
    marginTop: '20px',
    padding: '10px',
    boxSizing: 'border-box',
    boxShadow: 'rgba(0, 0, 0, 0.117647) 0px 1px 6px, rgba(0, 0, 0, 0.117647) 0px 1px 4px',
    borderRadius: '2px',
    fontSize: '14px',
    fontWeight: 'bold'
  }
}

const Message = ({ type, errorType, code, message, children, muiTheme }) => {
  const colorMap = {
    FAILURE: muiTheme.palette.errorColor,
    WARNING: muiTheme.palette.warningColor,
    SUCCESS: muiTheme.palette.successColor
  }
  return (
    <div style={{
      ...styles.message,
      color: muiTheme.palette.alternateTextColor,
      background: colorMap[type || errorType] || muiTheme.palette.errorColor
    }}>
      {message}
      {code}
      {children}
    </div>
  )
}

export default muiThemeable()(Message)
