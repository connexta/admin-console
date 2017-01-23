import React from 'react'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'

import { RadioSelection } from 'admin-wizard/inputs'

const LdapUseCases = [
  {
    value: 'login',
    label: 'Login'
  },
  {
    value: 'credentialStore',
    label: 'Credential store'
  },
  {
    value: 'loginAndCredentialStore',
    label: 'Login and Credential Store'
  }
]

// TODO update description to described LDAP as a login or credential store
// TODO Make the value selected from the radio button persist
const IntroductionStage = ({ disabled, next, configs: { ldapUseCase } = {} }) => (
  <Stage>
    <Title>Welcome to the LDAP Configuration Wizard</Title>
    <Description>
      This guide will walk through setting up the LDAP as an
      authentication source for users. To begin, make sure you
      have the hostname and port of the LDAP you plan to. How
      do you plan to use LDAP?
    </Description>
    <RadioSelection
      id='ldapUseCase'
      options={LdapUseCases}
      name='LDAP Use Cases'
      disabled={disabled} />
    <ActionGroup>
      <Action
        primary
        label='begin'
        onClick={next}
        nextStageId='ldap-type-selection'
        disabled={disabled || !ldapUseCase} />
    </ActionGroup>
  </Stage>
)

export default IntroductionStage
