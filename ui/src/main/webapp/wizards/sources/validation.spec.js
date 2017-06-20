import { expect } from 'chai'
import {
  isEmpty,
  isBlank,
  discoveryStageDisableNext,
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
    const baseConfig = (changes) => ({
      sourceUserName: '',
      sourceUserPassword: '',
      sourceHostName: 'localhost',
      sourcePort: 8993,
      ...changes
    })
    const discoveryType = 'hostnamePort'

    it('Can handle undefined values: enables Next', () => {
      const configs = baseConfig({
        sourceUserName: undefined,
        sourceUserPassword: undefined
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be enabled').to.equal(false)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
    it('User + Empty pass: disables Next', () => {
      const configs = baseConfig({
        sourceUserName: 'username',
        sourceUserPassword: ''
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should report an error').to.be.a('string').that.is.not.empty
    })
    it('Empty User + Pass: disables Next', () => {
      const configs = baseConfig({
        sourceUserName: '',
        sourceUserPassword: 'password'
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
      expect(userNameError(configs), 'Username should report an error').to.be.a('string').that.is.not.empty
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
    it('User + Pass: enables Next', () => {
      const configs = baseConfig({
        sourceUserName: 'username',
        sourceUserPassword: 'password'
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be enabled').to.equal(false)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
    it('Empty User + Empty Pass: enables Next', () => {
      const configs = baseConfig({
        sourceUserName: '',
        sourceUserPassword: ''
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be enabled').to.equal(false)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
    it('User + Blank (not empty) Pass: enables Next', () => {
      const configs = baseConfig({
        sourceUserName: 'username',
        sourceUserPassword: '        '
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be enabled').to.equal(false)
      expect(userNameError(configs), 'Username should not report an error').to.be.undefined
      expect(passwordError(configs), 'Password should not report an error').to.be.undefined
    })
  })

  describe('Hostname + Port', () => {
    const baseConfig = (changes) => ({
      sourceUserName: 'username',
      sourceUserPassword: 'password',
      sourceHostName: '',
      sourcePort: 0,
      ...changes
    })
    const discoveryType = 'hostnamePort'

    it('Can handle undefined values: disables Next', () => {
      const configs = baseConfig({
        sourceHostName: undefined,
        sourcePort: undefined
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
    })
    it('No Hostname + Valid Port: disables Next', () => {
      const configs = baseConfig({
        sourceHostName: '',
        sourcePort: 8993
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
    it('Blank (not empty) Hostname + Valid Port: disables Next', () => {
      const configs = baseConfig({
        sourceHostName: '        ',
        sourcePort: 8993
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
    it('Invalid port range, lower bound: disables Next', () => {
      const configs = baseConfig({
        sourceHostName: 'localhost',
        sourcePort: -1
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
      expect(portError(configs), 'Port should return an error').to.be.a('string').that.is.not.empty
    })
    it('Valid port range, lower bound: enables Next', () => {
      const configs = baseConfig({
        sourceHostName: 'localhost',
        sourcePort: 0
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be enabled').to.equal(false)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
    it('Valid port range, upper bound: enables Next', () => {
      const configs = baseConfig({
        sourceHostName: 'localhost',
        sourcePort: 65535
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be enabled').to.equal(false)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
    it('Invalid port range, upper bound: disables Next', () => {
      const configs = baseConfig({
        sourceHostName: 'localhost',
        sourcePort: 65536
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
      expect(portError(configs), 'Port should return an error').to.be.a('string').that.is.not.empty
    })
    it('Valid Hostname + Valid Port: enables Next', () => {
      const configs = baseConfig({
        sourceHostName: 'localhost',
        sourcePort: 8993
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be enabled').to.equal(false)
      expect(portError(configs), 'Port should not return an error').to.be.undefined
    })
  })

  describe('Url', () => {
    const baseConfig = (changes) => ({
      sourceUserName: 'username',
      sourceUserPassword: 'password',
      endpointUrl: '',
      ...changes
    })
    const discoveryType = 'url'

    it('Can handle undefined value: disables Next', () => {
      const configs = baseConfig({
        endpointUrl: undefined
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
    })
    it('No url: disables Next', () => {
      const configs = baseConfig({
        endpointUrl: ''
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
    })
    it('Blank (not empty) url: disables Next', () => {
      const configs = baseConfig({
        endpointUrl: '        '
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be disabled').to.equal(true)
    })
    it('Valid url: enables Next', () => {
      const configs = baseConfig({
        endpointUrl: 'https://localhost:8993/'
      })
      expect(discoveryStageDisableNext({
        configs,
        discoveryType
      }), 'Next button should be enabled').to.equal(false)
    })
  })
})
