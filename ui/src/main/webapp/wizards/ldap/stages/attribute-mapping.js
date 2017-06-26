import React, { Component } from 'react'

import { connect } from 'react-redux'

import { gql, graphql, withApollo } from 'react-apollo'

import { editConfig } from 'admin-wizard/actions'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'

import Body from 'components/wizard/Body'
import Navigation, { Back, Next } from 'components/wizard/Navigation'

import Message from 'components/Message'

import {Select, InputAuto} from 'admin-wizard/inputs'

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

class AttributeMapperView extends Component {
  constructor (props) {
    super(props)
    this.state = { selected: [] }
  }
  filterUpdateMappings () {
    const {
      editConfig,
      configs: {
        attributeMappings = {}
      } = {}
    } = this.props

    let o = {}

    Object.keys(attributeMappings).filter((_, i) => {
      if (this.state.selected === 'all') {
        return false
      }
      return this.state.selected.indexOf(i) === -1
    }).forEach((key) => {
      o[key] = attributeMappings[key]
    })

    editConfig('attributeMappings', o)
    this.setState({ selected: [] })
  }
  render () {
    const {
      editConfig,
      claims,
      attributes,
      configs: {
        attributeMappings = {},
        subjectClaims,
        userAttributes
      } = {}
    } = this.props

    return (
      <div>
        <Select
          options={claims}
          id='subjectClaims'
          label='STS Claim' />
        <InputAuto
          options={attributes}
          id='userAttributes'
          label='LDAP User Attribute' />

        <RaisedButton
          primary
          style={{margin: '0 auto', marginBottom: '30px', marginTop: '10px', display: 'block'}}
          label='Add Mapping'
          onClick={() => editConfig('attributeMappings', { ...attributeMappings, [subjectClaims]: userAttributes })}
          disabled={subjectClaims === undefined || userAttributes === undefined} />

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
              </TableRow>
            </TableHeader>
            <TableBody showRowHover deselectOnClickaway={false}>
              {Object.keys(attributeMappings).map((subjectClaim, i) =>
                <TableRow key={i} selected={this.state.selected === 'all' || this.state.selected.indexOf(i) > -1}>
                  <TableRowColumn>
                    <span style={{cursor: 'help'}} title={subjectClaim}>{subjectClaim}</span>
                  </TableRowColumn>
                  <TableRowColumn style={{ width: 120 }}>{attributeMappings[subjectClaim]}</TableRowColumn>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Card>

        <RaisedButton
          label='Remove Selected Mappings'
          primary
          style={{display: 'block'}}
          disabled={this.state.selected.length === 0}
          onClick={this.filterUpdateMappings.bind(this)} />
      </div>
    )
  }
}

const AttributeMapper = connect(null, { editConfig })(AttributeMapperView)

const testClaimMappings = (conn, info, userNameAttribute, dn, mapping) => ({
  fetchPolicy: 'network-only',
  query: gql`
    query TestClaimMappings(
      $conn: LdapConnection!,
      $info: BindUserInfo!,
      $userNameAttribute: String!,
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

    disabled,
    submitting,
    configs,
    configs: {
      attributeMappings = {}
    } = {},

    messages = [],

    data: { sts = {}, ldap = {} },

    prev
  } = props

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
    <Stage submitting={submitting}>
      <Title>LDAP User Attribute Mapping</Title>
      <Description>
        In order to authorize users, their attributes must be mapped to the Security Token
        Service (STS) claims. Not all attributes must be mapped but any unmapped attributes
        will not be used for authorization.
      </Description>

      <AttributeMapper
        claims={sts.claims}
        attributes={ldap.userAttributes}
        disabled={disabled}
        configs={configs} />

      <Body>
        <Navigation>
          <Back
            onClick={prev}
            disabled={disabled} />
          <Next
            onClick={() => {
              onStartSubmit()
              client.query(testClaimMappings(conn, info, userNameAttribute, dn, mapping))
                .then(() => {
                  onEndSubmit()
                  next({ nextStageId: 'confirm' })
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err.graphQLErrors)
                })
            }}
            disabled={disabled || Object.keys(attributeMappings).length === 0} />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Body>
    </Stage>
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
