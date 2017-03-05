import React from 'react'

import { connect } from 'react-redux'
import Flexbox from 'flexbox-react'
import { Link } from 'react-router'

import { stopPolling } from 'redux-polling'
import Mount from 'react-mount'

import Paper from 'material-ui/Paper'
import AccountIcon from 'material-ui/svg-icons/action/account-circle'
import LanguageIcon from 'material-ui/svg-icons/action/language'
import VpnLockIcon from 'material-ui/svg-icons/notification/vpn-lock'
import Divider from 'material-ui/Divider'
import { cyan500 } from 'material-ui/styles/colors'
import RaisedButton from 'material-ui/RaisedButton'
import MapDisplay from 'components/MapDisplay'
import Spinner from 'components/Spinner'
import confirmable from 'react-confirmable'

import * as selectors from './reducer'
import * as actions from './actions'
import * as styles from './styles.less'

const ConfirmableButton = confirmable(RaisedButton)

const getSourceTypeFromFactoryPid = (factoryPid) => {
  return factoryPid.replace(/_/g, ' ')
}

const SourceTileView = (props) => {
  const {
    sourceName,
    factoryPid,
    sourceUserName,
    endpointUrl,
    onDeleteConfig,
    submitting,
    messages = []
  } = props

  return (
    <Paper className={styles.outerSpinner}>
      <Spinner submitting={submitting}>
        <div className={styles.innerSpinner}>
          <div className={styles.tileTitle}>{sourceName}</div>
          <ConfigField fieldName='Source Type' value={getSourceTypeFromFactoryPid(factoryPid)} />
          <ConfigField fieldName='Endpoint' value={endpointUrl} />
          <ConfigField fieldName='Username' value={sourceUserName || 'none'} />
          <ConfigField fieldName='Password' value={sourceUserName || '******'} />
          <div style={{ textAlign: 'center' }}>
            <ConfirmableButton confirmableMessage='Confirm delete?' style={{ marginTop: 20 }}
              label='Delete' secondary onClick={onDeleteConfig} />
          </div>
          {messages.map((message, i) => (
            <Flexbox key={i} flexDirection='row' justifyContent='center' className={styles.error}>{message.message}</Flexbox>))}
        </div>
      </Spinner>
    </Paper>
  )
}

export const SourceTile = connect((state, { servicePid }) => ({
  messages: selectors.getConfigErrors(state, servicePid)
}), (dispatch, props) => ({
  onDeleteConfig: () => dispatch(actions.deleteConfig({configurationHandlerId: props.configurationType, ...props, id: 'sources'}))
}))(SourceTileView)

const LdapTileView = (props) => {
  const {
    hostName,
    port,
    encryptionMethod,
    bindUser,
    userNameAttribute,
    baseGroupDn,
    baseUserDn,
    onDeleteConfig,
    ldapUseCase,
    attributeMappings,
    submitting,
    messages = []
  } = props

  return (
    <Paper className={styles.outerSpinner}>
      <Spinner submitting={submitting}>
        <div className={styles.innerSpinner}>
          <div className={styles.tileTitle}>{ldapUseCase === 'authentication' ? 'LDAP Authentication Source' : 'LDAP Attribute Store'}</div>
          <ConfigField fieldName='Hostname' value={hostName} />
          <ConfigField fieldName='Port' value={port} />
          <ConfigField fieldName='Encryption Method' value={encryptionMethod} />
          <ConfigField fieldName='Bind User' value={bindUser} />
          <ConfigField fieldName='Bind User Password' value='******' />
          <ConfigField fieldName='UserName Attribute' value={userNameAttribute} />
          <ConfigField fieldName='Base Group DN' value={baseGroupDn} />
          <ConfigField fieldName='Base User DN' value={baseUserDn} />
          { ldapUseCase !== 'authentication'
            ? (
              <MapDisplay label='Attribute Mappings'
                mapping={attributeMappings} />
          ) : null }
          <div style={{ textAlign: 'center' }}>
            <ConfirmableButton confirmableMessage='Confirm delete?' style={{ marginTop: 20 }}
              label='Delete' secondary onClick={onDeleteConfig} />
          </div>
          {messages.map((message, i) => (
            <Flexbox key={i} flexDirection='row' justifyContent='center' className={styles.error}>{message.message}</Flexbox>))}
        </div>
      </Spinner>
    </Paper>
  )
}
export const LdapTile = connect((state, { servicePid }) => ({
  messages: selectors.getConfigErrors(state, servicePid)
}), (dispatch, props) => ({
  onDeleteConfig: () => dispatch(actions.deleteConfig({configurationHandlerId: props.configurationType, ...props, id: 'sources'}))
}))(LdapTileView)

