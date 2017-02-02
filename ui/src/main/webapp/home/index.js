import React from 'react'

import { connect } from 'react-redux'
import { Map } from 'immutable'
import Flexbox from 'flexbox-react'
import { Link } from 'react-router'

import { poll, stopPolling } from 'redux-polling'
import Mount from 'react-mount'

import { get, post } from 'redux-fetch'

import Paper from 'material-ui/Paper'
import AccountIcon from 'material-ui/svg-icons/action/account-circle'
import LanguageIcon from 'material-ui/svg-icons/action/language'
import VpnLockIcon from 'material-ui/svg-icons/notification/vpn-lock'
import Divider from 'material-ui/Divider'
import { cyan500 } from 'material-ui/styles/colors'
import RaisedButton from 'material-ui/RaisedButton'
import MapDisplay from 'components/MapDisplay'

import * as styles from './styles.less'

// actions

const setConfigs = (id, value) => ({type: 'SET_CONFIGS', id, value})

// async actions

const retrieve = (id) => async (dispatch) => {
  const res = await dispatch(get('/admin/beta/config/configurations/' + id))
  const json = await res.json()

  if (res.status === 200) {
    dispatch(setConfigs(id, json))
  }
}

const refresh = poll('home', () => (dispatch) =>
  Promise.all([
    dispatch(retrieve('sources')),
    dispatch(retrieve('ldap'))
  ])
)

export const deleteConfig = ({ configurationType, factoryPid, servicePid }) => async (dispatch) => {
  const url = '/admin/beta/config/persist/' + configurationType + '/delete'
  const body = JSON.stringify({ configurationType, factoryPid, servicePid })

  const res = await dispatch(post(url, { body }))

  if (res.status === 200) {
    dispatch(refresh())
  }
}

// selectors

const getSourceConfigs = (state) => state.getIn(['home', 'sources'])
const getLdapConfigs = (state) => state.getIn(['home', 'ldap'])

// reducers

const configs = (state = Map(), { type, id, value = [] }) => {
  switch (type) {
    case 'SET_CONFIGS':
      return state.set(id, value)
    default:
      return state
  }
}

export default configs

// views
const getSourceTypeFromFactoryPid = (factoryPid) => {
  return factoryPid.replace(/_/g, ' ')
}

const SourceTileView = (props) => {
  const {
    sourceName,
    factoryPid,
    sourceUserName,
    endpointUrl,
    onDeleteConfig
  } = props

  return (
    <Paper className={styles.config}>
      <div className={styles.tileTitle}>{sourceName}</div>
      <ConfigField fieldName='Source Type' value={getSourceTypeFromFactoryPid(factoryPid)} />
      <ConfigField fieldName='Endpoint' value={endpointUrl} />
      <ConfigField fieldName='Username' value={sourceUserName || 'none'} />
      <ConfigField fieldName='Password' value={sourceUserName || '******'} />
      <RaisedButton style={{marginTop: 20}} label='Delete' secondary onClick={onDeleteConfig} />
    </Paper>
  )
}

export const SourceTile = connect(null, (dispatch, props) => ({
  onDeleteConfig: () => dispatch(deleteConfig(props))
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
    attributeMappings
  } = props

  return (
    <Paper className={styles.config}>
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
      <RaisedButton style={{marginTop: 20}} label='Delete' secondary onClick={onDeleteConfig} />
    </Paper>
  )
}
export const LdapTile = connect(null, (dispatch, props) => ({
  onDeleteConfig: () => dispatch(deleteConfig(props))
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

const SourceConfigTiles = ({ sourceConfigs }) => {
  if (sourceConfigs.length === 0) {
    return <div style={{margin: '20px'}}>No Sources Configured </div>
  }

  return (
    <Flexbox flexDirection='row' flexWrap='wrap' style={{width: '100%'}}>
      {sourceConfigs.map((v, i) => (<SourceTile key={i} {...v} />))}
    </Flexbox>
  )
}

const LdapConfigTiles = ({ ldapConfigs }) => {
  if (ldapConfigs.length === 0) {
    return <div style={{margin: '20px'}}>No LDAP's Configured</div>
  }

  return (
    <Flexbox flexDirection='row' flexWrap='wrap' style={{width: '100%'}}>
      {ldapConfigs.map((v, i) => (<LdapTile key={i} {...v} />))}
    </Flexbox>
  )
}

const Title = ({ children }) => (
  <h1 className={styles.title}>{children}</h1>
)

const SourcesHomeView = ({ sourceConfigs = [], ldapConfigs = [], onRefresh, offRefresh }) => (
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
    <SourceConfigTiles sourceConfigs={sourceConfigs} />

    <Divider />

    <Title>LDAP Configurations</Title>
    <LdapConfigTiles ldapConfigs={ldapConfigs} />
  </div>
)

export const Home = connect((state) => ({
  sourceConfigs: getSourceConfigs(state),
  ldapConfigs: getLdapConfigs(state)
}), { onRefresh: refresh, offRefresh: stopPolling })(SourcesHomeView)

