import React from 'react'

import Mount from 'react-mount'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'
import Message from 'components/Message'
import Spinner from 'components/Spinner'

import {
  Input,
  Password,
  Select
} from 'admin-wizard/inputs'

const BindSettings = (props) => {
  const {
    // data
    disabled,
    submitting,
    configs: {
      ldapType
    },
    configs = {},
    messages = [],

    // actions
    prev,
    test,
    setDefaults
  } = props

  const { bindUserMethod, encryptionMethod } = configs
  let bindUserMethodOptions = ['Simple']

  if (encryptionMethod === 'LDAPS' || encryptionMethod === 'StartTLS') {
    bindUserMethodOptions.push('Digest MD5 SASL')
  }

  return (
    <Stage>
      <Mount
        on={setDefaults}
        bindUser={ldapType === 'activeDirectory' ? 'user@domain' : 'cn=admin'}
        bindUserPassword='secret'
        bindUserMethod='Simple' />

      <Spinner submitting={submitting}>
        <Title>LDAP Bind User Settings</Title>
        <Description>
          In order for the system to retrieve information from the LDAP store, it needs to bind
          a user that has permission to search it. This user will be used by the system whenever
          it needs to access the LDAP store.
        </Description>
        <Description> {/* todo - left justify this one? maybe not - test if it looks bad */}
          User credentials may be provided as a DN (distinguished name), User ID, or in the format UserID@Realm.
          The requirements for different LDAP servers vary; please contact your LDAP administrator if you need guidance.
        </Description>

        <Input id='bindUser' disabled={disabled} label='Bind User' />
        <Password id='bindUserPassword' disabled={disabled} label='Bind User Password' />
        <Select id='bindUserMethod'
          label='Bind User Method'
          disabled={disabled}
          options={bindUserMethodOptions} />
        {/* removed options: 'SASL', 'GSSAPI SASL' */}
        {/* TODO GSSAPI SASL only */}
        {/* <Input id='bindKdcAddress' disabled={disabled} label='KDC Address (for Kerberos authentication)' /> */}
        {/* TODO GSSAPI and Digest MD5 SASL only */}
        {/* Realm is needed for Kerberos and MD5 auth, currently only MD5 is supported by the wizard */}
        {
          (bindUserMethod === 'Digest MD5 SASL')
            ? (<Input id='bindRealm' disabled={disabled} label='Realm (for Digest MD5 authentication)' />)
            : null
        }

        <ActionGroup>
          <Action
            secondary
            label='back'
            onClick={prev}
            disabled={disabled} />
          <Action
            primary
            label='next'
            onClick={test}
            disabled={disabled}
            nextStageId='directory-settings'
            testId='bind' />
        </ActionGroup>

        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Spinner>
    </Stage>
  )
}

export default BindSettings

