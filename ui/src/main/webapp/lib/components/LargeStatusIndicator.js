import React from 'react'

import CheckIcon from 'material-ui/svg-icons/action/check-circle'
import CloseIcon from 'material-ui/svg-icons/navigation/cancel'
import muiThemeable from 'material-ui/styles/muiThemeable'
import { center } from './styles.less'

const statusIndicator = ({
  width: '300px',
  height: '300px'
})

const LargeStatusIndicator = ({success, muiTheme}) => (
  <div className={center}>
    {(success)
      ? <CheckIcon style={statusIndicator} color={muiTheme.palette.successColor} />
      : <CloseIcon style={statusIndicator} color={muiTheme.palette.errorColor} />
    }
  </div>
)

export default muiThemeable()(LargeStatusIndicator)
