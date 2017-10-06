import React from 'react'
import ReactDOM from 'react-dom'

import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'

import Reporter from './reporter'
import { withRunner } from './runner'

const MochaReporter = withRunner(window.mocha)(Reporter)

ReactDOM.render(
  <MuiThemeProvider>
    <MochaReporter />
  </MuiThemeProvider>,
  document.getElementById('mocha'))
