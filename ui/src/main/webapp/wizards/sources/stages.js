import React from 'react'
import { connect } from 'react-redux'

import { getSourceSelections, getConfigurationHandlerId, getSourceName, getConfigTypes } from './reducer'
import { getMessages, getAllConfig, getConfig } from '../../reducer'
import { changeStage, testSources, persistConfig, resetSourceWizardState, fetchConfigTypes, testManualUrl } from './actions'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'

import Flexbox from 'flexbox-react'
import { Link } from 'react-router'
import { setDefaults } from '../../actions'
import Mount from 'react-mount'

import LargeStatusIndicator from 'components/LargeStatusIndicator'

import {
  stageStyle
} from './styles.less'

import {
  ConstrainedInput,
  ConstrainedPasswordInput,
  ConstrainedHostnameInput,
  ConstrainedPortInput,
  ConstrainedSelectInput,
  CenteredElements,
  ConstrainedInfo,
  SourceRadioButtons,
  NavPanes
} from './components'

import Message from 'components/Message'

// Welcome Stage
const WelcomeStageView = ({ changeStage }) => (
  <CenteredElements>
    <Title>
      Welcome to the Source Configuration Wizard
    </Title>
    <Description>
      This wizard will guide you through discovering and configuring
      various sources that are used to query metadata from catalogs.
      To begin, make sure you have the hostname and port of the source you plan to configure.
    </Description>
    <ActionGroup>
      <Action
        primary
        label='Begin Source Setup'
        onClick={() => changeStage('discoveryStage')} />
    </ActionGroup>
  </CenteredElements>
)
export const WelcomeStage = connect(null, {
  changeStage: changeStage
})(WelcomeStageView)

// Discovery Stage
const discoveryStageDefaults = {
  sourceHostName: '',
  sourcePort: 8993
}
const DiscoveryStageView = ({ testSources, setDefaults, messages, configs }) => (
  <Mount on={() => setDefaults(discoveryStageDefaults)}>
    <NavPanes backClickTarget='welcomeStage' forwardClickTarget='sourceSelectionStage'>
      <Title>
        Discover Available Sources
      </Title>
      <Description>
        Enter connection information to scan for available sources on a host.
      </Description>
      <ConstrainedHostnameInput
        id='sourceHostName'
        label='Hostname'
        errorText={(configs.sourceHostName) === null ? 'Cannot be blank' : null} />
      <ConstrainedPortInput
        id='sourcePort'
        label='Port'
        errorText={(configs.sourcePort < 0) ? 'Invalid port number' : null} />
      <ConstrainedInput
        id='sourceUserName'
        label='Username (optional)'
        errorText={(isEmpty(configs.sourceUserName) && !isEmpty(configs.sourceUserPassword)) ? 'Password with no username' : null} />
      <ConstrainedPasswordInput
        id='sourceUserPassword'
        label='Password (optional)'
        errorText={(!isEmpty(configs.sourceUserName) && isEmpty(configs.sourceUserPassword)) ? 'Username with no password' : null} />
      {messages.map((msg, i) => <Message key={i} {...msg} />)}

      <ActionGroup>
        <Action
          primary
          label='Check'
          disabled={isEmpty(configs.sourceHostName) ||
            configs.sourcePort < 0 ||
            isEmpty(configs.sourceUserName) !== isEmpty(configs.sourceUserPassword)}
          onClick={() => testSources('/admin/beta/config/probe/sources/discover-sources', 'sources', 'sourceSelectionStage', 'source')} />
      </ActionGroup>
    </NavPanes>
  </Mount>
)
export const DiscoveryStage = connect((state) => ({
  messages: getMessages(state, 'source'),
  configs: getAllConfig(state)
}), {
  testSources,
  setDefaults
})(DiscoveryStageView)

