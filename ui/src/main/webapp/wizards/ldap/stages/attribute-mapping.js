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
      setMappedErrors,
      errors,
      configs: {
        attributeMappings = {}
      } = {}
    } = this.props

    const newErrors = errors.slice()
    const filtered = Object.keys(attributeMappings).filter((_, i) => {
      const filter = this.state.selected !== 'all' && this.state.selected.indexOf(i) === -1

      if (!filter) {
        newErrors.splice(i, 1)
      }

      return this.state.selected.indexOf(i) === -1
    }).reduce((o, key) => {
      o[key] = attributeMappings[key]
      return o
    }, {})

    setMappedErrors(newErrors)
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

const testClaimMappings = (conn, info, loginUserAttribute, dn, mapping) => ({
  fetchPolicy: 'network-only',
  query: gql`
    query TestClaimMappings(
      $conn: LdapConnection!,
      $info: BindUserInfo!,
      $loginUserAttribute: LdapAttributeName!,
      $dn: DistinguishedName!
      $mapping: [ClaimsMapEntry]!
    ) {
      ldap {
        testClaimMappings(
          connection: $conn,
          bindInfo: $info,
          loginUserAttribute: $loginUserAttribute,
          baseUserDn: $dn,
          claimsMapping: $mapping
        )
      }
    }
  `,
  variables: { conn, info, loginUserAttribute, dn, mapping }
})

class LdapAttributeMappingStage extends Component {

  constructor (props) {
    super(props)
    this.state = {mappedErrors: []}
  }

  setMappedErrors (errors) {
    this.setState({mappedErrors: errors})
  }

  render () {
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
        } = this.props

        // filter out unique messages
    const messages = errors.filter((err, i) => {
      const before = errors.slice(0, i).map((err) => err.message)
      return !before.includes(err.message)
    })

    const connKey = Object.keys(configs.connectionInfo)[0]
    const connInfo = configs.connectionInfo[connKey]

    const conn = {
      hostname: connInfo[0],
      port: connInfo[1],
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

    const loginUserAttribute = configs.loginUserAttribute
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
          errors={this.state.mappedErrors}
          setMappedErrors={(errs) => this.setMappedErrors(errs)}
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
                client.query(testClaimMappings(conn, info, loginUserAttribute, dn, mapping))
                            .then(() => {
                              onEndSubmit()
                              next('confirm')
                            })
                            .catch((err) => {
                              onEndSubmit()
                              onError(err)
                              mapTableErrors(err, (errs) => this.setMappedErrors(errs))
                            })
              }}
              disabled={Object.keys(attributeMappings).length === 0} />
          </Navigation>
          {messages.map((msg, i) => <Message key={i} {...msg} />)}
        </Body>
      </div>
    )
  }
}

const mapTableErrors = (err = {}, setMappedErrors) => {
  if (Array.isArray(err.graphQLErrors) && err.graphQLErrors.length > 0) {
    const errors = err.graphQLErrors.map(({ message: code, ...rest }) => ({
      message: code,
      ...rest
    }))

    const keys = [ 'claimsMapping' ]

    const mappedErrors = errors
            .filter((err) => keys.some((key) => err.path.includes(key)))
            .reduce((acc, err) => {
              const index = parseInt(err.path[err.path.length - 2])
              acc[index] = err.message
              return acc
            }, [])

    setMappedErrors(mappedErrors)
  }
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
        hostname: configs.connectionInfo[Object.keys(configs.connectionInfo)[0]][0],
        port: configs.connectionInfo[Object.keys(configs.connectionInfo)[0]][1],
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
