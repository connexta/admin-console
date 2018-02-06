import React from 'react'

import Mount from 'react-mount'
import { withApollo } from 'react-apollo'

import { queries } from '../graphql'

import {
  discoveryStageDisableNext,
  hostnameError,
  userNameError,
  passwordError,
  portError,
  urlError
} from './validation'

import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'
import Body from 'components/wizard/Body'
import Navigation, { Next, Back } from 'components/wizard/Navigation'

import { SideLines } from './components'

import { Input, Password, Hostname, Port } from 'admin-wizard/inputs'

import FlatButton from 'material-ui/FlatButton'

const discoveryStageDefaults = {
  sourceHostName: '',
  sourcePort: 8993
}

const DiscoveryStage = (props) => {
  const {
    errors: messages = [],
    setDefaults,
    configs,
    onEdit,
    onError,
    onStartSubmit,
    onEndSubmit,
    discoveryType,
    setDiscoveryType,
    setDiscoveredEndpoints,
    prev,
    next
  } = props

  return (
    <Mount on={() => setDefaults(discoveryStageDefaults)}>
      <div>
        <Title>
          Discover Available Sources
        </Title>
        <Description>
          Enter connection information to scan for available sources on a host.
        </Description>
        <Body>
          <Hostname
            value={configs.sourceHostName}
            onEdit={onEdit('sourceHostName')}
            visible={discoveryType === 'hostnamePort'}
            label='Host'
            errorText={hostnameError(configs)}
            autoFocus
          />
          <Port
            value={configs.sourcePort}
            onEdit={onEdit('sourcePort')}
            visible={discoveryType === 'hostnamePort'}
            label='Port'
            errorText={portError(configs)} />
          <Input
            visible={discoveryType === 'url'}
            value={configs.endpointUrl}
            onEdit={onEdit('endpointUrl')}
            label='Source URL'
            autoFocus
            errorText={urlError(configs)} />
          {
            (discoveryType === 'hostnamePort') ? (
              <div style={{ textAlign: 'right' }}>
                <FlatButton
                  primary
                  labelStyle={{fontSize: '14px', textTransform: 'none'}}
                  label='Know the source url?'
                  onClick={() => { setDiscoveryType('url') }}
                />
              </div>
            ) : (
              <div style={{ textAlign: 'right' }}>
                <FlatButton
                  primary
                  labelStyle={{fontSize: '14px', textTransform: 'none'}}
                  label={'Don\'t know the source url?'}
                  onClick={() => { setDiscoveryType('hostnamePort') }}
                />
              </div>
            )
          }
          <SideLines label='Authentication (Optional)' />
          <Input
            label='Username'
            value={configs.sourceUserName}
            onEdit={onEdit('sourceUserName')}
            errorText={userNameError(configs)} />
          <Password
            label='Password'
            value={configs.sourceUserPassword}
            onEdit={onEdit('sourceUserPassword')}
            errorText={passwordError(configs)} />
          <Navigation>
            <Back onClick={prev} />
            <Next disabled={discoveryStageDisableNext(props)}
              onClick={() => {
                onStartSubmit()
                queries.queryAllSources(props)
                  .then((endpoints) => {
                    onEndSubmit()
                    setDiscoveredEndpoints(endpoints)
                    next('sourceSelectionStage')
                  })
                  .catch((e) => {
                    onEndSubmit()
                    onError({ graphQLErrors: e })
                  })
              }}
            />
          </Navigation>
          {messages.map((msg, i) => <Message key={i} type='FAILURE' {...msg} />)}
        </Body>
      </div>
    </Mount>
  )
}

export default withApollo(DiscoveryStage)
