import React from 'react'

import { connect } from 'react-redux'

import { getProbeValue, setProbeValue } from 'admin-wizard/reducer'

import { gql, graphql, withApollo } from 'react-apollo'

import { List, ListItem } from 'material-ui/List'
import { Card, CardActions, CardHeader } from 'material-ui/Card'
import FlatButton from 'material-ui/FlatButton'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'

import Body from 'components/wizard/Body'
import Navigation, { Back, Next } from 'components/wizard/Navigation'

import { InputAuto } from 'admin-wizard/inputs'

import * as styles from './styles.less'

import muiThemeable from 'material-ui/styles/muiThemeable'

const QueryResultView = (props) => {
  const { muiTheme: { palette }, entries = [] } = props

  const attrs = entries.map(({ key, value }, i) =>
    <ListItem key={i}>
      <b style={{ color: palette.primary1Color }}>{key}:</b> {value}
    </ListItem>
  )

  const primary = [ 'ou', 'cn', 'uid', 'name' ]

  const primaryText = primary
    .map((a) => entries.find(({ key: b }) => a === b))
    .find((value) => value !== undefined)

  return (
    <ListItem
      primaryText={(primaryText || {}).value}
      nestedItems={attrs}
      primaryTogglesNestedList />
  )
}

const QueryResult = muiThemeable()(QueryResultView)

const query = (conn, info, base, query) => ({
  fetchPolicy: 'network-only',
  query: gql`
    query Query($conn: LdapConnection!, $info: BindUserInfo!, $base: DistinguishedName!, $query: LdapQuery!) {
      ldap {
        query(connection: $conn, bindInfo: $info, queryBase: $base, query: $query, maxQueryResults: 25) {
          entries {
            key
            value
          }
        }
      }
    }
  `,
  variables: { conn, info, base, query }
})

const LdapQueryToolView = ({ disabled, options = {}, client, conn, info, configs, state = [], setState, onError }) => (
  <Card>
    <CardHeader style={{textAlign: 'center', fontSize: '1.1em'}}
      title='LDAP Query Tool'
      subtitle='Execute queries against the connected LDAP'
      actAsExpander
      showExpandableButton
    />
    <CardActions expandable style={{margin: '5px'}}>
      <InputAuto id='query' options={['(objectClass=*)']} disabled={disabled} label='Query' />
      <InputAuto id='queryBase' options={options.queryBases} disabled={disabled} label='Query Base DN' />

      <div style={{textAlign: 'right', marginTop: 20}}>
        <FlatButton
          secondary
          label='run query'
          disabled={disabled}
          onClick={() => {
            client.query(query(conn, info, configs.queryBase, configs.query))
              .then(({ data }) => {
                onError([])
                setState(data.ldap.query)
              })
              .catch((err) => onError(err.graphQLErrors))
          }} />
      </div>

        (
        <div className={styles.queryWindow}>
          <Title>Query Results</Title>
          {state.length === 0
                ? 'No results'
                : <List>
                  {state.map((value, i) => <QueryResult key={i} entries={value.entries} />)}
                </List>
            }
        </div>
        )
    </CardActions>
  </Card>
)

const LdapQueryTool = connect(
  (state) => ({ state: getProbeValue(state) }),
  { setState: setProbeValue }
)(withApollo(LdapQueryToolView))

const testDirectorySettings = (conn, info, settings) => ({
  fetchPolicy: 'network-only',
  query: gql`
    query TestDirectorySettings($conn: LdapConnection!, $info: BindUserInfo!, $settings: LdapDirectorySettings!) {
      ldap {
        testDirectorySettings(connection: $conn, bindInfo: $info, directorySettings: $settings)
      }
    }
  `,
  variables: { conn, info, settings }
})

