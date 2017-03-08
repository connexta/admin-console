import React from 'react'

export default (Component) => ({ visible = true, ...rest }) => {
  if (visible) {
    return (<Component {...rest} />)
  } else {
    return null
  }
}
