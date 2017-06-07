import React from 'react'
import { connect } from 'react-redux'
import { withApollo } from 'react-apollo'

import { queryAllSources } from './discovery'
import { NavPanes, SourceRadioButtons } from '../components'
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

const SourceSelectionStageView = (props) => {
  const {
    messages,
    changeStage,
    discoveredEndpoints = {},
    chosenEndpoint,
    setChosenEndpoint
  } = props

  if (Object.keys(discoveredEndpoints).length !== 0) {
    return (
      <NavPanes back='discoveryStage' forward='confirmationStage'>
        <Title>
          Sources Found!
        </Title>
        <Description>
          Choose which sources to add.
        </Description>
        <SourceRadioButtons
          options={discoveredEndpoints}
          valueSelected={chosenEndpoint}
          onChange={setChosenEndpoint}
        />
        {messages.map((msg, i) => <Message key={i} message={msg} type='FAILURE' />)}
        <ActionGroup>
          <Action
            primary
            label='Next'
            disabled={chosenEndpoint === ''}
            onClick={() => changeStage('confirmationStage')}
          />
        </ActionGroup>
      </NavPanes>
    )
  } else {
    return (
      <NavPanes back='discoveryStage' forward='manualEntryStage'>
        <Title>
          No Sources Found
        </Title>
        <Description>
          No sources were found at the given location. Try again or go back to enter a different address.
        </Description>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
        <ActionGroup>
          <Action
            primary
            label='Try Again'
            onClick={() => queryAllSources(props)} />
        </ActionGroup>
      </NavPanes>
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
