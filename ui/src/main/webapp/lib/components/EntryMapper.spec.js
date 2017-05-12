import React from 'react'
import { expect } from 'chai'

import { shallow } from 'enzyme'

import Title from './Title'
import Description from 'components/Description'
import EntryMapper from './EntryMapper'

import SelectField from 'material-ui/SelectField'
import AutoComplete from 'material-ui/AutoComplete'
import RaisedButton from 'material-ui/RaisedButton'

import {
  Table,
  TableHeaderColumn
} from 'material-ui/Table'

describe('<EntryMapper />', () => {
  it('should accept a title', () => {
    const title = 'title'
    const wrapper = shallow(<EntryMapper title={title} />)
    expect(wrapper.find(Title).children().text()).to.equal(title)
  })
  it('should accept a description', () => {
    const description = 'description'
    const wrapper = shallow(<EntryMapper description={description} />)
    expect(wrapper.find(Description).children().text()).to.equal(description)
  })
  it('should accept key and value labels', () => {
    const label = 'label'
    const wrapper = shallow(<EntryMapper keyLabel={label} valueLabel={label} />)
    const headers = wrapper.find(TableHeaderColumn)
    expect(headers.length).to.equal(2)
    expect(headers.first().children().text()).to.equal(label)
    expect(headers.last().children().text()).to.equal(label)
  })
  it('should add a new mapping', (done) => {
    const key = 'key'
    const value = 'value'
    const onChange = (mappings) => {
      expect(mappings.length).to.equal(1)
      const { key: k, value: v } = mappings[0]
      expect(k).to.equal(key)
      expect(v).to.equal(value)
      done()
    }
    const wrapper = shallow(<EntryMapper onChange={onChange} />)
    wrapper.find(SelectField).simulate('change', null, null, key)
    wrapper.find(AutoComplete).simulate('updateInput', value)
    wrapper.find(RaisedButton).first().simulate('click')
  })
  it('should update the second mapping', (done) => {
    const values = ['a', 'b', 'c']
    const pair = (value, key) => ({ key, value })
    const mappings = values.map(pair)
    const onChange = (mappings) => {
      expect(mappings.length).to.equal(values.length)
      const result = ['a', 'z', 'c']
      mappings.forEach(({ key, value }) => {
        expect(value).to.equal(result[key])
      })
      done()
    }
    const wrapper = shallow(<EntryMapper mappings={mappings} onChange={onChange} />)
    wrapper.find(SelectField).simulate('change', null, null, 1)
    wrapper.find(AutoComplete).simulate('updateInput', 'z')
    wrapper.find(RaisedButton).first().simulate('click')
  })
  it('should remove the second mapping', (done) => {
    const values = ['a', 'b', 'c']
    const mappings = values.map((value, key) => ({ key, value }))
    const onChange = (mappings) => {
      expect(mappings.length).to.equal(values.length - 1)
      mappings.forEach(({ key, value }) => {
        expect(value).to.not.equal(values[1])
        expect(value).to.equal(values[key])
      })
      done()
    }
    const wrapper = shallow(<EntryMapper mappings={mappings} onChange={onChange} />)
    wrapper.find(Table).simulate('rowSelection', [1])
    wrapper.find(RaisedButton).last().simulate('click')
  })
})
