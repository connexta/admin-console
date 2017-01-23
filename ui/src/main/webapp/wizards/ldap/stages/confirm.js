import React from 'react'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'

export default ({ disabled, prev, persist }) => (
  <Stage>
    <Title>LDAP Confirm</Title>

    <Description>
      All of the values have been successfully verified. Would you like to
      save the LDAP configuration?
    </Description>

    <ActionGroup>
      <Action
        secondary
        label='back'
        onClick={prev}
        disabled={disabled} />
      <Action
        primary
        label='save'
        onClick={persist}
        disabled={disabled}
        persistId='create'
        nextStageId='final-stage' />
    </ActionGroup>
  </Stage>
)
