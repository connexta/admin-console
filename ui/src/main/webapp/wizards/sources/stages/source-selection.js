import React from 'react'

import { withApollo } from 'react-apollo'

import Flexbox from 'flexbox-react'

import { queries } from '../graphql'

import RaisedButton from 'material-ui/RaisedButton'

import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'
import Body from 'components/wizard/Body'
import Navigation, { Next, Back } from 'components/wizard/Navigation'

import { SourceRadioButtons } from './components'

const SourceSelectionStage = (props) => {
  const {
    errors: messages = [],
    prev,
    next,
    onStartSubmit,
    onEndSubmit,
    discoveredEndpoints = {},
    setDiscoveredEndpoints,
    chosenEndpoint,
    setChosenEndpoint,
    onError
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
          <Navigation>
            <Back onClick={prev} />
            <Next
              disabled={chosenEndpoint === undefined || chosenEndpoint === ''}
              onClick={() => next('confirmationStage')} />
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
                onStartSubmit()
                queries.queryAllSources(props)
                  .then((endpoints) => {
                    onEndSubmit()
                    setDiscoveredEndpoints(endpoints)
                  })
                  .catch((e) => {
                    onEndSubmit()
                    onError(e)
                  })
              }}
            />
          </Flexbox>
          <Navigation>
            <Back onClick={prev} />
            <Next disabled />
          </Navigation>
          {messages.map((msg, i) => <Message key={i} {...msg} />)}
        </Body>
      </div>
    )
  }
}

export default withApollo(SourceSelectionStage)
