import { expect } from 'chai'

import { createPolicy, validator as policyValidator } from './policy'
import { validator as whitelistValidator } from './whitelist'

describe('validators', () => {
  describe('validator(policy)', () => {
    it('should not allow empty fields', () => {
      const policy = createPolicy({ realm: '', paths: [], authTypes: [] })
      const errors = policyValidator(policy)
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.getIn(['paths', 0])).to.not.equal(undefined)
      expect(errors.get('authTypes')).to.not.equal(undefined)
      expect(errors.get('realm')).to.not.equal(undefined)
    })
    it('should not allow invalid context paths', () => {
      const policy = createPolicy({ paths: ['asdf'], authTypes: ['GUEST'] })
      const errors = policyValidator(policy)
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.getIn(['paths', 0])).to.not.equal(undefined)
    })
    it('should not allow duplicate context paths', () => {
      const policy = createPolicy({ paths: ['/hello', '/hello'], authTypes: ['GUEST'] })
      const errors = policyValidator(policy)
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.getIn(['paths', 1])).to.not.equal(undefined)
    })
    it('should not allow duplicate context paths', () => {
      const policy = createPolicy({ paths: ['/hello/world'], authTypes: ['GUEST'] })
      const policies = [policy]
      const errors = policyValidator(policy, { policies })
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.getIn(['paths', 0])).to.not.equal(undefined)
    })
    it('should not allow whitelisted context path', () => {
      const policy = createPolicy({ paths: ['/hello/world'], authTypes: ['GUEST'] })
      const whitelisted = ['/hello/world']
      const errors = policyValidator(policy, { whitelisted })
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.getIn(['paths', 0])).to.not.equal(undefined)
    })
    it('should be a valid policy', () => {
      const policy = createPolicy({ realm: 'karaf', paths: ['/'], authTypes: ['GUEST'] })
      const errors = policyValidator(policy)
      expect(errors.isEmpty()).to.equal(true)
    })
    it('should not allow trailing slashes in context path', () => {
      const policy = createPolicy({ paths: ['/test/'], authTypes: ['GUEST'] })
      const errors = policyValidator(policy)
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.getIn(['paths', 0])).to.not.equal(undefined)
    })
  })
  describe('validator(whitelist)', () => {
    it('should not allow invalid context paths', () => {
      const whitelist = ['/hello', 'asdf', '/world']
      const errors = whitelistValidator(whitelist)
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.get(0)).to.equal(undefined)
      expect(errors.get(1)).to.not.equal(undefined)
      expect(errors.get(2)).to.equal(undefined)
    })
    it('should not allow duplicate paths in the whitelist', () => {
      const whitelist = ['/hello', '/world', '/hello']
      const errors = whitelistValidator(whitelist)
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.get(0)).to.equal(undefined)
      expect(errors.get(1)).to.equal(undefined)
      expect(errors.get(2)).to.not.equal(undefined)
    })
    it('should not allow context path included in a policy', () => {
      const whitelist = ['/hello']
      const policies = [{ paths: whitelist }]
      const errors = whitelistValidator(whitelist, { policies })
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.get(0)).to.not.equal(undefined)
    })
    it('should be a valid whitelist', () => {
      const whitelist = ['/hello']
      const policies = [{ paths: ['/world'] }]
      const errors = whitelistValidator(whitelist, { policies })
      expect(errors.isEmpty()).to.equal(true)
    })
    it('should not allow trailing slashes in context path', () => {
      const whitelist = ['/hello', '/world', '/test/']
      const errors = whitelistValidator(whitelist)
      expect(errors.isEmpty()).to.equal(false)
      expect(errors.get(0)).to.equal(undefined)
      expect(errors.get(1)).to.equal(undefined)
      expect(errors.get(2)).to.not.equal(undefined)
    })
  })
})
