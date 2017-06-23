import React from 'react'

export default () => {
  const DevTool = process.env.NODE_ENV === 'production' ? () => null : require('./dev-tools').default
  return <DevTool />
}
