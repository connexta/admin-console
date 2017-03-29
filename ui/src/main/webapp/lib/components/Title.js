import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

import { title } from './styles.less'

const Title = ({ children, muiTheme }) => (
  <p className={title} style={{ color: muiTheme.palette.textColor }}>{children}</p>
)

export default muiThemeable()(Title)