const TileLink = ({ to, title, subtitle, children }) => (
  <div>
    <Link to={to}>
      <Paper className={styles.main}>
        <div style={{width: '100%', height: '100%'}}>
          <Flexbox
            alignItems='center'
            flexDirection='column'
            justifyContent='center'
            style={{width: '100%', height: '100%'}}>

            <p className={styles.titleTitle}>{title}</p>
            {children}
            <p className={styles.tileSubtitle}>{subtitle}</p>

          </Flexbox>
        </div>
      </Paper>
    </Link>
  </div>
)

const ConfigField = ({fieldName, value}) => (
  <div className={styles.configField}>{fieldName}: {value}</div>
)

const SourceConfigTiles = ({ sourceConfigs, submittingPids }) => {
  if (sourceConfigs.length === 0) {
    return <div style={{margin: '20px'}}>No Sources Configured </div>
  }

  return (
    <Flexbox flexDirection='row' flexWrap='wrap' style={{width: '100%'}}>
      {sourceConfigs.map((v, i) => (<SourceTile key={i} {...v} submitting={submittingPids[v.servicePid]} />))}
    </Flexbox>
  )
}

const LdapConfigTiles = ({ ldapConfigs, submittingPids }) => {
  if (ldapConfigs.length === 0) {
    return <div style={{margin: '20px'}}>No LDAP Servers Configured</div>
  }

  return (
    <Flexbox flexDirection='row' flexWrap='wrap' style={{width: '100%'}}>
      {ldapConfigs.map((v, i) => (<LdapTile key={i} {...v} submitting={submittingPids[v.servicePid]} />))}
    </Flexbox>
  )
}

const Title = ({ children }) => (
  <h1 className={styles.title}>{children}</h1>
)

const SourcesHomeView = ({ sourceConfigs = [], ldapConfigs = [], submittingPids = {}, onRefresh, offRefresh }) => (
  <div style={{width: '100%'}}>
    <Mount on={onRefresh} off={offRefresh} id='home' />

    <Title>
      Setup Wizards
    </Title>

    <Flexbox flexDirection='row' flexWrap='wrap' style={{width: '100%'}}>
      <TileLink
        to='/sources'
        title='Source Setup Wizard'
        subtitle='Setup a new source for federating'>
        <LanguageIcon style={{color: cyan500, width: '50%', height: '50%'}} />
      </TileLink>

      <TileLink
        to='/web-context-policy-manager'
        title='Endpoint Security'
        subtitle='Web context policy management'>
        <VpnLockIcon style={{color: cyan500, width: '50%', height: '50%'}} />
      </TileLink>

      <TileLink
        to='/ldap'
        title='LDAP Setup Wizard'
        subtitle='Configure LDAP as a login'>
        <AccountIcon style={{color: cyan500, width: '50%', height: '50%'}} />
      </TileLink>
    </Flexbox>

    <Divider />

    <Title>Source Configurations</Title>
    <SourceConfigTiles sourceConfigs={sourceConfigs} submittingPids={submittingPids} />

    <Divider />

    <Title>LDAP Configurations</Title>
    <LdapConfigTiles ldapConfigs={ldapConfigs} submittingPids={submittingPids} />
  </div>
)

export const Home = connect((state) => ({
  sourceConfigs: selectors.getSourceConfigs(state),
  ldapConfigs: selectors.getLdapConfigs(state),
  submittingPids: selectors.getSubmittingPids(state)
}), { onRefresh: actions.refresh, offRefresh: stopPolling })(SourcesHomeView)

