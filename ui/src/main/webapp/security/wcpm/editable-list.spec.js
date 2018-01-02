import React from 'react'
import { expect } from 'chai'

import { shallow, mount } from 'enzyme'

import TextField from 'material-ui/TextField'
import MTP from 'material-ui/styles/MuiThemeProvider'

import EditableList from './editable-list'

import { RemoveButton } from './components'

import { List } from 'immutable'

describe('<EditableList />', () => {
  it('should only render the input field with an empty list', () => {
    const wrapper = shallow(<EditableList list={[]} />)
    expect(wrapper.find(TextField)).to.have.length(1)
    expect(wrapper.find(RemoveButton)).to.have.length(0)
  })
  it('should render a <TextField /> for each item in the list', () => {
    const list = ['a', 'b', 'c']
    const wrapper = shallow(<EditableList list={list} />)
    expect(wrapper.find(TextField)).to.have.length(list.length + 1)
    expect(wrapper.find(RemoveButton)).to.have.length(list.length)
  })
  it('should focus new item on enter', (done) => {
    const list = ['a']
    const wrapper = mount(<MTP><EditableList list={list} /></MTP>)
    const first = wrapper.find(TextField).first()
    const last = wrapper.find(TextField).last()
    // if the input node gets focused, the test has passed
    // otherwise it will timeout and fail
    last.node.focus = done
    first.find('input').simulate('keyPress', { key: 'Enter' })
  })
  it('should focus new item on delete on current line', (done) => {
    const list = ['a']
    const onChange = () => {}
    const wrapper = mount(<MTP><EditableList onChange={onChange} list={list} /></MTP>)
    const first = wrapper.find(TextField).first()
    const last = wrapper.find(TextField).last()
    // if the input node gets focused, the test has passed
    // otherwise it will timeout and fail
    last.node.focus = done
    first.find('input').simulate('change', { target: { value: '' } })
  })
  it('should render errors correctly', () => {
    const list = ['a']
    const errors = ['an error occurred']
    const wrapper = shallow(<EditableList errors={errors} list={list} />)
    const field = wrapper.find(TextField).first()
    expect(field.prop('errorText')).to.equal(errors[0])
  })
  it('should render `hintText` correctly', () => {
    const hintText = 'abc'
    const wrapper = shallow(<EditableList list={[]} hintText={hintText} />)
    const field = wrapper.find(TextField).first()
    expect(field.prop('hintText')).to.equal(hintText)
  })
  it('should auto add new items', (done) => {
    const value = 'a'
    const onChange = ({ value: v, index }) => {
      expect(v).to.equal(value)
      expect(index).to.equal(0)
      done()
    }
    const wrapper = shallow(<EditableList onChange={onChange} list={[]} />)
    wrapper.find(TextField).simulate('change', { target: { value } })
  })
  it('should remove the first item', (done) => {
    const list = ['a', 'b', 'c']
    const onChange = ({ value, index }) => {
      expect(value).to.equal(undefined)
      expect(index).to.equal(0)
      done()
    }
    const wrapper = shallow(<EditableList onChange={onChange} list={list} />)
    wrapper.find(RemoveButton).first().simulate('remove')
  })
  it('should remove the last item', (done) => {
    const list = ['a', 'b', 'c']
    const onChange = ({ value, index }) => {
      expect(value).to.equal(undefined)
      expect(index).to.equal(list.length - 1)
      done()
    }
    const wrapper = shallow(<EditableList onChange={onChange} list={list} />)
    wrapper.find(RemoveButton).last().simulate('remove')
  })
  it('should render with an immutable List', () => {
    const list = List.of('a', 'b', 'c')
    const wrapper = shallow(<EditableList list={list} />)
    expect(wrapper.find(TextField)).to.have.length(list.size + 1)
    expect(wrapper.find(RemoveButton)).to.have.length(list.size)
  })
  it('should render with an immutable List of errors', () => {
    const list = ['a']
    const errors = List.of('an error occurred')
    const wrapper = shallow(<EditableList errors={errors} list={list} />)
    const field = wrapper.find(TextField).first()
    expect(field.prop('errorText')).to.equal(errors.get(0))
  })
  it('should remove with an immutable List', (done) => {
    const list = List.of('a', 'b', 'c')
    const onChange = ({ value, index }) => {
      expect(value).to.equal(undefined)
      expect(index).to.equal(list.size - 1)
      done()
    }
    const wrapper = shallow(<EditableList onChange={onChange} list={list} />)
    wrapper.find(RemoveButton).last().simulate('remove')
  })
  it('should grow with an immutable List', (done) => {
    const value = 'z'
    const list = List.of('a', 'b', 'c')
    const onChange = ({ value: v, index }) => {
      expect(v).to.equal(value)
      expect(index).to.equal(list.size)
      done()
    }
    const wrapper = shallow(<EditableList onChange={onChange} list={list} />)
    wrapper.find(TextField).last().simulate('change', { target: { value } })
  })
})
