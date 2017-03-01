import React from 'react'
import { connect } from 'react-redux'

import { getAllConfig, getMessages } from '../../../reducer'
import { testSources } from '../actions'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'

import {
  Input,
  Password,
  Hostname,
  Port
} from 'admin-wizard/inputs'

import { NavPanes } from '../components'

import { setDefaults } from '../../../actions'
import Mount from 'react-mount'

const isEmpty = (string) => {
  return !string
}

const isBlank = (string) => {
  return !string || !string.trim()
}

const discoveryStageDefaults = {
  sourceHostName: '',
  sourcePort: 8993
}

const DiscoveryStageView = ({ messages, testSources, setDefaults, configs }) => (
  <Mount on={() => setDefaults(discoveryStageDefaults)}>
    <NavPanes backClickTarget='welcomeStage' forwardClickTarget='sourceSelectionStage'>
      <Title>
        Discover Available Sources
      </Title>
      <Description>
        Enter connection information to scan for available sources on a host.
      </Description>
      <div style={{ width: 400, position: 'relative', margin: '0px auto', padding: 0 }}>
        <Hostname
          id='sourceHostName'
          label='Hostname'
          autoFocus />
        <Port
          id='sourcePort'
          label='Port'
          errorText={(configs.sourcePort < 0) ? 'Port is not in valid range.' : undefined} />
        <Input
          id='sourceUserName'
          label='Username (optional)'
          errorText={(isBlank(configs.sourceUserName) && !isEmpty(configs.sourceUserPassword)) ? 'Password with no username.' : undefined} />
        <Password
          id='sourceUserPassword'
          label='Password (optional)'
          errorText={(!isBlank(configs.sourceUserName) && isEmpty(configs.sourceUserPassword)) ? 'Username with no password.' : undefined} />
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </div>

      <ActionGroup>
        <Action
          primary
          label='Check'
          disabled={isBlank(configs.sourceHostName) ||
          configs.sourcePort < 0 ||
          isBlank(configs.sourceUserName) !== isEmpty(configs.sourceUserPassword)}
          onClick={() => testSources('sources', 'sourceSelectionStage', 'discoveryStage')} />
      </ActionGroup>
    </NavPanes>
  </Mount>
)

export default connect((state) => ({
  configs: getAllConfig(state),
  messages: getMessages(state, 'discoveryStage')
}), {
  testSources,
  setDefaults
})(DiscoveryStageView)
