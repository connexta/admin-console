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

import { groupErrors } from './errors'

const BindSettings = (props) => {
  const {
    client,
    onEdit,
    onError,
    onStartSubmit,
    onEndSubmit,
    next,
    // data
    configs: {
      ldapType
    },
    configs = {},

    // actions
    prev,
    setDefaults
  } = props

  const { messages, ...errors } = groupErrors([
    'username',
    'password',
    'bindMethod',
    'realm'
  ], props.errors)

  const { bindUserMethod, encryption } = configs
  let bindUserMethodOptions = ['Simple']

  if (encryption === 'ldaps' || encryption === 'startTls') {
    bindUserMethodOptions.push('DigestMD5SASL')
  }

  return (
    <div>
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
        <Input
          value={configs.bindUser}
          errorText={errors.username}
          onEdit={onEdit('bindUser')}
          label='Bind User' />

        <Password
          value={configs.bindUserPassword}
          errorText={errors.password}
          onEdit={onEdit('bindUserPassword')}
          label='Bind User Password' />

        <Select
          value={configs.bindUserMethod}
          errorText={errors.bindMethod}
          onEdit={onEdit('bindUserMethod')}
          label='Bind User Method'
          options={bindUserMethodOptions} />
        {/* removed options: 'SASL', 'GSSAPI SASL' */}
        {/* TODO GSSAPI SASL only */}
        {/* <Input id='bindKdcAddress' disabled={disabled} label='KDC Address (for Kerberos authentication)' /> */}
        {/* TODO GSSAPI and Digest MD5 SASL only */}
        {/* Realm is needed for Kerberos and MD5 auth, currently only MD5 is supported by the wizard */}
        <Input
          value={configs.bindRealm}
          errorText={errors.realm}
          onEdit={onEdit('bindRealm')}
          visible={bindUserMethod === 'DigestMD5SASL'}
          label='Realm (for Digest MD5 authentication)' />

        <Navigation>
          <Back onClick={prev} />
          <Next
            onClick={() => {
              onStartSubmit()
              var key = Object.keys(configs.connectionInfo)[0]
              var connInfo = configs.connectionInfo[key]
              client.query(testBind({
                hostname: connInfo[0],
                port: connInfo[1],
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
                  next('directory-settings')
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err)
                })
            }}
          />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Body>
    </div>
  )
}

export default withApollo(BindSettings)

