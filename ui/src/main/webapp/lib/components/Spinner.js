import React from 'react'

import Flexbox from 'flexbox-react'
import CircularProgress from 'material-ui/CircularProgress'

const styles = {
  submitting: {
    position: 'absolute',
    top: 0,
    bottom: 0,
    right: 0,
    left: 0,
    background: 'rgba(0, 0, 0, 0.1)',
    zIndex: 9001,
    display: 'flex'
  }
}

export default ({ submitting = false, children }) => (
  <div>
    {submitting
      ? <div style={styles.submitting}>
        <Flexbox justifyContent='center' alignItems='center' width='100%'>
          <CircularProgress size={60} thickness={7} />
        </Flexbox>
      </div>
      : null}
    {children}
  </div>
)
