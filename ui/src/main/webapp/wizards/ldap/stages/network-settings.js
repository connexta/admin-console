import React from 'react'

import { gql, withApollo } from 'react-apollo'

import Mount from 'react-mount'

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

const keys = {
  hostname: true,
  port: true,
  encryption: true
}

const NetworkSettings = (props) => {
  const {
    client,
    setDefaults,
    prev,
    next,
    configs,
    onEdit,
    onError,
    onStartSubmit,
    onEndSubmit,
    errors
  } = props

  const messages = errors.filter((err) => {
    const attr = err.path[err.path.length - 1]
    return !keys[attr]
  })

  return (
    <div>
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
        <Hostname
          value={configs.hostname}
          onEdit={onEdit('hostname')}
          autoFocus />

        <Port
          value={configs.port}
          onEdit={onEdit('port')}
          options={[389, 636]} />

        <Select
          value={configs.encryption}
          onEdit={onEdit('encryption')}
          label='Encryption Method'
          options={[ 'none', 'ldaps', 'startTls' ]} />

        <Navigation>
          <Back onClick={prev} />
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
                  next('bind-settings')
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err.graphQLErrors)
                })
            }}
          />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Body>
    </div>
  )
}

export default withApollo(NetworkSettings)
