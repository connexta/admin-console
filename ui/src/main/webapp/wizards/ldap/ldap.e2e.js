import React from 'react'

import Ldap from '.'

import { getWizard } from '../../reducer'

const LdapWizard = () => <Ldap rootSelector={getWizard} />

import drive, {
  assert,
  wait,
  click,
  edit,
  next
} from '../../enzyme-driver'

const useCaseStage = (useCase) => [
  wait('#use-case-stage'),
  assert('Next', 'disabled', true),
  edit('RadioSelectionView', useCase),
  assert('Next', 'disabled', false)
]

const typeSelectionStage = (type) => [
  wait('#ldap-type-selection'),
  assert('Next', 'disabled', true),
  edit('RadioSelectionView', type),
  assert('Next', 'disabled', false)
]

const networkSettingsStage = (hostname, port, encryption) => [
  wait('#network-settings'),
  edit('Hostname', hostname),
  edit('Port', port),
  edit('SelectView', encryption)
]

const bindSettingsStage = (bindUser) => [
  wait('#bind-settings'),
  edit(['InputView', 0], bindUser)
]

const directorySettingsStage = (baseUserDn, loginUserAttribute, memberAttributeReferencedInGroup, baseGroupDn) => [
  wait('#directory-settings'),
  baseUserDn !== undefined
    ? edit(['InputAutoView', 0], baseUserDn) : undefined,
  loginUserAttribute !== undefined
    ? edit(['InputAutoView', 1], loginUserAttribute) : undefined,
  memberAttributeReferencedInGroup !== undefined
    ? edit(['InputAutoView', 2], memberAttributeReferencedInGroup) : undefined,
  baseGroupDn !== undefined
    ? edit(['InputAutoView', 3], baseGroupDn) : undefined
].filter((value) => value !== undefined)

describe('<LdapWizard />', function () {
  this.timeout(30000)

  describe('network settings stage unhappy paths', () => {
    let state

    it('should start the wizard', async () => {
      const actions = [
        click('Begin'),
        ...useCaseStage('Authentication'), next(),
        ...typeSelectionStage('openDj'), next()
      ]
      state = (await drive(LdapWizard, actions)).state
    })

    it('should fail to bind because of unavailable hostname', () => {
      const actions = [
        ...networkSettingsStage('localhostt', 389, 'none'), next(),
        wait('Message'),
        assert('Message', 'message', 'Could not connect to the specified server.')
      ]
      return drive(LdapWizard, actions, { state })
    })

    it('should fail to bind because of unavailable port', () => {
      const actions = [
        ...networkSettingsStage('localhost', 1234, 'none'), next(),
        wait('Message'),
        assert('Message', 'message', 'Could not connect to the specified server.')
      ]
      return drive(LdapWizard, actions, { state })
    })

    it('should fail to bind because of unavailable encryption', () => {
      const actions = [
        ...networkSettingsStage('localhost', 1636, 'startTls'), next(),
        wait('Message'),
        assert('Message', 'message', 'Could not connect to the specified server.')
      ]
      return drive(LdapWizard, actions, { state })
    })
  })

  describe('bind settings stage unhappy path', () => {
    let state

    it('should start the wizard', async () => {
      const actions = [
        click('Begin'),
        ...useCaseStage('Authentication'), next(),
        ...typeSelectionStage('openDj'), next(),
        ...networkSettingsStage('localhost', 1636, 'ldaps'), next(),
        ...bindSettingsStage('')
      ]
      state = (await drive(LdapWizard, actions)).state
    })

    it('should fail to bind with incorrect username', () => {
      const actions = [
        ...bindSettingsStage('cn=adminn'), next(),
        wait('Message'),
        assert('Message', 'message', 'Cannot authenticate user.')
      ]
      return drive(LdapWizard, actions, { state })
    })

    it('should fail to bind without username', () => {
      const actions = [
        ...bindSettingsStage(''), next(),
        wait({ errorText: 'Empty required field.' })
      ]
      return drive(LdapWizard, actions, { state })
    })
  })

  describe('happy paths with openDj', () => {
    it('should reach the confirmation stage', () => {
      const actions = [
        click('Begin'),
        ...useCaseStage('Authentication'), next(),
        ...typeSelectionStage('openDj'), next(),
        ...networkSettingsStage('localhost', 1636, 'ldaps'), next(),
        ...bindSettingsStage('cn=admin'), next(),
        ...directorySettingsStage(
          'ou=users,dc=example,dc=com',
          'uid',
          'uid',
          'ou=groups,dc=example,dc=com'
        ), next(),
        wait('#confirm')
      ]
      return drive(LdapWizard, actions)
    })
  })
})
