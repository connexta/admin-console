import React from 'react'

import Paper from 'material-ui/Paper'

import * as styles from './styles.less'

export default ({ children }) => (
  <Paper className={styles.main}>
    {children}
  </Paper>
)

