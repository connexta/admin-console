import React from 'react'

import Mount from 'react-mount'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'
import Message from 'components/Message'
import Spinner from 'components/Spinner'

const useCaseDescription = (ldapUseCase) => {
  switch (ldapUseCase) {
    case 'authenticationAndAttributeStore':
      return 'authentication source & attribute store'
    case 'authentication' :
      return 'authentication source'
    default:
      return 'credential store'
  }
}

const ConfigureEmbeddedLdap = (props) => {
  const {
    disabled,
    submitting,
    configs: {
      ldapUseCase
    } = {},
    messages = [],

    prev,
    setDefaults,
    persist
  } = props

  return (
    <Stage>
      <Spinner submitting={submitting}>
        <Mount on={setDefaults}
          embeddedLdapPort={1389}
          embeddedLdapsPort={1636}
          embeddedLdapAdminPort={4444}
          embeddedLdapStorageLocation='etc/org.codice.opendj/ldap'
          ldifPath='etc/org.codice.opendj/ldap' />
        <Title>Are You Sure You Want to Install Embedded LDAP?</Title>
        <Description>
          <div> { /* todo - add a 'warning-style' box around this <p/> */ }
            <p>
              The embedded LDAP server is used for testing purposes only and
              should not be used in a production environment.
            </p>
          </div>
          <p>
          Installing Embedded LDAP will start up the internal LDAP and
            configure it as a {useCaseDescription(ldapUseCase)}.
          </p>
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
            nextStageId='final-stage'
            configHandlerId='embedded-ldap'
            configurationType='embedded-ldap'
            persistId='defaults' />
        </ActionGroup>

        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Spinner>
    </Stage>
  )
}

export default ConfigureEmbeddedLdap
