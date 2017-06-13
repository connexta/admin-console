import React from 'react'
import { connect } from 'react-redux'
import { withApollo } from 'react-apollo'

import { queryAllSources } from '../graphql-queries/source-discovery'
import { SourceRadioButtons } from '../components'
import {
  getDiscoveryType,
  getDiscoveredEndpoints,
  getChosenEndpoint
} from '../reducer'
import {
  changeStage,
  setDiscoveredEndpoints,
  setChosenEndpoint,
  startSubmitting,
  endSubmitting,
  setErrors,
  clearErrors
} from '../actions'

import { getAllConfig, getMessages } from 'admin-wizard/reducer'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'
import { Navigator, BackNav, NextNav } from 'components/WizardNavigator'

const currentStageId = 'sourceSelectionStage'

const SourceSelectionStageView = (props) => {
  const {
    messages,
    changeStage,
    discoveredEndpoints = {},
    chosenEndpoint,
    setChosenEndpoint,
    clearErrors
  } = props

  if (Object.keys(discoveredEndpoints).length !== 0) {
    return (
      <div>
        <Title>
          Sources Found!
        </Title>
        <Description>
          Choose which sources to add.
        </Description>
        <div style={{ maxWidth: '600px', margin: '0px auto' }}>
          <SourceRadioButtons
            options={discoveredEndpoints}
            valueSelected={chosenEndpoint}
            onChange={setChosenEndpoint}
          />
          {messages.map((msg, i) => <Message key={i} message={msg} type='FAILURE' />)}
          <Navigator
            max={3}
            value={1}
            left={<BackNav onClick={() => changeStage('discoveryStage')} />}
            right={<NextNav onClick={() => changeStage('confirmationStage')} disabled={chosenEndpoint === ''} />}
          />
        </div>
      </div>
    )
  } else {
    return (
      <div>
        <Title>
          No Sources Found
        </Title>
        <Description>
          No sources were found at the given location. Try again or go back to enter a different address.
          Make sure you entered a valid username and password if the source requires authentication.
        </Description>
        <div style={{ maxWidth: '600px', margin: '0px auto' }}>
          { messages.map((msg, i) => <Message key={i} {...msg} />) }
          <ActionGroup>
            <Action
              primary
              label='Refresh'
              onClick={() => queryAllSources(props,
                () => {
                  clearErrors()
                  changeStage('sourceSelectionStage')
                },
                (e) => setErrors(currentStageId, e))} />
          </ActionGroup>
          <Navigator
            max={3}
            value={1}
            left={<BackNav onClick={() => changeStage('discoveryStage')} />}
            right={<NextNav disabled />}
          />
        </div>
      </div>
    )
  }
}

let SourceSelectionStage = connect((state) => ({
  messages: getMessages(state, 'sourceSelectionStage'),
  discoveryType: getDiscoveryType(state),
  discoveredEndpoints: getDiscoveredEndpoints(state),
  configs: getAllConfig(state),
  chosenEndpoint: getChosenEndpoint(state)
}), {
  changeStage,
  setDiscoveredEndpoints,
  setChosenEndpoint,
  startSubmitting,
  endSubmitting,
  setErrors,
  clearErrors
})(SourceSelectionStageView)

export default withApollo(SourceSelectionStage)
