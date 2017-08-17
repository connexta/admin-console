import React from 'react'

import Mount from 'react-mount'

export default (props) => {
  const {
    wizardId,
    stageId,
    stages,
    local,
    setLocal,
    clearLocal,
    shared,
    setShared,
    clearShared,
    ...rest
  } = props

  const Component = stages[stageId]

  return (
    <Mount key={wizardId} off={clearShared}>
      <Mount key={stageId} off={clearLocal}>
        {(stages[stageId] !== undefined)
          ? <Component
            local={local}
            setLocal={setLocal}
            shared={shared}
            setShared={setShared}
            restart={clearShared}
            {...rest} />
          : <div>Cannot Find stage with id = {stageId}</div>}
      </Mount>
    </Mount>
  )
}
