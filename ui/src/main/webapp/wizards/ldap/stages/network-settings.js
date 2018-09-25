import React, { Component } from 'react'

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

import {
    Table,
    TableBody,
    TableHeader,
    TableHeaderColumn,
    TableRow,
    TableRowColumn
} from 'material-ui/Table'

import RaisedButton from 'material-ui/RaisedButton'

import { groupErrors } from './errors'
import {getFriendlyMessage} from 'graphql-errors'

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

class NetworkSettings extends Component {
  constructor (props) {
    super(props)
    this.state = { selected: [] }
  }
  filterUpdateHosts () {
    const {
        onEdit,
        configs: {
          connectionInfo = {}
        } = {}
      } = this.props

    const filtered = Object.keys(connectionInfo).filter((_, i) => {
      return this.state.selected.indexOf(i) === -1
    }).reduce((o, key) => {
      o[key] = connectionInfo[key]
      return o
    }, {})

    onEdit('connectionInfo')(filtered)
    this.setState({ selected: [] })
  }

  render () {
    const {
      client,
      setDefaults,
      prev,
      next,
      configs,
      configs: {
        connectionInfo = {}
      } = {},
      onEdit,
      onError,
      onStartSubmit,
      onEndSubmit
    } = this.props

    const { messages, ...errors } = groupErrors([
      'hostname',
      'port',
      'encryption'
    ], this.props.errors)

    const isPortInvalid = configs.port === undefined || configs.port < 0 || configs.port > 65535

    const missingHost = configs.connectionInfo === undefined || Object.keys(configs.connectionInfo).length === 0

    return (
      <div>
        <Mount
          on={setDefaults}
          port={636}
          encryption='ldaps'
          loadbalancing='roundRobin' />

        <Title>LDAP Network Settings</Title>
        <Description>
          To establish a connection to the remote LDAP store, we need the hostname of the
          LDAP machine, the port number that the LDAP service is running on, and the
          encryption method. Typically, port 636 uses LDAPS encryption and port 389 uses
          StartTLS.  Adding more than one host creates a cluster.  The cluster connections can
          be load balanced between the hosts (round-robin) or can be treated as a failover cluster
          where the first host is considered the primary host.  The configuration of each host
          in the cluster is assumed to be the same (i.e. bind user, directory settings, etc).
        </Description>

        <Body>
          <Hostname
            value={configs.hostname}
            errorText={errors.hostname}
            onEdit={onEdit('hostname')}
            autoFocus />

          <Port
            value={configs.port}
            errorText={isPortInvalid ? getFriendlyMessage('INVALID_PORT_RANGE') : errors.port}
            onEdit={onEdit('port')}
            options={[389, 636]} />

          {messages.map((msg, i) => <Message key={i} {...msg} />)}

          <RaisedButton
            primary
            style={{margin: '0 auto', marginBottom: '30px', marginTop: '10px', display: 'block'}}
            label='Add Host'
            disabled={isPortInvalid}
            onClick={() => {
              onStartSubmit()
              client.query(testConnect({
                hostname: configs.hostname,
                port: configs.port,
                encryption: configs.encryption
              })).then((result) => {
                onEdit({
                  hostname: '',
                  messages: [],
                  connectionInfo: {
                    ...connectionInfo,
                    [Date.now()]: [configs.hostname, configs.port]
                  }
                })
                onError([])
                onEndSubmit()
              }).catch((err) => {
                onError(err)
                onEndSubmit()
              })
            }} />

          <Table multiSelectable onRowSelection={(selected) => this.setState({ selected })}>
            <TableHeader displaySelectAll={false} >
              <TableRow>
                <TableHeaderColumn>Host</TableHeaderColumn>
                <TableHeaderColumn>Port</TableHeaderColumn>
              </TableRow>
            </TableHeader>
            <TableBody showRowHover deselectOnClickaway={false}>
              {Object.keys(connectionInfo).map((connInfo, i) =>
                <TableRow key={i} selected={this.state.selected === 'all' || this.state.selected.indexOf(i) > -1}>
                  <TableRowColumn>{connectionInfo[connInfo][0]}</TableRowColumn>
                  <TableRowColumn>{connectionInfo[connInfo][1]}</TableRowColumn>
                </TableRow>
              )}
            </TableBody>
          </Table>
          <RaisedButton
            label='Remove Selected Hosts'
            primary
            style={{ display: 'block', marginTop: 20 }}
            disabled={this.state.selected.length === 0}
            onClick={this.filterUpdateHosts.bind(this)} />

          <Select
            value={configs.encryption}
            errorText={errors.encryption}
            onEdit={onEdit('encryption')}
            label='Encryption Method'
            options={[ 'none', 'ldaps', 'startTls' ]} />

          <Select
            value={configs.loadbalancing}
            onEdit={onEdit('loadbalancing')}
            label='Load Balancing Algorithm'
            options={[ 'roundRobin', 'failover' ]} />

          <Navigation>
            <Back onClick={prev} />
            <Next disabled={missingHost}
              onClick={() => {
                onStartSubmit()
                onEndSubmit()
                next('bind-settings')
              }}
            />
          </Navigation>
        </Body>
      </div>
    )
  }
}

export default withApollo(NetworkSettings)
