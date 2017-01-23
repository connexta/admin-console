import React from 'react'

import FlatButton from 'material-ui/FlatButton'
import RaisedButton from 'material-ui/RaisedButton'

export default (props) => {
  const {
    primary,
    secondary,
    disabled,
    label,
    onClick,
    ...opts
  } = props

  const handleClick = () => {
    if (typeof onClick === 'function') {
      onClick(opts)
    }
  }

  if (primary) {
    return <RaisedButton primary disabled={disabled} label={label} onClick={handleClick} />
  } else if (secondary) {
    return <FlatButton secondary disabled={disabled} label={label} onClick={handleClick} />
  }
}
