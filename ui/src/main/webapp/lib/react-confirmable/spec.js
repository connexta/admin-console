import React from 'react'

import MTP from 'material-ui/styles/MuiThemeProvider'

import confirmable from './'

import { mount } from 'enzyme'
import { expect } from 'chai'

const Button = (props) => (
  <button {...props} />
)

describe('confirmable', () => {
  it('wrapped component should render initially', () => {
    const ConfirmableButton = confirmable(Button)
    const wrapper = mount(<MTP><ConfirmableButton className='button1' confirmableMessage='test' onClick={() => {}} /></MTP>)
    expect(wrapper.find('.button1')).to.have.length(1)
  })
  it('confirmation dialog should appear on click', () => {
    const ConfirmableButton = confirmable(Button)
    const wrapper = mount(<MTP><ConfirmableButton className='button1' confirmableMessage='test' onClick={() => {}} /></MTP>)
    wrapper.find('.button1').simulate('click')
    expect(wrapper.find('.yes')).to.have.length(1)
    expect(wrapper.find('.no')).to.have.length(1)
  })
  it('confirm should call original onClick', (done) => {
    const ConfirmableButton = confirmable(Button)
    const wrapper = mount(<MTP><ConfirmableButton className='button1' confirmableMessage='test' onClick={() => done()} /></MTP>)
    wrapper.find('.button1').simulate('click')
    wrapper.find('.yes button').simulate('click')
  })
  it('deny should display original component', () => {
    const ConfirmableButton = confirmable(Button)
    const wrapper = mount(<MTP><ConfirmableButton className='button1' confirmableMessage='test' onClick={() => {}} /></MTP>)
    wrapper.find('.button1').simulate('click')
    wrapper.find('.no button').simulate('click')
    expect(wrapper.find('.button1')).to.have.length(1)
  })
})
