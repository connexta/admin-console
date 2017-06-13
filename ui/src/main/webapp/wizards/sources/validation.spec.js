import { expect } from 'chai'
import {
  isEmpty,
  isBlank,
  nextShouldBeDisabled,
  portError,
  userNameError,
  passwordError
} from './validation'

describe('Sources Validation', () => {
  it('isEmpty', () => {
    expect(isEmpty('')).to.equal(true)
    expect(isEmpty(undefined)).to.equal(true)
    expect(isEmpty('    ')).to.equal(false)
    expect(isEmpty('test')).to.equal(false)
  })
  it('isBlank', () => {
    expect(isBlank('')).to.equal(true)
    expect(isBlank(undefined)).to.equal(true)
    expect(isBlank('    ')).to.equal(true)
    expect(isBlank('test')).to.equal(false)
  })

  describe('User + Pass', () => {
    var configs = {
      sourceUserName: '',
      sourceUserPassword: '',
      sourceHostName: 'localhost',
      sourcePort: 8993
    }
    var discoveryType = 'hostnamePort'
    it('Can handle undefined values: enables Next', () => {
      configs.sourceUserName = undefined
      configs.sourceUserPassword = undefined
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be enabled').to.equal(false)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
    it('User + Empty pass: disables Next', () => {
      configs.sourceUserName = 'username'
      configs.sourceUserPassword = ''
      const discoveryType = 'hostnamePort'
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should report an error').to.be.a('string').that.is.not.empty
    })
    it('Empty User + Pass: disables Next', () => {
      configs.sourceUserName = ''
      configs.sourceUserPassword = 'password'
      const discoveryType = 'hostnamePort'
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
      expect(userNameError(configs), 'Username should report an error').to.be.a('string').that.is.not.empty
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
    it('User + Pass: enables Next', () => {
      configs.sourceUserName = 'username'
      configs.sourceUserPassword = 'password'
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be enabled').to.equal(false)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
    it('Empty User + Empty Pass: enables Next', () => {
      configs.sourceUserName = ''
      configs.sourceUserPassword = ''
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be enabled').to.equal(false)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
    it('User + Blank (not empty) Pass: enables Next', () => {
      configs.sourceUserName = 'username'
      configs.sourceUserPassword = '        '
      const discoveryType = 'hostnamePort'
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be enabled').to.equal(false)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
  })

  describe('Hostname + Port', () => {
    var configs = {
      sourceUserName: 'username',
      sourceUserPassword: 'password',
      sourceHostName: '',
      sourcePort: 0
    }
    var discoveryType = 'hostnamePort'
    it('Can handle undefined values: disables Next', () => {
      configs.sourceHostName = undefined
      configs.sourcePort = undefined
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
    })
    it('No Hostname + Valid Port: disables Next', () => {
      configs.sourceHostName = ''
      configs.sourcePort = 8993
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
    it('Blank (not empty) Hostname + Valid Port: disables Next', () => {
      configs.sourceHostName = '        '
      configs.sourcePort = 8993
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
    it('Invalid port range, lower bound: disables Next', () => {
      configs.sourceHostName = 'localhost'
      configs.sourcePort = -1
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
      expect(portError(configs), 'Port should return an error').to.be.a('string').that.is.not.empty
    })
    it('Valid port range, lower bound: enables Next', () => {
      configs.sourceHostName = 'localhost'
      configs.sourcePort = 0
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be enabled').to.equal(false)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
    it('Valid port range, upper bound: enables Next', () => {
      configs.sourceHostName = 'localhost'
      configs.sourcePort = 65535
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be enabled').to.equal(false)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
    it('Invalid port range, upper bound: disables Next', () => {
      configs.sourceHostName = 'localhost'
      configs.sourcePort = 65536
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
      expect(portError(configs), 'Port should return an error').to.be.a('string').that.is.not.empty
    })
    it('Valid Hostname + Valid Port: enables Next', () => {
      configs.sourceHostName = 'localhost'
      configs.sourcePort = 8993
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be enabled').to.equal(false)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
  })

  describe('Url', () => {
    var configs = {
      sourceUserName: 'username',
      sourceUserPassword: 'password',
      endpointUrl: ''
    }
    var discoveryType = 'url'
    it('Can handle undefined value: disables Next', () => {
      configs.endpointUrl = undefined
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
    })
    it('No url: disables Next', () => {
      configs.endpointUrl = ''
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
    })
    it('Blank (not empty) url: disables Next', () => {
      configs.endpointUrl = '        '
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be disabled').to.equal(true)
    })
    it('Valid url: enables Next', () => {
      configs.endpointUrl = 'https://localhost:8993/'
      expect(nextShouldBeDisabled({ configs, discoveryType }), 'Next button should be enabled').to.equal(false)
    })
  })
})
