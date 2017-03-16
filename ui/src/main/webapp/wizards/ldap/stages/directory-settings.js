import React from 'react'

import { connect } from 'react-redux'

import { getProbeValue } from 'admin-wizard/reducer'

import { List, ListItem } from 'material-ui/List'
import { Card, CardActions, CardHeader } from 'material-ui/Card'
import FlatButton from 'material-ui/FlatButton'
import NextIcon from 'material-ui/svg-icons/image/navigate-next'

import Mount from 'react-mount'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'
import Message from 'components/Message'
import ActionMessage from 'components/ActionMessage'
import visible from 'react-visible'

import { InputAuto } from 'admin-wizard/inputs'

import { probe } from './actions'

import * as styles from './styles.less'

import muiThemeable from 'material-ui/styles/muiThemeable'

const VisibleActionMessage = visible(ActionMessage)

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

        (
        <div className={styles.queryWindow}>
          <Title>Query Results</Title>
          {probeValue.length === 0
                ? 'No results'
                : <List>
                  {probeValue.map((v, i) => <QueryResult key={i} {...v} />)}
                </List>
            }
        </div>
        )
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
    next,
    probe,
    test,
    allowSkip
  } = props

  const isAttrStore = ldapUseCase === 'authenticationAndAttributeStore' || ldapUseCase === 'attributeStore'
  const nextStageId = isAttrStore ? 'attribute-mapping' : 'confirm'

  return (
    <Stage submitting={submitting}>
      <Mount on={probe} probeId='dir-struct' />

      <Title>LDAP Directory Structure</Title>
      <Description>
        Next we need to configure the directories for users/members and decide which attributes to use.
        Default values have been filled out below and some other recommended values are available via each
        field's drop-down menu. This page also has an LDAP Query Tool capable of executing queries
        against the connected LDAP to assist in customizing these settings.
      </Description>
      <InputAuto id='baseUserDn' disabled={disabled} label='Base User DN'
        tooltip='Distinguished name of the LDAP directory in which users can be found.' />
      <InputAuto id='userNameAttribute' disabled={disabled} label='User Name Attribute'
        tooltip='Attribute used to designate the user’s name in LDAP. Typically uid or cn.' />
      <InputAuto visible={isAttrStore} id='memberAttributeReferencedInGroup' disabled={disabled}
        label='Member Attribute Referenced in Groups'
        tooltip='The attribute of the user entry that, when combined with the Base User DN,
                 forms the reference value, e.g. XXX=jsmith,ou=users,dc=example,dc=com' />
      <InputAuto id='baseGroupDn' disabled={disabled} label='Base Group DN'
        tooltip='Distinguished name of the LDAP directory in which groups can be found.' />
      <InputAuto visible={isAttrStore} id='groupObjectClass' disabled={disabled} label='LDAP Group ObjectClass'
        tooltip='ObjectClass that defines the structure for group membership in LDAP. Typically groupOfNames.' />
      <InputAuto visible={isAttrStore} id='groupAttributeHoldingMember' disabled={disabled}
        label='Group Attribute Holding Member References'
        tooltip='Multivalued-attribute on the group entry that holds references to users.' />

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
          nextStageId={nextStageId} />
      </ActionGroup>

      <VisibleActionMessage visible={allowSkip || false}
        type='WARNING'
        message='There are warnings, would you like to ignore warnings and continue anyway?'
        label='Skip Warnings'
        labelPosition='before'
        icon={<NextIcon />}
        onClick={() => next({ nextStageId })} />

      {messages.map((msg, i) => <Message key={i} {...msg} />)}
    </Stage>
  )
}

export default DirectorySettings
