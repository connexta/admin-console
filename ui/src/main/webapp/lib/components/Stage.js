import React from 'react'

import Paper from 'material-ui/Paper'

import Spinner from './Spinner'

import * as styles from './styles.less'

export default ({ children, submitting }) => (
  <Paper className={styles.main}>
    <Spinner submitting={submitting}>
      <div className={styles.innerSpinner}>
        {children}
      </div>
    </Spinner>
  </Paper>
)

