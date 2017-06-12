import React from 'react'

import Mount from 'react-mount'

import { gql, withApollo } from 'react-apollo'

const testBind = (conn, info) => ({
  fetchPolicy: 'network-only',
  query: gql`
    query TestBind($conn: LdapConnection!, $info: BindUserInfo!) {
      ldap {
        testBind(connection: $conn, bindInfo: $info)
      }
    }
  `,
  variables: { conn, info }
})

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'

import Body from 'components/wizard/Body'
import Navigation, { Back, Next } from 'components/wizard/Navigation'

import {
  Input,
  Password,
  Select
} from 'admin-wizard/inputs'

const BindSettings = (props) => {
  const {
    client,
    onError,
    onStartSubmit,
    onEndSubmit,
    next,
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
    setDefaults
  } = props

  const { bindUserMethod, encryptionMethod } = configs
  let bindUserMethodOptions = ['Simple']

  if (encryptionMethod === 'LDAPS' || encryptionMethod === 'StartTLS') {
    bindUserMethodOptions.push('Digest MD5 SASL')
  }

  return (
    <Stage submitting={submitting}>
      <Mount
        on={setDefaults}
        bindUser={ldapType === 'activeDirectory' ? 'user@domain' : 'cn=admin'}
        bindUserPassword='secret'
        bindUserMethod='Simple' />

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

      <Body>
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
        <Input visible={bindUserMethod === 'Digest MD5 SASL'} id='bindRealm'
          disabled={disabled} label='Realm (for Digest MD5 authentication)' />

        <Navigation>
          <Back
            onClick={prev}
            disabled={disabled} />
          <Next
            onClick={() => {
              onStartSubmit()
              client.query(testBind({
                hostname: configs.hostname,
                port: configs.port,
                encryption: configs.encryption
              }, {
                creds: {
                  username: configs.bindUser,
                  password: configs.bindUserPassword
                },
                bindMethod: configs.bindUserMethod,
                realm: configs.bindRealm
              }))
                .then(() => {
                  onEndSubmit()
                  onError([])
                  next({ nextStageId: 'directory-settings' })
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err.graphQLErrors)
                })
            }}
            disabled={disabled} />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Body>
    </Stage>
  )
}

export default withApollo(BindSettings)

