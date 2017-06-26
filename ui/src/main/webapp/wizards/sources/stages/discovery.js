import React from 'react'
import { connect } from 'react-redux'
import Mount from 'react-mount'
import { withApollo } from 'react-apollo'

import { getDiscoveryType, getErrors } from '../reducer'
import { SideLines } from '../components'
import { queryAllSources } from '../graphql-queries/source-discovery'
import {
  setDiscoveryType,
  changeStage,
  setDiscoveredEndpoints,
  startSubmitting,
  endSubmitting,
  setErrors,
  clearErrors
} from '../actions'
import {
  discoveryStageDisableNext,
  hostnameError,
  userNameError,
  passwordError,
  portError,
  urlError
} from '../validation'

import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'
import Body from 'components/wizard/Body'
import Navigation, { Next, Back } from 'components/wizard/Navigation'

import { getAllConfig } from 'admin-wizard/reducer'
import { setDefaults } from 'admin-wizard/actions'
import { Input, Password, Hostname, Port } from 'admin-wizard/inputs'

import FlatButton from 'material-ui/FlatButton'

const currentStageId = 'discoveryStage'

const discoveryStageDefaults = {
  sourceHostName: '',
  sourcePort: 8993
}

const DiscoveryStageView = (props) => {
  const {
    messages,
    setDefaults,
    configs,
    discoveryType,
    setDiscoveryType,
    changeStage,
    setErrors,
    clearErrors
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
            visible={discoveryType === 'hostnamePort'}
            id='sourceHostName'
            label='Host'
            errorText={hostnameError(configs)}
            autoFocus
          />
          <Port
            visible={discoveryType === 'hostnamePort'}
            id='sourcePort'
            label='Port'
            errorText={portError(configs)} />
          <Input
            visible={discoveryType === 'url'}
            id='endpointUrl'
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
            id='sourceUserName'
            label='Username'
            errorText={userNameError(configs)} />
          <Password
            id='sourceUserPassword'
            label='Password'
            errorText={passwordError(configs)} />
          <Navigation>
            <Back onClick={() => changeStage('welcomeStage')} />
            <Next disabled={discoveryStageDisableNext(props)}
              onClick={() => {
                queryAllSources(props)
                      .then((endpoints) => {
                        props.setDiscoveredEndpoints(endpoints)
                        clearErrors()
                        changeStage('sourceSelectionStage')
                      })
                      .catch((e) => {
                        setErrors(currentStageId, e)
                      })
              }} />
          </Navigation>
          { messages.map((msg, i) => <Message key={i} message={msg} type='FAILURE' />) }
        </Body>
      </div>
    </Mount>
  )
}

let DiscoveryStage = connect((state) => ({
  configs: getAllConfig(state),
  messages: getErrors(state, currentStageId),
  discoveryType: getDiscoveryType(state)
}), {
  setDefaults,
  setDiscoveryType,
  changeStage,
  setDiscoveredEndpoints,
  startSubmitting,
  endSubmitting,
  setErrors,
  clearErrors
})(DiscoveryStageView)

export default withApollo(DiscoveryStage)