// Source Selection Stage
const SourceSelectionStageView = ({sourceSelections = [], selectedSourceConfigHandlerId, changeStage, fetchConfigTypes}) => {
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
        <ActionGroup>
          <Action primary label='Enter Information Manually' onClick={fetchConfigTypes} />
        </ActionGroup>
      </NavPanes>
    )
  }
}
export const SourceSelectionStage = connect((state) => ({
  sourceSelections: getSourceSelections(state),
  selectedSourceConfigHandlerId: getConfigurationHandlerId(state)
}), {
  changeStage,
  fetchConfigTypes: () => fetchConfigTypes('manualEntryStage')
})(SourceSelectionStageView)

// Confirmation Stage
const ConfirmationStageView = ({ selectedSource, persistConfig, sourceName, configType }) => (
  <NavPanes backClickTarget='sourceSelectionStage' forwardClickTarget='completedStage'>
    <Title>
      Finalize Source Configuration
    </Title>
    <Description>
      Please give your source a unique name, confirm details, and press finish to create source.
    </Description>
    <ConstrainedInput id='sourceName' label='Source Name' />
    <ConstrainedInfo label='Source Address' value={selectedSource.endpointUrl} />
    <ConstrainedInfo label='Username' value={selectedSource.sourceUserName || 'none'} />
    <ConstrainedInfo label='Password' value={selectedSource.sourceUserPassword ? '*****' : 'none'} />
    <ActionGroup>
      <Action
        primary
        label='Finish'
        disabled={sourceName === undefined || sourceName.trim() === ''}
        onClick={() => persistConfig('/admin/beta/config/persist/' + (selectedSource.configurationHandlerId || configType) + '/create', null, 'completedStage', configType)} />
    </ActionGroup>
  </NavPanes>
)
export const ConfirmationStage = connect((state) => ({
  selectedSource: getAllConfig(state),
  sourceName: getSourceName(state),
  configType: (getConfig(state, 'configurationType'))
    ? getConfig(state, 'configurationType').value
    : null
}), ({
  persistConfig
}))(ConfirmationStageView)

const isEmpty = (string) => {
  return !string || !string.trim()
}

// Completed Stage
const CompletedStageView = ({ resetSourceWizardState }) => (
  <Flexbox className={stageStyle} justifyContent='center' flexDirection='row'>
    <CenteredElements>
      <Title>
        All Done!
      </Title>
      <Description>
        Your source has been added successfully.
      </Description>
      <LargeStatusIndicator success />
      <ActionGroup>
        <Link to='/'>
          <Action primary label='Go Home' onClick={resetSourceWizardState} />
        </Link>
        <Action primary label='Add Another Source' onClick={resetSourceWizardState} />
      </ActionGroup>
    </CenteredElements>
  </Flexbox>
)
export const CompletedStage = connect(null, { resetSourceWizardState })(CompletedStageView)

// Manual Entry Page
const ManualEntryStageView = ({ configOptions, endpointUrl, configType, testManualUrl }) => (
  <NavPanes backClickTarget='sourceSelectionStage' forwardClickTarget='confirmationStage'>
    <Title>
      Manual Source Entry
    </Title>
    <Description>
      Choose a source configuration type and enter a source URL.
    </Description>
    <ConstrainedSelectInput id='manualEntryConfigTypeInput' label='Source Configuration Type' options={configOptions} />
    <ConstrainedInput id='endpointUrl' label='Source URL' />
    <ActionGroup>
      <Action
        primary
        label='Next'
        onClick={() => testManualUrl(endpointUrl, configType, 'confirmationStage', 'manualEntryStage')} />
    </ActionGroup>
  </NavPanes>
)

export const ManualEntryStage = connect((state) => ({
  configOptions: getConfigTypes(state),
  endpointUrl: getConfig(state, 'endpointUrl').value,
  configType: (getConfig(state, 'manualEntryConfigTypeInput').value || {}).id
}), {
  testManualUrl: testManualUrl
})(ManualEntryStageView)
