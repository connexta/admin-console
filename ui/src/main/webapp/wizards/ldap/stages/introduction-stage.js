import React from 'react'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'

const IntroductionStage = ({ disabled, next, configs: { ldapUseCase } = {} }) => (
  <Stage>
    <Title>Welcome to the LDAP Configuration Wizard</Title>
    <Description>
      This wizard will guide you through configuring an LDAP store. An LDAP can be configured
      as an authentication source for users to log in and/or it can be setup as an attribute
      store to provide user attributes to a different authentication source.
    </Description>
    <ActionGroup>
      <Action
        primary
        label='begin'
        onClick={next}
        nextStageId='use-case-stage'
        disabled={disabled} />
    </ActionGroup>
  </Stage>
)

export default IntroductionStage
