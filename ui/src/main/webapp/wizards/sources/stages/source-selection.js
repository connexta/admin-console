import React from 'react'
import { connect } from 'react-redux'

import { getSourceSelections, getConfigurationHandlerId } from '../reducer'
import { getMessages } from '../../../reducer'
import { changeStage, fetchConfigTypes } from '../actions'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'

import { NavPanes, SourceRadioButtons } from '../components'

const SourceSelectionStageView = ({ messages, sourceSelections = [], selectedSourceConfigHandlerId, changeStage, fetchConfigTypes }) => {
  if (sourceSelections.length !== 0) {
    return (
      <NavPanes backClickTarget='discoveryStage' forwardClickTarget='confirmationStage'>
        <Title>
          Sources Found!
        </Title>
        <Description>
          Choose which sources to add.
        </Description>
        <SourceRadioButtons options={sourceSelections} />
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
        <ActionGroup>
          <Action primary label='Next' disabled={selectedSourceConfigHandlerId === undefined} onClick={() => changeStage('confirmationStage')} />
        </ActionGroup>
      </NavPanes>
    )
  } else {
    return (
      <NavPanes backClickTarget='discoveryStage' forwardClickTarget='manualEntryStage'>
        <Title>
          No Sources Found
        </Title>
        <Description>
          Click below to enter source information manually, or go back to enter a different hostname/port.
        </Description>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
        <ActionGroup>
          <Action primary label='Enter Information Manually' onClick={fetchConfigTypes} />
        </ActionGroup>
      </NavPanes>
    )
  }
}
export default connect((state) => ({
  sourceSelections: getSourceSelections(state),
  selectedSourceConfigHandlerId: getConfigurationHandlerId(state),
  messages: getMessages(state, 'sourceSelectionStage')
}), {
  changeStage,
  fetchConfigTypes: () => fetchConfigTypes('manualEntryStage', 'sourceSelectionStage')
})(SourceSelectionStageView)
