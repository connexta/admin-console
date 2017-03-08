import React from 'react'
import Flexbox from 'flexbox-react'

import FlatButton from 'material-ui/FlatButton'

import Message from 'components/Message'

export default ({ type, message, ...rest }) => {
  return (
    <Message type={type} >
      <Flexbox flexDirection='row' alignItems='center' justifyContent='space-between'>
        <div>{message}</div>
        <FlatButton {...rest} />
      </Flexbox>
    </Message>
  )
}
