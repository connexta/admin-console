import React from 'react'
import { connect } from 'react-redux'
import Mount from 'react-mount'
import { withApollo } from 'react-apollo'

import { getDiscoveryType, getErrors } from '../reducer'
import { NavPanes, SideLines } from '../components'
import { setDefaults } from '../../../actions'
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
  nextShouldBeDisabled,
  userNameError,
  passwordError,
  portError
} from '../validation'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'

import { getAllConfig } from 'admin-wizard/reducer'
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
    changeStage
  } = props

  return (
    <Mount on={() => setDefaults(discoveryStageDefaults)}>
      <NavPanes back='welcomeStage' forward='sourceSelectionStage'>
        <Title>
          Discover Available Sources
        </Title>
        <Description>
          Enter connection information to scan for available sources on a host.
        </Description>
        <div style={{ width: 400, position: 'relative', margin: '0px auto', padding: 0 }}>

          <Hostname
            visible={discoveryType === 'hostnamePort'}
            id='sourceHostName'
            label='Host'
            autoFocus />
          <Port
            visible={discoveryType === 'hostnamePort'}
            id='sourcePort'
            label='Port'
            errorText={portError(configs)} />
          <Input
            visible={discoveryType === 'url'}
            id='endpointUrl'
            label='Source URL'
            autoFocus />
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
          {messages.map((msg, i) => <Message key={i} message={msg} type='FAILURE' />)}
          <ActionGroup>
            <Action
              primary
              label='Check'
              disabled={nextShouldBeDisabled(props)}
              onClick={() => queryAllSources(props, () => changeStage('sourceSelectionStage'))} />
          </ActionGroup>
        </div>
      </NavPanes>
    </Mount>
  )
}

let DiscoveryStage = connect((state) => ({
  configs: getAllConfig(state),
  messages: getErrors(state)(currentStageId),
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
