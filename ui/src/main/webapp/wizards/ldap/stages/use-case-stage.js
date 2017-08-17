import React from 'react'

import Title from 'components/Title'
import Description from 'components/Description'

import Body from 'components/wizard/Body'
import Navigation, { Back, Next } from 'components/wizard/Navigation'

import { RadioSelection } from 'admin-wizard/inputs'

const LdapUseCases = [
  {
    value: 'Authentication',
    label: 'Authentication'
  },
  {
    value: 'AttributeStore',
    label: 'Attribute Store'
  },
  {
    value: 'AuthenticationAndAttributeStore',
    label: 'Authentication and Attribute Store'
  }
]

// TODO update description to described LDAP as a login or credential store
// TODO Make the value selected from the radio button persist
const UseCaseStage = ({ onEdit, next, prev, configs: { ldapUseCase } = {} }) => (
  <div>
    <Title>How do you plan to use LDAP?</Title>
    <Description>
      LDAP can be configured as an authentication source for users to log in or it
      can be setup as an attribute store to provide user attributes to a different
      authentication source.
    </Description>
    <Body>
      <RadioSelection
        value={ldapUseCase}
        onEdit={onEdit('ldapUseCase')}
        options={LdapUseCases}
        name='LDAP Use Cases' />
      <Navigation>
        <Back
          onClick={prev} />
        <Next
          onClick={() => next('ldap-type-selection')}
          disabled={!ldapUseCase} />
      </Navigation>
    </Body>
  </div>
)

export default UseCaseStage
