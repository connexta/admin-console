import React from 'react'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'

import { RadioSelection } from 'admin-wizard/inputs'

const LdapUseCases = [
  {
    value: 'authentication',
    label: 'Authentication'
  },
  {
    value: 'attributeStore',
    label: 'Attribute store'
  },
  {
    value: 'authenticationAndAttributeStore',
    label: 'Authentication and Attribute Store'
  }
]

// TODO update description to described LDAP as a login or credential store
// TODO Make the value selected from the radio button persist
const UseCaseStage = ({ disabled, next, configs: { ldapUseCase } = {} }) => (
  <Stage>
    <Title>How do you plan to use LDAP?</Title>
    <Description>
      LDAP can be configured as an authentication source for users to log in or it
      can be setup as an attribute store to provide user attributes to a different
      authentication source.
    </Description>
    <RadioSelection
      id='ldapUseCase'
      options={LdapUseCases}
      name='LDAP Use Cases'
      disabled={disabled} />
    <ActionGroup>
      <Action
        primary
        label='next'
        onClick={next}
        nextStageId='ldap-type-selection'
        disabled={disabled || !ldapUseCase} />
    </ActionGroup>
  </Stage>
)

export default UseCaseStage
