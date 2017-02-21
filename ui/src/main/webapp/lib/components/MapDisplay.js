import React from 'react'

import Info from 'components/Information'

export default ({label, mapping, visible = true}) => {
  if (visible) {
    return (
      <div>
        <Info label={label}
          value={Object.keys(mapping)
                .map((key) => key + ' = ' + mapping[key])} />
      </div>
    )
  }
  return null
}
