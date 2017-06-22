import { expect } from 'chai'
import {
  genericMessage,
  getFriendlyMessage
} from './'

describe('GraphQL Errors', () => {
  describe('Friendly Messages', () => {
    it('Known error code returns custom message', () => {
      const code = 'CANNOT_CONNECT'
      expect(getFriendlyMessage(code), 'Did not return a string.').to.be.a('string').that.is.not.empty
      expect(getFriendlyMessage(code), 'Returned a generic message.').to.not.equal(genericMessage(code))
    })
    it('Unknown error code returns generic message', () => {
      const code = 'NOT_A_KNOWN_CODE'
      expect(getFriendlyMessage(code), 'Did not return a generic message.').to.equal(genericMessage(code))
    })
  })
})
