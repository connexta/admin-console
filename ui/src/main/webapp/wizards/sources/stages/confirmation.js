import React from 'react'
import { connect } from 'react-redux'

import { getSourceName } from '../reducer'
import { getAllConfig, getConfig, getMessages } from 'admin-wizard/reducer'
import { persistConfig } from '../actions'
import { NavPanes } from '../components.js'

import Info from 'components/Information'
import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'

import {
  Input
} from 'admin-wizard/inputs'

const ConfirmationStageView = ({ messages, selectedSource, persistConfig, sourceName, configType }) => (
  <NavPanes backClickTarget='sourceSelectionStage' forwardClickTarget='completedStage'>
    <Title>
      Finalize Source Configuration
    </Title>
    <Description>
      Please give your source a unique name, confirm details, and press finish to create source.
    </Description>
    <div style={{ width: 400, position: 'relative', margin: '0px auto', padding: 0 }}>
      <Input id='sourceName' label='Source Name' autoFocus />
      <Info label='Source Address' value={selectedSource.endpointUrl} />
      <Info label='Username' value={selectedSource.sourceUserName || 'none'} />
      <Info label='Password' value={selectedSource.sourceUserPassword ? '*****' : 'none'} />
      {messages.map((msg, i) => <Message key={i} {...msg} />)}
    </div>
    <ActionGroup>
      <Action
        primary
        label='Finish'
        disabled={sourceName === undefined || sourceName.trim() === ''}
        onClick={() => persistConfig('/admin/beta/config/persist/' + (selectedSource.configurationHandlerId || configType) + '/create', null, 'completedStage', configType, 'confirmationStage')} />
    </ActionGroup>
  </NavPanes>
)
export default connect((state) => ({
  selectedSource: getAllConfig(state),
  sourceName: getSourceName(state),
  configType: (getConfig(state, 'configurationType'))
    ? getConfig(state, 'configurationType').value
    : null,
  messages: getMessages(state, 'confirmationStage')
}), ({
  persistConfig
}))(ConfirmationStageView)
