import React from 'react'

import { gql, withApollo } from 'react-apollo'

import Mount from 'react-mount'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'

import Body from 'components/wizard/Body'
import Navigation, { Back, Next } from 'components/wizard/Navigation'

import {
  Hostname,
  Port,
  Select
} from 'admin-wizard/inputs'

const testConnect = (conn) => ({
  fetchPolicy: 'network-only',
  query: gql`
    query TestConnect($conn: LdapConnection!) {
      ldap {
        testConnect(connection: $conn)
      }
    }
  `,
  variables: { conn }
})

const NetworkSettings = (props) => {
  const {
    client,
    setDefaults,
    prev,
    next,
    submitting,
    disabled,
    configs,
    onError,
    onStartSubmit,
    onEndSubmit,
    messages = []
  } = props

  return (
    <Stage submitting={submitting}>
      <Mount
        on={setDefaults}
        port={636}
        encryption='ldaps' />
      <Title>LDAP Network Settings</Title>
      <Description>
        To establish a connection to the remote LDAP store, we need the hostname of the
        LDAP machine, the port number that the LDAP service is running on, and the
        encryption method. Typically, port 636 uses LDAPS encryption and port 389 uses
        StartTLS.
      </Description>

      <Body>
        <Hostname id='hostname' disabled={disabled} autoFocus />
        <Port id='port' disabled={disabled} options={[389, 636]} />
        <Select id='encryption'
          label='Encryption Method'
          disabled={disabled}
          options={[ 'none', 'ldaps', 'startTls' ]} />

        <Navigation>
          <Back
            onClick={prev}
            disabled={disabled} />
          <Next
            onClick={() => {
              onStartSubmit()
              client.query(testConnect({
                hostname: configs.hostname,
                port: configs.port,
                encryption: configs.encryption
              }))
                .then(() => {
                  onEndSubmit()
                  next({ nextStageId: 'bind-settings' })
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

export default withApollo(NetworkSettings)
