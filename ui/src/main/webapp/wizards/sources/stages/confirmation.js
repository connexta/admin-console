import React from 'react'

import { withApollo } from 'react-apollo'

import { Input } from 'admin-wizard/inputs'

import Info from 'components/Information'
import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'
import Body from 'components/wizard/Body'
import Navigation, { Finish, Back } from 'components/wizard/Navigation'

import { mutations } from '../graphql'

const ConfirmationStage = (props) => {
  const {
    errors: messages = [],
    configs,
    prev,
    next,
    onStartSubmit,
    onEndSubmit,
    discoveredEndpoints,
    chosenEndpoint,
    onError,
    onEdit
  } = props

  const config = discoveredEndpoints[chosenEndpoint]
  const sourceName = configs.sourceName

  return (
    <div>
      <Title>
        Finalize Source Configuration
      </Title>
      <Description>
        Please give your source a unique name, confirm details, and press finish to create source.
      </Description>
      <Body>

        <Input
          label='Source Name'
          value={sourceName}
          onEdit={onEdit('sourceName')}
          autoFocus />

        <Info label='Source Address' value={config.endpointUrl} />
        <Info label='Username' value={configs.sourceUserName || 'none'} />
        <Info label='Password' value={configs.sourceUserPassword ? '*****' : 'none'} />

        <Navigation>
          <Back onClick={prev} />
          <Finish
            disabled={sourceName === undefined || sourceName.trim() === ''}
            onClick={() => {
              onStartSubmit()
              mutations.saveSource({ ...props, config, inputConfigs: configs, sourceName })
                .then(() => {
                  onEndSubmit()
                  next('completedStage')
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err)
                })
            }}
          />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} type='FAILURE' {...msg} />)}
      </Body>
    </div>
  )
}

export default withApollo(ConfirmationStage)
