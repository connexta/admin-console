import React, { Component } from 'react'
import PropTypes from 'prop-types'

import AutoComplete from 'material-ui/AutoComplete'
import MenuItem from 'material-ui/MenuItem'
import RaisedButton from 'material-ui/RaisedButton'
import SelectField from 'material-ui/SelectField'
import { Card, CardHeader } from 'material-ui/Card'

import {
  Table,
  TableBody,
  TableHeader,
  TableHeaderColumn,
  TableRow,
  TableRowColumn
} from 'material-ui/Table'

import Title from 'components/Title'
import Description from 'components/Description'

class EntryMapper extends Component {
  constructor (props) {
    super(props)
    this.state = {
      key: '',
      value: '',
      selected: []
    }
  }
  handleRemove () {
    const { onChange, mappings = [] } = this.props

    onChange(mappings
      .filter((_, i) => !this.state.selected.includes(i)))

    this.setState({ selected: [] })
  }
  handleAdd () {
    const { onChange, mappings = [] } = this.props
    const newPair = {
      key: this.state.key,
      value: this.state.value
    }

    onChange(mappings.filter(({ key }) => key !== newPair.key)
      .concat(newPair))

    this.setState({ key: '', value: '' })
  }
  render () {
    const {
      title,
      description,
      keyLabel,
      valueLabel,

      mappings = [],
      keys = [],
      values = []
    } = this.props

    return (
      <div>
        <SelectField
          fullWidth
          value={this.state.key}
          onChange={(e, i, key) => this.setState({ key })}
          floatingLabelText={keyLabel}>
          {keys.map((claim, i) =>
            <MenuItem key={i} value={claim} primaryText={claim} />)}
        </SelectField>

        <AutoComplete
          menuStyle={{maxHeight: 200, overflowY: 'scroll'}}
          fullWidth
          openOnFocus
          dataSource={values.map((value) => ({text: String(value), value}))}
          filter={AutoComplete.noFilter}
          floatingLabelText={valueLabel}
          searchText={this.state.value}
          onNewRequest={({ value }) => { this.setState({ value }) }}
          onUpdateInput={(value) => { this.setState({ value }) }} />

        <RaisedButton
          primary
          fullWidth
          style={{ margin: '20px 0' }}
          label='Add Entry'
          onClick={this.handleAdd.bind(this)}
          disabled={this.state.key === '' || this.state.value === ''} />

        <Card expanded style={{ width: '100%' }}>
          <CardHeader style={{ fontSize: '0.80em' }}>
            <Title>{title}</Title>
            <Description>{description}</Description>
          </CardHeader>
          <Table multiSelectable onRowSelection={(selected) => this.setState({ selected })}>
            <TableHeader displaySelectAll={false}>
              <TableRow>
                <TableHeaderColumn>{keyLabel}</TableHeaderColumn>
                <TableHeaderColumn style={{ width: 120 }}>{valueLabel}</TableHeaderColumn>
              </TableRow>
            </TableHeader>
            <TableBody key={this.state.selected.join()} showRowHover deselectOnClickaway={false}>
              {mappings.map((mapping, i) =>
                <TableRow key={i} selected={this.state.selected.includes(i)}>
                  <TableRowColumn>
                    <span style={{cursor: 'help'}} title={mapping.key}>{mapping.key}</span>
                  </TableRowColumn>
                  <TableRowColumn style={{ width: 120 }}>{mapping.value}</TableRowColumn>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Card>

        <RaisedButton
          label='Remove Selected Mappings'
          primary
          fullWidth
          disabled={this.state.selected.length === 0}
          onClick={this.handleRemove.bind(this)} />
      </div>
    )
  }
}

EntryMapper.propTypes = {
  onChange: PropTypes.func.isRequired
}

export default EntryMapper
