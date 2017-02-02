import React from 'react'

import Info from 'components/Information'

export default ({label, mapping}) => (<div>
  <Info label={label}
    value={Object.keys(mapping)
            .map((key) => key + ' = ' + mapping[key])} />
</div>
)
