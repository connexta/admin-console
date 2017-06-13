import React from 'react'
import { connect } from 'react-redux'
import { withApollo } from 'react-apollo'

import { getAllConfig } from 'admin-wizard/reducer'
import { Input } from 'admin-wizard/inputs'

import Info from 'components/Information'
import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'
import { Navigator, BackNav, NextNav } from 'components/WizardNavigator'

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
    <div>
      <Title>
        Finalize Source Configuration
      </Title>
      <Description>
        Please give your source a unique name, confirm details, and press finish to create source.
      </Description>
      <div style={{ maxWidth: 600, margin: '0px auto' }}>
        <Input id='sourceName' label='Source Name' autoFocus />
        <Info label='Source Address' value={config.endpointUrl} />
        <Info label='Username' value={inputConfigs.sourceUserName || 'none'} />
        <Info label='Password' value={inputConfigs.sourceUserPassword ? '*****' : 'none'} />
        { messages.map((msg, i) => <Message key={i} message={msg} type='FAILURE' />) }
        <Navigator
          max={3}
          value={2}
          left={
            <BackNav onClick={() => changeStage('sourceSelectionStage')} />
          }
          right={
            <NextNav label='Finish'
              onClick={() => saveSource(props, () => changeStage('completedStage', currentStageId))}
              disabled={sourceName === undefined || sourceName.trim() === ''} />
          }
        />
      </div>
    </div>
  )
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
