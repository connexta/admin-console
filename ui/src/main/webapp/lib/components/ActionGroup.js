import React from 'react'

import Flexbox from 'flexbox-react'

export default ({ children, style = {}, ...rest }) => (
  <Flexbox
    style={{ marginTop: 20, ...style }}
    justifyContent={React.Children.count(children) < 2 ? 'center' : 'space-between'}
    {...rest}>
    {children}
  </Flexbox>
)
