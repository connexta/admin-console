import React from 'react'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'

import { RadioSelection } from 'admin-wizard/inputs'

const LdapTypes = [
  {
    value: 'activeDirectory',
    label: 'Active Directory'
  },
  {
    value: 'openDj',
    label: 'OpenDJ'
  },
  {
    value: 'openLdap',
    label: 'OpenLDAP'
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
    <Title>LDAP Type Selection</Title>
    <Description>
      Select the type of LDAP you plan to connect to.
    </Description>
    <RadioSelection
      id='ldapType'
      options={LdapTypes}
      name='LDAP Type Selections'
      disabled={disabled} />
    <ActionGroup>
      <Action
        secondary
        label='back'
        onClick={prev}
        disabled={disabled} />
      <Action
        primary
        label='begin'
        onClick={next}
        disabled={disabled || !ldapType}
        nextStageId={ldapType === 'embeddedLdap' ? 'configure-embedded-ldap' : 'network-settings'} />
    </ActionGroup>
  </Stage>
)

export default LdapTypeSelection
