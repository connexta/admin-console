import React from 'react'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'

import Body from 'components/wizard/Body'
import Navigation, { Back, Next } from 'components/wizard/Navigation'

import { RadioSelection } from 'admin-wizard/inputs'

const LdapTypes = [
  {
    value: 'activeDirectory',
    label: 'Active Directory'
  },
  {
    value: 'openLdap',
    label: 'OpenLDAP'
  },
  {
    value: 'openDj',
    label: 'OpenDJ'
  },
  {
    value: 'embeddedLdap',
    label: 'Embedded LDAP (For testing purposes only)'
  },
  {
    value: 'unknown',
    label: 'Not Sure/None Of The Above'
  }
]

const LdapTypeSelection = ({ disabled, prev, next, configs: { ldapType } = {} }) => (
  <Stage>
    <Title>What type of LDAP are you setting up?</Title>
    <Description>
      This will help us recommend the best options for setting up your LDAP connection.
      If you are unsure, choose 'Not Sure/None of the Above' and we will do our best to
      determine the connection type.
    </Description>
    <Body>
      <RadioSelection
        id='ldapType'
        options={LdapTypes}
        name='LDAP Type Selections'
        disabled={disabled} />
      <Navigation>
        <Back
          onClick={prev}
          disabled={disabled} />
        <Next
          onClick={() => next({
            nextStageId: ldapType === 'embeddedLdap' ? 'configure-embedded-ldap' : 'network-settings'
          })}
          disabled={disabled || !ldapType} />
      </Navigation>
    </Body>
  </Stage>
)

export default LdapTypeSelection
