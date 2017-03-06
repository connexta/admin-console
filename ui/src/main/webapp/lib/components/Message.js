import React from 'react'

import * as styles from './styles.less'

export default ({ type, message, children }) => {
  const styleMap = {
    FAILURE: styles.error,
    WARNING: styles.warning,
    SUCCESS: styles.success
  }
  return (
    <div className={styleMap[type] || styleMap.SUCCESS}>
      {message}
      {children}
    </div>
  )
}
