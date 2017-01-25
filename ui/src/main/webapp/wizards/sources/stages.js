import React from 'react'
import { connect } from 'react-redux'

import { getSourceSelections, getConfigurationHandlerId, getSourceName, getConfigTypes } from './reducer'
import { getMessages, getAllConfig, getConfig } from '../../reducer'
import { changeStage, testSources, persistConfig, resetSourceWizardState, fetchConfigTypes, testManualUrl } from './actions'

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
  ButtonBox,
  Info,
  CenteredElements,
  ConstrainedSourceInfo,
  SourceRadioButtons,
  NavPanes,
  Submit
} from './components'

import Message from 'components/Message'

// Welcome Stage
const welcomeTitle = 'Welcome to the Source Configuration Wizard'
const welcomeSubtitle = 'This wizard will guide you through discovering and configuring ' +
  "various sources that are used to query metadata in other DDF's or external systems. " +
  'To begin, make sure you have the hostname and port of the source you plan to configure.'

const WelcomeStageView = ({ changeStage }) => (
  <Flexbox className={stageStyle} justifyContent='center' flexDirection='row'>
    <CenteredElements>
      <Info title={welcomeTitle} subtitle={welcomeSubtitle} />
      <Submit label='Begin Source Setup' onClick={() => changeStage('discoveryStage')} />
    </CenteredElements>
  </Flexbox>
)
export const WelcomeStage = connect(null, {
  changeStage: changeStage
})(WelcomeStageView)

// Discovery Stage
const discoveryTitle = 'Discover Available Sources'
const discoverySubtitle = 'Enter source information to scan for available sources'
const discoveryStageDefaults = {
  sourceHostName: 'localhost',
  sourcePort: 8993
}
const DiscoveryStageView = ({ testSources, setDefaults, messages, configs }) => (
  <Mount on={() => setDefaults(discoveryStageDefaults)}>
    <NavPanes backClickTarget='welcomeStage' forwardClickTarget='sourceSelectionStage'>
      <CenteredElements>
        <Info title={discoveryTitle} subtitle={discoverySubtitle} />
        <ConstrainedHostnameInput
          id='sourceHostName'
          label='Hostname'
          errorText={(isEmpty(configs.sourceHostName)) ? 'Cannot be blank' : null} />
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
        <Submit
          label='Check'
          disabled={isEmpty(configs.sourceHostName) ||
            configs.sourcePort < 0 ||
            isEmpty(configs.sourceUserName) !== isEmpty(configs.sourceUserPassword)}
          onClick={() => testSources('/admin/beta/config/probe/sources/discover-sources', 'sources', 'sourceSelectionStage', 'source')} />
      </CenteredElements>
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
const sourceSelectionTitle = 'Sources Found!'
const sourceSelectionSubtitle = 'Choose which source to add'
const noSourcesFoundTitle = 'No Sources Were Found'
const noSourcesFoundSubtitle = 'Click below to enter source information manually, or go back to enter a different hostname/port.'

const SourceSelectionStageView = ({sourceSelections = [], selectedSourceConfigHandlerId, changeStage, fetchConfigTypes}) => {
  if (sourceSelections.length !== 0) {
    return (<NavPanes backClickTarget='discoveryStage' forwardClickTarget='confirmationStage'>
      <CenteredElements>
        <Info title={sourceSelectionTitle} subtitle={sourceSelectionSubtitle} />
        <SourceRadioButtons options={sourceSelections} />
        <Submit label='Next' disabled={selectedSourceConfigHandlerId === undefined} onClick={() => changeStage('confirmationStage')} />
      </CenteredElements>
    </NavPanes>)
  } else {
    return (<NavPanes backClickTarget='discoveryStage' forwardClickTarget='manualEntryStage'>
      <CenteredElements>
        <Info title={noSourcesFoundTitle} subtitle={noSourcesFoundSubtitle} />
        <Submit label='Enter Information Manually' onClick={fetchConfigTypes} />
      </CenteredElements>
    </NavPanes>)
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
const confirmationTitle = 'Finalize Source Configuration'
const confirmationSubtitle = 'Please give your source a unique name, confirm details, and press finish to add source'
const ConfirmationStageView = ({ selectedSource, persistConfig, sourceName, configType }) => (
  <NavPanes backClickTarget='sourceSelectionStage' forwardClickTarget='completedStage'>
    <CenteredElements>
      <Info title={confirmationTitle} subtitle={confirmationSubtitle} />
      <ConstrainedInput id='sourceName' label='Source Name' />
      <ConstrainedSourceInfo label='Source Address' value={selectedSource.endpointUrl} />
      <ConstrainedSourceInfo label='Username' value={selectedSource.sourceUserName || 'none'} />
      <ConstrainedSourceInfo label='Password' value={selectedSource.sourceUserPassword ? '*****' : 'none'} />
      <Submit label='Finish' disabled={sourceName === undefined || sourceName.trim() === ''} onClick={() => persistConfig('/admin/beta/config/persist/' + (selectedSource.configurationHandlerId || configType) + '/create', null, 'completedStage', configType)} />
    </CenteredElements>
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
const completedTitle = 'All Done!'
const completedSubtitle = 'Your source has been added successfully'
const CompletedStageView = ({ resetSourceWizardState }) => (
  <Flexbox className={stageStyle} justifyContent='center' flexDirection='row'>
    <CenteredElements>
      <Info title={completedTitle} subtitle={completedSubtitle} />
      <LargeStatusIndicator success />
      <ButtonBox>
        <Link to='/'>
          <Submit label='Go Home' onClick={resetSourceWizardState} />
        </Link>
        <Submit label='Add Another Source' onClick={resetSourceWizardState} />
      </ButtonBox>
    </CenteredElements>
  </Flexbox>
)
export const CompletedStage = connect(null, { resetSourceWizardState })(CompletedStageView)

// Manual Entry Page
const manualEntryTitle = 'Manual Source Entry'
const manualEntrySubtitle = 'Choose a source configuration type and enter a source URL'
const ManualEntryStageView = ({ configOptions, endpointUrl, configType, testManualUrl }) => (
  <NavPanes backClickTarget='sourceSelectionStage' forwardClickTarget='confirmationStage'>
    <CenteredElements>
      <Info title={manualEntryTitle} subtitle={manualEntrySubtitle} />
      <ConstrainedSelectInput id='manualEntryConfigTypeInput' label='Source Configuration Type' options={configOptions} />
      <ConstrainedInput id='endpointUrl' label='Source URL' />
      <Submit label='Next' onClick={() => testManualUrl(endpointUrl, configType, 'confirmationStage', 'manualEntryStage')} />
    </CenteredElements>
  </NavPanes>
)

export const ManualEntryStage = connect((state) => ({
  configOptions: getConfigTypes(state),
  endpointUrl: getConfig(state, 'endpointUrl').value,
  configType: (getConfig(state, 'manualEntryConfigTypeInput').value || {}).id
}), {
  testManualUrl: testManualUrl
})(ManualEntryStageView)
