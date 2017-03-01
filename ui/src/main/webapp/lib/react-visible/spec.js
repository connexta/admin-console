import React from 'react'

import visible from './'

import { mount } from 'enzyme'
import { expect } from 'chai'

describe('visible', () => {
  it('should be visible by default', () => {
    const Visible = visible(() => <span />)
    const wrapper = mount(<Visible />)
    expect(wrapper.find('span')).to.have.length(1)
  })

  it('should be visible when attribute is set to true', () => {
    const Visible = visible(() => <span />)
    const wrapper = mount(<Visible visible />)
    expect(wrapper.find('span')).to.have.length(1)
  })

  it('should NOT be visible when attribute is set to false', () => {
    const Visible = visible(() => <span />)
    const wrapper = mount(<Visible visible={false} />)
    expect(wrapper.find('span')).to.have.length(0)
  })

  it('should pass down all props', () => {
    const Visible = visible(({ className }) => <span className={className} />)
    const wrapper = mount(<Visible visible className='header' />)
    expect(wrapper.find({ className: 'header' })).to.have.length(1)
  })

  it('should not pass down visible', () => {
    const Visible = visible(({ visible }) => <span className={(visible === undefined) ? 'visible' : 'invisible'} />)
    const wrapper = mount(<Visible visible />)
    expect(wrapper.find({ className: 'visible' })).to.have.length(1)
  })
})
