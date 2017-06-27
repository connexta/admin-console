import { connect } from 'react-redux'

import getMuiTheme from 'material-ui/styles/getMuiTheme'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'

import { getTheme } from './reducer'

export default connect(
  (state) => ({ muiTheme: getMuiTheme(getTheme(state)) })
)(MuiThemeProvider)
