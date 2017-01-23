import React from 'react'

import * as styles from './styles.less'

export default ({ type, message }) => (
  <div className={type === 'FAILURE' ? styles.error : styles.success}>{message}</div>
)
