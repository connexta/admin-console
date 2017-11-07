import React from 'react'

import Mount from 'react-mount'

import { gql, withApollo } from 'react-apollo'

import { List, ListItem } from 'material-ui/List'
import { Card, CardActions, CardHeader } from 'material-ui/Card'
import FlatButton from 'material-ui/FlatButton'

import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'

import Body from 'components/wizard/Body'
import Navigation, { Back, Next } from 'components/wizard/Navigation'

import { InputAuto } from 'admin-wizard/inputs'

import { groupErrors } from './errors'

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

const LdapQueryToolView = (props) => {
  const {
    errors,
    options = {},
    setDefaults,
    client,
    onEdit,
    conn,
    info,
    configs,
    state = [],
    setState,
    onError
  } = props

  return (
    <Card>
      <CardHeader style={{textAlign: 'center', fontSize: '1.1em'}}
        title='LDAP Query Tool'
        subtitle='Execute queries against the connected LDAP'
        actAsExpander
        showExpandableButton
      />
      <CardActions expandable style={{margin: '5px'}}>
        <Mount on={setDefaults} query='(objectClass=*)' />

        <InputAuto
          label='Query'
          value={configs.query}
          onEdit={onEdit('query')}
          errorText={errors.query}
          options={['(objectClass=*)']} />

        <InputAuto
          label='Query Base DN'
          value={configs.queryBase}
          onEdit={onEdit('queryBase')}
          errorText={errors.queryBase}
          options={options.queryBases} />

        <div style={{textAlign: 'right', marginTop: 20}}>
          <FlatButton
            secondary
            label='run query'
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
          <div>
            <Title>Query Results</Title>
            {state.length === 0
              ? 'No results'
              : <List>
                {state.map((value, i) => <QueryResult key={i} entries={value.entries} />)}
              </List>}
          </div>
          )
      </CardActions>
    </Card>
  )
}

const LdapQueryTool = withApollo(LdapQueryToolView)

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

const configInputs = [
  {
    key: 'baseUserDn',
    optionKey: 'userDns',
    label: 'Base User DN',
    tooltip: 'Distinguished name of the LDAP directory in which users can be found.'
  },
  {
    key: 'userNameAttribute',
    optionKey: 'userNameAttributes',
    label: 'User Name Attribute',
    tooltip: 'Attribute used to designate the user’s name in LDAP.  Typically uid or cn.'
  },
  {
    key: 'memberAttributeReferencedInGroup',
    optionKey: 'memberAttributesReferencedInGroup',
    label: 'Member Attribute Referenced in Groups',
    tooltip: 'The attribute of the user entry that, when combined with the Base User DN, forms the reference value, e.g. XXX=jsmith,ou=users,dc=example,dc=com',
    attrStoreOnly: true
  },
  {
    key: 'baseGroupDn',
    optionKey: 'groupsDns',
    label: 'Base Group DN',
    tooltip: 'Distinguished name of the LDAP directory in which groups can be found.'
  },
  {
    key: 'groupObjectClass',
    optionKey: 'groupObjectClasses',
    label: 'LDAP Group ObjectClass',
    tooltip: 'ObjectClass that defines the structure for group membership in LDAP. Typically groupOfNames.',
    attrStoreOnly: true
  },
  {
    key: 'groupAttributeHoldingMember',
    optionKey: 'groupAttributesHoldingMember',
    label: 'Group Attribute Holding Member References',
    tooltip: 'Multivalued-attribute on the group entry that holds references to users.',
    attrStoreOnly: true
  },
  {
    key: 'queryBase',
    optionKey: 'queryBases'
  },
  {
    key: 'query'
  }
]

const keys = configInputs.map(({ key }) => key)

const remapKeys = configInputs
  .filter(({ optionKey }) => optionKey !== undefined)
  .reduce((o, { key, optionKey }) => {
    o[optionKey] = key
    return o
  }, {})

const recommendedSettings = (conn, info, type) => ({
  fetchPolicy: 'network-only',
  query: gql`
    query RecommendedSettings($conn: LdapConnection!, $info: BindUserInfo!, $type: LdapType!) {
      ldap {
        recommendedSettings(connection: $conn, bindInfo: $info, type: $type) {
          ${Object.keys(remapKeys).join('\n')}
        }
      }
    }
  `,
  variables: { conn, info, type }
})

const DirectorySettings = (props) => {
  const {
    client,
    onError,
    onStartSubmit,
    onEndSubmit,
    next,

    configs: {
      ldapUseCase
    } = {},

    configs,
    options,
    onEdit,
    setOptions,
    setDefaults,

    results,
    onQuery,

    prev
  } = props

  const { messages, ...errors } = groupErrors(keys, props.errors)

  const isAttrStore = ldapUseCase === 'AuthenticationAndAttributeStore' || ldapUseCase === 'AttributeStore'
  const nextStageId = isAttrStore ? 'attribute-mapping' : 'confirm'

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
    useCase: configs.ldapUseCase
  }

  if (isAttrStore) {
    settings.memberAttributeReferencedInGroup = configs.memberAttributeReferencedInGroup
    settings.groupObjectClass = configs.groupObjectClass
    settings.groupAttributeHoldingMember = configs.groupAttributeHoldingMember
  }

  return (
    <div>
      <Mount on={() => {
        onStartSubmit()
        client.query(recommendedSettings(conn, info, configs.ldapType))
          .then(({ data }) => {
            const settings = data.ldap.recommendedSettings
            onEndSubmit()
            const defaults = Object.keys(settings).reduce((o, key) => {
              if (remapKeys[key] !== undefined) {
                o[remapKeys[key]] = settings[key][0]
              }

              return o
            }, {})
            setDefaults(defaults)
            setOptions(settings)
          })
          .catch((err) => {
            onEndSubmit()
            onError(err.graphQLErrors)
          })
      }} />

      <Title>LDAP Directory Structure</Title>

      <Description>
        Next we need to configure the directories for users/members and decide which attributes to use.
        Default values have been filled out below and some other recommended values are available via each
        field's drop-down menu. This page also has an LDAP Query Tool capable of executing queries
        against the connected LDAP to assist in customizing these settings.
      </Description>

      <Body>
        {configInputs
          .filter(({ label }) => label !== undefined)
          .map(({ key, optionKey, label, tooltip, attrStoreOnly = false }) => (
            <InputAuto
              key={key}
              id={key}
              value={configs[key]}
              onEdit={onEdit(key)}
              errorText={errors[key]}
              label={label}
              tooltip={tooltip}
              visible={!attrStoreOnly || isAttrStore}
              options={options[optionKey]} />
          ))}

        <LdapQueryTool
          errors={errors}
          state={results}
          setState={onQuery}
          setDefaults={setDefaults}
          onEdit={onEdit}
          options={options}
          conn={conn}
          info={info}
          configs={configs}
          onError={onError} />

        <Navigation>
          <Back onClick={prev} />
          <Next
            onClick={() => {
              onStartSubmit()
              client.query(testDirectorySettings(conn, info, settings))
                .then(() => {
                  onEndSubmit()
                  next(nextStageId)
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err.graphQLErrors)
                })
            }}
          />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Body>
    </div>
  )
}

export { DirectorySettings }

export default withApollo(DirectorySettings)
