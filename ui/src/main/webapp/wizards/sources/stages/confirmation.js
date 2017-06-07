import React from 'react'
import { connect } from 'react-redux'
import { withApollo } from 'react-apollo'

import { getAllConfig } from 'admin-wizard/reducer'
import { Input } from 'admin-wizard/inputs'

import Info from 'components/Information'
import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'

import { NavPanes } from '../components.js'
import { saveSource } from '../graphql-mutations/source-persist'
import {
  getSourceName,
  getChosenEndpoint,
  getDiscoveredEndpoints,
  getErrors
} from '../reducer'
import {
  changeStage,
  startSubmitting,
  endSubmitting,
  setErrors,
  clearErrors
} from '../actions'

const currentStageId = 'confirmationStage'

const ConfirmationStageView = (props) => {
  const {
    messages,
    sourceName,
    inputConfigs,
    config,
    changeStage
  } = props

  return (
    <NavPanes back='sourceSelectionStage' forward='completedStage'>
      <Title>
        Finalize Source Configuration
      </Title>
      <Description>
        Please give your source a unique name, confirm details, and press finish to create source.
      </Description>
      <div style={{ width: 400, position: 'relative', margin: '0px auto', padding: 0 }}>
        <Input id='sourceName' label='Source Name' autoFocus />
        <Info label='Source Address' value={config.endpointUrl} />
        <Info label='Username' value={inputConfigs.sourceUserName || 'none'} />
        <Info label='Password' value={inputConfigs.sourceUserPassword ? '*****' : 'none'} />
        {messages.map((msg, i) => <Message key={i} message={msg} type='FAILURE' />)}
      </div>
      <ActionGroup>
        <Action
          primary
          label='Finish'
          disabled={sourceName === undefined || sourceName.trim() === ''}
          onClick={() => saveConfiguration(props, () => changeStage('completedStage'))} />
      </ActionGroup>
    </NavPanes>
  )
}

const saveConfiguration = (props, onFinish) => {
  const {
    client,
    type,
    config,
    sourceName,
    inputConfigs,
    startSubmitting,
    endSubmitting,
    setErrors,
    clearErrors
  } = props

  startSubmitting()
  client.mutate(saveSource({
    type,
    config,
    sourceName,
    creds: {
      username: inputConfigs.sourceUserName,
      password: inputConfigs.sourceUserPassword
    }}))
    .then(() => {
      clearErrors()
      if (onFinish) onFinish()
      endSubmitting()
    })
    .catch(() => {
      setErrors(currentStageId, ['Network Error'])
      endSubmitting()
    })
}

let ConfirmationStage = connect((state) => ({
  sourceName: getSourceName(state),
  messages: getErrors(state)(currentStageId),
  inputConfigs: getAllConfig(state),
  type: getChosenEndpoint(state),
  config: getDiscoveredEndpoints(state)[getChosenEndpoint(state)]
}), ({
  changeStage,
  startSubmitting,
  endSubmitting,
  setErrors,
  clearErrors
}))(ConfirmationStageView)

export default withApollo(ConfirmationStage)
