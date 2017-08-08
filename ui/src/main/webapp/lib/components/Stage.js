import React from 'react'

import Paper from 'material-ui/Paper'

import Spinner from './Spinner'

const styles = {
  main: {
    margin: '20px 0',
    padding: '0px',
    position: 'relative',
    boxSizing: 'border-box'
  }
}

export default ({ children, submitting }) => (
  <Paper style={styles.main}>
    <Spinner submitting={submitting}>
      <div style={{ padding: '40px', position: 'relative' }}>
        {children}
      </div>
    </Spinner>
  </Paper>
)

