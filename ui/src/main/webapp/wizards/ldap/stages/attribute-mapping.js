import React, { Component } from 'react'

import { connect } from 'react-redux'

import { editConfig } from 'admin-wizard/actions'

import Mount from 'react-mount'

import Stage from 'components/Stage'
import Spinner from 'components/Spinner'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'

import { Select } from 'admin-wizard/inputs'

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
      configs: {
        attributeMappings = {},
        subjectClaims,
        userAttributes
      } = {}
    } = this.props

    return (
      <div>
        <Select
          id='subjectClaims'
          label='STS Claim' />
        <Select
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
            <Title>STS Claims to LDAP Attribute Mapping</Title>
            <Description>
              The mappings below will be saved.
            </Description>
          </CardHeader>
          <Table multiSelectable onRowSelection={(selected) => this.setState({ selected })}>
            <TableHeader>
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

const LdapAttributeMappingStage = (props) => {
  const {
    disabled,
    submitting,
    configs,
    configs: {
      attributeMappings = {}
    } = {},

    prev,
    probe,
    test
  } = props

  return (
    <Stage>
      <Mount probeId='subject-attributes' on={probe} />
      <Spinner submitting={submitting}>
        <Title>LDAP User Attribute Mapping</Title>
        <Description>
          In order to authenticate users, the attributes of the users must be mapped to the STS
          claims.
          Not all attributes must be mapped but any unmapped attributes will not be used for
          authentication.
          Claims can be mapped to 1 or more attributes.
        </Description>

        <AttributeMapper disabled={disabled} configs={configs} />

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
            disabled={disabled || Object.keys(attributeMappings).length === 0}
            testId='attribute-mapping'
            nextStageId='confirm' />
        </ActionGroup>
      </Spinner>
    </Stage>
  )
}

export default LdapAttributeMappingStage
