import React from 'react'
import { connect } from 'react-redux'

import { getConfigTypes } from '../reducer'
import { getAllConfig, getMessages } from '../../../reducer'
import { testManualUrl } from '../actions'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'

import { NavPanes } from '../components'

import {
  Input,
  Password,
  Select
} from 'admin-wizard/inputs'

const isEmpty = (string) => {
  return !string
}

const isBlank = (string) => {
  return !string || !string.trim()
}

const ManualEntryStageView = ({ configs, messages, configOptions, testManualUrl }) => (
  <NavPanes backClickTarget='sourceSelectionStage' forwardClickTarget='confirmationStage'>
    <Title>
      Manual Source Entry
    </Title>
    <Description>
      Choose a source configuration type and enter a source URL.
    </Description>
    <div style={{ width: 400, position: 'relative', margin: '0px auto', padding: 0 }}>
      <Select id='configType' label='Source Configuration Type' options={configOptions} />
      <Input id='endpointUrl' label='Source URL' />
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
        label='Next'
        disabled={isBlank(configs.configType) ||
        isBlank(configs.endpointUrl) ||
        (isBlank(configs.sourceUserName) !== isEmpty(configs.sourceUserPassword))}
        onClick={() => testManualUrl(configs.endpointUrl, configs.configType, 'confirmationStage', 'manualEntryStage')} />
    </ActionGroup>
  </NavPanes>
)

export default connect((state) => ({
  configs: getAllConfig(state),
  configOptions: getConfigTypes(state),
  messages: getMessages(state, 'manualEntryStage')
}), {
  testManualUrl: testManualUrl
})(ManualEntryStageView)

