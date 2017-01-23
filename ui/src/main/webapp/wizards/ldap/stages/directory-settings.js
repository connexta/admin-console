import React from 'react'

import { connect } from 'react-redux'

import { getProbeValue } from '../../../reducer'

import { List, ListItem } from 'material-ui/List'
import { Card, CardActions, CardHeader } from 'material-ui/Card'
import FlatButton from 'material-ui/FlatButton'

import Mount from 'react-mount'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'
import Spinner from 'components/Spinner'
import Message from 'components/Message'

import { InputAuto } from 'admin-wizard/inputs'

import { probe } from './actions'

import * as styles from './styles.less'

import muiThemeable from 'material-ui/styles/muiThemeable'

const QueryResultView = (props) => {
  const { muiTheme: { palette }, ...rest } = props

  const attrs = Object.keys(rest).map((key, i) =>
    <ListItem key={i}>
      <b style={{ color: palette.primary1Color }}>{key}:</b> {rest[key]}
    </ListItem>
  )

  const { name, uid, cn, ou } = rest

  return (
    <ListItem
      primaryText={ou || cn || uid || name}
      nestedItems={attrs}
      primaryTogglesNestedList />
  )
}

const QueryResult = muiThemeable()(QueryResultView)

const LdapQueryToolView = ({ disabled, probeValue, probe }) => (
  <Card>
    <CardHeader style={{textAlign: 'center', fontSize: '1.1em'}}
      title='LDAP Query Tool'
      subtitle='Execute queries against the connected LDAP'
      actAsExpander
      showExpandableButton
    />
    <CardActions expandable style={{margin: '5px'}}>
      <InputAuto id='query' disabled={disabled} label='Query' />
      <InputAuto id='queryBase' disabled={disabled} label='Query Base DN' />

      <div style={{textAlign: 'right', marginTop: 20}}>
        <FlatButton disabled={disabled} secondary label='run query' onClick={() => probe('/admin/beta/config/probe/ldap/query')} />
      </div>

      {probeValue.length === 0
       ? null
       : (<div className={styles.queryWindow}>
         <h2 className={styles.title}>Query Results</h2>
         <List>
           {probeValue.map((v, i) => <QueryResult key={i} {...v} />)}
         </List>
       </div>)}
    </CardActions>
  </Card>
)

const LdapQueryTool = connect(
  (state) => ({ probeValue: getProbeValue(state) }),
  { probe }
)(LdapQueryToolView)

const DirectorySettings = (props) => {
  const {
    disabled,
    submitting,
    configs: {
      ldapUseCase
    } = {},
    messages = [],

    prev,
    probe,
    test
  } = props

  return (
    <Stage>
      <Spinner submitting={submitting}>
        <Mount on={probe} probeId='dir-struct' />

        <Title>LDAP Directory Structure</Title>
        <Description>
          Next we need to configure the directories to for users/members and the attributes to use.
          Below is the LDAP Query Tool, capable of executing queries against the connected LDAP to discover the required field values
        </Description>
        <InputAuto id='baseUserDn' disabled={disabled} label='Base User DN' />
        <InputAuto id='userNameAttribute' disabled={disabled} label='User Name Attribute' />
        <InputAuto id='baseGroupDn' disabled={disabled} label='Base Group DN' />
        {ldapUseCase === 'loginAndCredentialStore' || ldapUseCase === 'credentialStore'
          ? <div>
            <InputAuto id='groupObjectClass' disabled={disabled} label='LDAP Group ObjectClass' />
            <InputAuto id='membershipAttribute' disabled={disabled} label='LDAP Membership Attribute' />
          </div>
          : null}

        <LdapQueryTool disabled={disabled} />

        <ActionGroup>
          <Action
            secondary
            label='back'
            onClick={prev}
            disabled={disabled} />
          <Action
            primary
            label='next'
            onClick={test}
            disabled={disabled}
            testId='dir-struct'
            nextStageId={ldapUseCase === 'loginAndCredentialStore' || ldapUseCase === 'credentialStore' ? 'attribute-mapping' : 'confirm'} />
        </ActionGroup>

        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Spinner>
    </Stage>
  )
}

export default DirectorySettings
