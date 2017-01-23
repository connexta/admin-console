import React from 'react'

import Flexbox from 'flexbox-react'
import CircularProgress from 'material-ui/CircularProgress'

import * as styles from './styles.less'

export default ({ submitting = false, children }) => (
  <div>
    {submitting
      ? <div className={styles.submitting}>
        <Flexbox justifyContent='center' alignItems='center' width='100%'>
          <CircularProgress size={60} thickness={7} />
        </Flexbox>
      </div>
      : null}
    {children}
  </div>
)
