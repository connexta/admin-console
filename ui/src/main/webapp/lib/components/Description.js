import React from 'react'

import { description } from './styles.less'

export default ({ children }) => (
  <p className={description}>{children}</p>
)
