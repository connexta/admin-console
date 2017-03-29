import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

import * as styles from './styles.less'

const Message = ({ type, message, children, muiTheme }) => {
  const styleMap = {
    FAILURE: muiTheme.palette.errorColor,
    WARNING: muiTheme.palette.warningColor,
    SUCCESS: muiTheme.palette.successColor
  }
  return (
    <div className={styles.messageStyle} style={{ color: muiTheme.palette.alternateTextColor, background: styleMap[type] }}>
      {message}
      {children}
    </div>
  )
}

export default muiThemeable()(Message)
