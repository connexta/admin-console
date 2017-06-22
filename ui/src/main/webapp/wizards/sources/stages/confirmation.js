import React from 'react'
import { connect } from 'react-redux'
import { withApollo } from 'react-apollo'

import { getAllConfig } from 'admin-wizard/reducer'
import { Input } from 'admin-wizard/inputs'
import { getFriendlyMessage } from 'graphql-errors'

import Info from 'components/Information'
import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'
import Body from 'components/wizard/Body'
import Navigation, { Finish, Back } from 'components/wizard/Navigation'

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
    changeStage,
    endSubmitting,
    clearErrors,
    startSubmitting,
    setErrors
  } = props

  return (
    <div>
      <Title>
        Finalize Source Configuration
      </Title>
      <Description>
        Please give your source a unique name, confirm details, and press finish to create source.
      </Description>
      <Body>
        <Input id='sourceName' label='Source Name' autoFocus />
        <Info label='Source Address' value={config.endpointUrl} />
        <Info label='Username' value={inputConfigs.sourceUserName || 'none'} />
        <Info label='Password' value={inputConfigs.sourceUserPassword ? '*****' : 'none'} />
        { messages.map((msg, i) => <Message key={i} message={msg} type='FAILURE' />) }
        <Navigation>
          <Back onClick={() => changeStage('sourceSelectionStage')} />
          <Finish
            disabled={sourceName === undefined || sourceName.trim() === ''}
            onClick={() => {
              clearErrors()
              startSubmitting()
              saveSource(props).then(() => {
                changeStage('completedStage', currentStageId)
                endSubmitting()
              }).catch((e) => {
                setErrors(currentStageId, e.graphQLErrors.map((error) =>
                  getFriendlyMessage(error.message)))
                endSubmitting()
              })
            }} />
        </Navigation>
      </Body>
    </div>
  )
}

let ConfirmationStage = connect((state) => ({
  sourceName: getSourceName(state),
  messages: getErrors(state, currentStageId),
  inputConfigs: getAllConfig(state),
  type: getChosenEndpoint(state),
  config: getDiscoveredEndpoints(state)[getChosenEndpoint(state)]
}), {
  changeStage,
  startSubmitting,
  endSubmitting,
  setErrors,
  clearErrors
})(ConfirmationStageView)

export default withApollo(ConfirmationStage)
