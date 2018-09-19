import React from 'react'

import { expect } from 'chai'
import { shallow } from 'enzyme'

import { InputAuto } from 'admin-wizard/inputs'

import { DirectorySettings } from './directory-settings'

describe('<LDAP />', () => {
  describe('<DirectorySettings />', () => {
    it('should hide fields when `Authentication`', () => {
      const wrapper = shallow(
        <DirectorySettings
          configs={{
            ldapUseCase: 'Authentication',
            connectionInfo: [['localhost', '636', 'ldaps']]
          }}
          options={{}}
          errors={[]}
          onEdit={() => {}}
        />
      )

      const inputs = wrapper.find(InputAuto)
      expect(inputs).to.have.length(6)

      const visible = inputs
        .filterWhere((comp) => comp.prop('visible'))
        .map((comp) => comp.prop('id'))

      expect(visible).to.deep.equal([
        'baseUserDn',
        'loginUserAttribute',
        'memberAttributeReferencedInGroup',
        'baseGroupDn'
      ])

      const notVisible = wrapper.find(InputAuto)
        .filterWhere((comp) => !comp.prop('visible'))
        .map((comp) => comp.prop('id'))

      expect(notVisible).to.deep.equal([
        'groupObjectClass',
        'groupAttributeHoldingMember'
      ])
    })
    it('should show all fields when `AttributeStore`', () => {
      const wrapper = shallow(
        <DirectorySettings
          configs={{
            ldapUseCase: 'AttributeStore',
            connectionInfo: [['localhost', '636', 'ldaps']]
          }}
          options={{}}
          errors={[]}
          onEdit={() => {}}
        />
      )

      const visible = wrapper.find(InputAuto).filterWhere((comp) => comp.prop('visible'))
      expect(visible).to.have.length(6, 'All fields should be visible.')
    })
    it('should show all fields when `AuthenticationAndAttributeStore`', () => {
      const wrapper = shallow(
        <DirectorySettings
          configs={{
            ldapUseCase: 'AuthenticationAndAttributeStore',
            connectionInfo: [['localhost', '636', 'ldaps']]
          }}
          options={{}}
          errors={[]}
          onEdit={() => {}}
        />
      )

      const visible = wrapper.find(InputAuto).filterWhere((comp) => comp.prop('visible'))
      expect(visible).to.have.length(6, 'All fields should be visible.')
    })
  })
})