const DirectorySettings = (props) => {
  const {
    client,
    onError,
    onStartSubmit,
    onEndSubmit,
    next,

    disabled,
    submitting,
    configs: {
      ldapUseCase
    } = {},
    messages = [],

    data,
    configs,

    prev
  } = props

  const isAttrStore = ldapUseCase === 'AuthenticationAndAttributeStore' || ldapUseCase === 'AttributeStore'
  const nextStageId = isAttrStore ? 'attribute-mapping' : 'confirm'

  let options = {}

  if (data.ldap) {
    options = data.ldap.recommendedSettings
  }

  const conn = {
    hostname: configs.hostname,
    port: configs.port,
    encryption: configs.encryption
  }

  const info = {
    creds: {
      username: configs.bindUser,
      password: configs.bindUserPassword
    },
    bindMethod: configs.bindUserMethod,
    realm: configs.bindRealm
  }

  const settings = {
    userNameAttribute: configs.userNameAttribute,
    baseUserDn: configs.baseUserDn,
    baseGroupDn: configs.baseGroupDn,
    groupObjectClass: configs.groupObjectClass,
    groupAttributeHoldingMember: configs.groupAttributeHoldingMember,
    memberAttributeReferencedInGroup: configs.memberAttributeReferencedInGroup,
    useCase: configs.ldapUseCase
  }

  return (
    <Stage submitting={submitting}>
      <Title>LDAP Directory Structure</Title>
      <Description>
        Next we need to configure the directories for users/members and decide which attributes to use.
        Default values have been filled out below and some other recommended values are available via each
        field's drop-down menu. This page also has an LDAP Query Tool capable of executing queries
        against the connected LDAP to assist in customizing these settings.
      </Description>

      <Body>
        <InputAuto
          id='baseUserDn'
          label='Base User DN'
          tooltip='Distinguished name of the LDAP directory in which users can be found.'
          disabled={disabled}
          options={options.userDns} />

        <InputAuto
          id='userNameAttribute'
          label='User Name Attribute'
          tooltip='Attribute used to designate the user’s name in LDAP. Typically uid or cn.'
          disabled={disabled}
          options={options.userNameAttributes} />

        <InputAuto
          id='memberAttributeReferencedInGroup'
          label='Member Attribute Referenced in Groups'
          tooltip='The attribute of the user entry that, when combined with the Base User DN, forms the reference value, e.g. XXX=jsmith,ou=users,dc=example,dc=com'
          visible={isAttrStore}
          disabled={disabled}
          options={options.groupAttributesHoldingMember} />

        <InputAuto
          id='baseGroupDn'
          label='Base Group DN'
          tooltip='Distinguished name of the LDAP directory in which groups can be found.'
          disabled={disabled}
          options={options.groupsDns} />

        <InputAuto
          id='groupObjectClass'
          label='LDAP Group ObjectClass'
          tooltip='ObjectClass that defines the structure for group membership in LDAP. Typically groupOfNames.'
          visible={isAttrStore}
          disabled={disabled}
          options={options.groupObjectClasses} />

        <InputAuto
          id='groupAttributeHoldingMember'
          label='Group Attribute Holding Member References'
          tooltip='Multivalued-attribute on the group entry that holds references to users.'
          visible={isAttrStore}
          disabled={disabled}
          options={options.groupAttributesHoldingMember} />

        <LdapQueryTool
          options={options}
          conn={conn}
          info={info}
          configs={configs}
          disabled={disabled}
          onError={onError} />

        <Navigation>
          <Back
            onClick={prev}
            disabled={disabled} />
          <Next
            onClick={() => {
              onStartSubmit()
              client.query(testDirectorySettings(conn, info, settings))
                .then(() => {
                  onEndSubmit()
                  next({ nextStageId })
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err.graphQLErrors)
                })
            }}
            disabled={disabled} />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Body>
    </Stage>
  )
}

export default graphql(gql`
  query RecommendedSettings($conn: LdapConnection!, $info: BindUserInfo!, $type: LdapType!) {
    ldap {
      recommendedSettings(connection: $conn, bindInfo: $info, type: $type) {
        userDns
        groupsDns
        userNameAttributes
        groupAttributesHoldingMember
        groupObjectClasses
        groupAttributesHoldingMember
        queryBases
      }
    }
  }
`, {
  options: ({ configs }) => ({
    variables: {
      conn: {
        hostname: configs.hostname,
        port: configs.port,
        encryption: configs.encryption
      },
      info: {
        creds: {
          username: configs.bindUser,
          password: configs.bindUserPassword
        },
        bindMethod: configs.bindUserMethod,
        realm: configs.bindRealm
      },
      type: configs.ldapType
    }
  })
})(withApollo(DirectorySettings))
