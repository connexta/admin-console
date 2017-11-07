import React, { Component } from 'react'

import { gql, graphql, withApollo } from 'react-apollo'

import Title from 'components/Title'
import Description from 'components/Description'

import Body from 'components/wizard/Body'
import Navigation, { Back, Next } from 'components/wizard/Navigation'

import Message from 'components/Message'

import { Select, InputAuto } from 'admin-wizard/inputs'

import { Card, CardHeader } from 'material-ui/Card'

import {
  Table,
  TableBody,
  TableHeader,
  TableHeaderColumn,
  TableRow,
  TableRowColumn
} from 'material-ui/Table'

import RaisedButton from 'material-ui/RaisedButton'

import Error from 'material-ui/svg-icons/alert/error'

import muiThemeable from 'material-ui/styles/muiThemeable'

const AttrError = muiThemeable()(
  ({ error, muiTheme }) => (
    error !== undefined
    ? <span title={error}>
      <Error color={muiTheme.palette.errorColor} />
    </span>
    : null
  )
)

class AttributeMapper extends Component {
  constructor (props) {
    super(props)
    this.state = { selected: [] }
  }
  filterUpdateMappings () {
    const {
      onEdit,
      configs: {
        attributeMappings = {}
      } = {}
    } = this.props

    const filtered = Object.keys(attributeMappings).filter((_, i) => {
      if (this.state.selected === 'all') {
        return false
      }
      return this.state.selected.indexOf(i) === -1
    }).reduce((o, key) => {
      o[key] = attributeMappings[key]
      return o
    }, {})

    onEdit('attributeMappings')(filtered)
    this.setState({ selected: [] })
  }
  render () {
    const {
      claims,
      attributes,
      configs: {
        attributeMappings = {},
        subjectClaims = '',
        userAttributes = ''
      } = {},
      onEdit,
      errors
    } = this.props

    return (
      <div>

        <Select
          label='STS Claim'
          value={subjectClaims}
          onEdit={onEdit('subjectClaims')}
          options={claims} />

        <InputAuto
          label='LDAP User Attribute'
          value={userAttributes}
          onEdit={(value) => {
            onEdit('userAttributes')(value.replace(/\s/g, ''))
          }}
          options={attributes} />

        <RaisedButton
          primary
          style={{margin: '0 auto', marginBottom: '30px', marginTop: '10px', display: 'block'}}
          label='Add Mapping'
          onClick={() => {
            onEdit({
              subjectClaims: '',
              userAttributes: '',
              attributeMappings: {
                ...attributeMappings,
                [subjectClaims]: userAttributes
              }
            })
          }}
          disabled={subjectClaims === undefined || userAttributes === undefined || userAttributes === ''} />

        <Card expanded style={{ width: '100%' }}>
          <CardHeader style={{ fontSize: '0.80em' }}>
            <Title>STS Claims to LDAP User Attribute Mapping</Title>
            <Description>
              The mappings below will be saved.
            </Description>
          </CardHeader>
          <Table multiSelectable onRowSelection={(selected) => this.setState({ selected })}>
            <TableHeader displaySelectAll={false} >
              <TableRow>
                <TableHeaderColumn>STS Claim</TableHeaderColumn>
                <TableHeaderColumn style={{ width: 120 }}>LDAP User Attribute</TableHeaderColumn>
                <TableHeaderColumn style={{ width: 32 }}>Errors</TableHeaderColumn>
              </TableRow>
            </TableHeader>
            <TableBody showRowHover deselectOnClickaway={false}>
              {Object.keys(attributeMappings).map((subjectClaim, i) =>
                <TableRow key={i} selected={this.state.selected === 'all' || this.state.selected.indexOf(i) > -1}>
                  <TableRowColumn>
                    <span style={{cursor: 'help'}} title={subjectClaim}>{subjectClaim}</span>
                  </TableRowColumn>
                  <TableRowColumn style={{ width: 120 }}>{attributeMappings[subjectClaim]}</TableRowColumn>
                  <TableRowColumn style={{ width: 32 }}>
                    <AttrError error={errors[i]} />
                  </TableRowColumn>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Card>

        <RaisedButton
          label='Remove Selected Mappings'
          primary
          style={{ display: 'block', marginTop: 20 }}
          disabled={this.state.selected.length === 0}
          onClick={this.filterUpdateMappings.bind(this)} />
      </div>
    )
  }
}

const testClaimMappings = (conn, info, userNameAttribute, dn, mapping) => ({
  fetchPolicy: 'network-only',
  query: gql`
    query TestClaimMappings(
      $conn: LdapConnection!,
      $info: BindUserInfo!,
      $userNameAttribute: LdapAttributeName!,
      $dn: DistinguishedName!
      $mapping: [ClaimsMapEntry]!
    ) {
      ldap {
        testClaimMappings(
          connection: $conn,
          bindInfo: $info,
          userNameAttribute: $userNameAttribute,
          baseUserDn: $dn,
          claimsMapping: $mapping
        )
      }
    }
  `,
  variables: { conn, info, userNameAttribute, dn, mapping }
})

const LdapAttributeMappingStage = (props) => {
  const {
    client,
    onError,
    onStartSubmit,
    onEndSubmit,
    next,

    configs,
    configs: {
      attributeMappings = {}
    } = {},
    onEdit,

    errors,

    data: { sts = {}, ldap = {} },

    prev
  } = props

  const keys = [ 'claimsMapping' ]

  const claimErrors = errors
    .filter((err) => keys.some((key) => err.path.includes(key)))
    .reduce((acc, err) => {
      const index = parseInt(err.path[err.path.length - 2])
      acc[index] = err.message
      return acc
    }, [])

  // filter out unique messages
  const messages = errors.filter((err, i) => {
    const before = errors.slice(0, i).map((err) => err.message)
    return !before.includes(err.message)
  })

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

  const userNameAttribute = configs.userNameAttribute
  const dn = configs.baseUserDn
  const mapping = Object.keys(attributeMappings).map((key) => ({ key, value: attributeMappings[key] }))

  return (
    <div>
      <Title>LDAP User Attribute Mapping</Title>
      <Description>
        In order to authorize users, their attributes must be mapped to the Security Token
        Service (STS) claims. Not all attributes must be mapped but any unmapped attributes
        will not be used for authorization.
      </Description>

      <AttributeMapper
        errors={claimErrors}
        onEdit={onEdit}
        claims={sts.claims}
        attributes={ldap.userAttributes}
        configs={configs} />

      <Body>
        <Navigation>
          <Back onClick={prev} />
          <Next
            onClick={() => {
              onStartSubmit()
              client.query(testClaimMappings(conn, info, userNameAttribute, dn, mapping))
                .then(() => {
                  onEndSubmit()
                  next('confirm')
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err)
                })
            }}
            disabled={Object.keys(attributeMappings).length === 0} />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Body>
    </div>
  )
}

export default graphql(gql`
  query Settings($conn: LdapConnection!, $info: BindUserInfo!, $dn: DistinguishedName!) {
    sts {
      claims
    }
    ldap {
      userAttributes(connection: $conn, bindInfo: $info, baseUserDn: $dn)
    }
  }
`, {
  options: ({ configs }) => ({
    fetchPolicy: 'network-only',
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
      dn: configs.baseUserDn
    }
  })
})(withApollo(LdapAttributeMappingStage))
