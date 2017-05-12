import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

import { description } from './styles.less'

const Description = ({ children, muiTheme, style = {} }) => (
  <div className={description} style={{ color: muiTheme.palette.textColor, ...style }}>{children}</div>
)

export default muiThemeable()(Description)
