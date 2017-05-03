import sub from './'

import { expect } from 'chai'

describe('submarine', () => {
  it('should pass through the value when no root selector has been set', () => {
    const submarine = sub()
    expect(submarine(42)).to.equal(42)
  })
  it('init can only be passed functions', () => {
    const submarine = sub()
    expect(() => submarine.init(42)).to.throw(Error)
  })
  it('should select part of the state that was passed in', () => {
    const submarine = sub()
    submarine.init((state) => state.test)
    expect(submarine({ test: 42 })).to.equal(42)
  })
})
