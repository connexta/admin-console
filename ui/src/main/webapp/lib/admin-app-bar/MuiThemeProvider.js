import { connect } from 'react-redux'

import getMuiTheme from 'material-ui/styles/getMuiTheme'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'

import { getTheme } from './reducer'

const id = (v) => v

export default connect(
  (root, { rootSelector = id }) => ({
    muiTheme: getMuiTheme(getTheme(rootSelector(root)))
  })
)(MuiThemeProvider)
