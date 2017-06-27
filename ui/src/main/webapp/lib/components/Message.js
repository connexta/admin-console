import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

import * as styles from './styles.less'

const Message = ({ type, errorType, code, message, children, muiTheme }) => {
  const styleMap = {
    FAILURE: muiTheme.palette.errorColor,
    WARNING: muiTheme.palette.warningColor,
    SUCCESS: muiTheme.palette.successColor
  }
  return (
    <div className={styles.messageStyle} style={{ color: muiTheme.palette.alternateTextColor, background: styleMap[type || errorType] || muiTheme.palette.errorColor }}>
      {message}
      {code}
      {children}
    </div>
  )
}

export default muiThemeable()(Message)
