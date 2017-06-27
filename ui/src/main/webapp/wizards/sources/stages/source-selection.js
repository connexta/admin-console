import React from 'react'
import { connect } from 'react-redux'
import { withApollo } from 'react-apollo'

import Flexbox from 'flexbox-react'

import { queryAllSources } from '../graphql-queries/source-discovery'
import { SourceRadioButtons } from '../components'
import {
  getDiscoveryType,
  getDiscoveredEndpoints,
  getChosenEndpoint,
  getErrors
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

import RaisedButton from 'material-ui/RaisedButton'

import { getAllConfig } from 'admin-wizard/reducer'

import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'
import Body from 'components/wizard/Body'
import Navigation, { Next, Back } from 'components/wizard/Navigation'

const currentStageId = 'sourceSelectionStage'

const SourceSelectionStageView = (props) => {
  const {
    messages,
    changeStage,
    discoveredEndpoints = {},
    chosenEndpoint,
    setChosenEndpoint,
    clearErrors,
    setErrors
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
        <Body>
          <SourceRadioButtons
            options={discoveredEndpoints}
            valueSelected={chosenEndpoint}
            onChange={setChosenEndpoint}
          />
          {messages.map((msg, i) => <Message key={i} message={msg} type='FAILURE' />)}
          <Navigation>
            <Back onClick={() => changeStage('discoveryStage')} />
            <Next disabled={chosenEndpoint === ''}
              onClick={() => changeStage('confirmationStage')} />
          </Navigation>
        </Body>
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
        <Body>
          <Flexbox
            style={{ marginTop: 20 }}
            justifyContent='center'>
            <RaisedButton
              primary
              label='Refresh'
              onClick={() => {
                queryAllSources(props)
                  .then((endpoints) => {
                    props.setDiscoveredEndpoints(endpoints)
                    clearErrors()
                  })
                  .catch((e) => {
                    setErrors(currentStageId, e)
                  })
              }} />
          </Flexbox>
          <Navigation>
            <Back onClick={() => changeStage('discoveryStage')} />
            <Next disabled />
          </Navigation>
          { messages.map((msg, i) => <Message key={i} {...msg} />) }
        </Body>
      </div>
    )
  }
}

const SourceSelectionStage = connect((state) => ({
  messages: getErrors(state, currentStageId),
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
