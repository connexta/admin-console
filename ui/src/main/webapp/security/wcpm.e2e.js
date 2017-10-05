import React from 'react'

import Wcpm from './wcpm'

import { getWcpm } from '../reducer'

const WebContextPolicyManager = () => <Wcpm rootSelector={getWcpm} />

import drive, {
  click,
  wait,
  change
} from '../enzyme-driver'

describe('<WebContextPolicyManager />', function () {
  this.timeout(10000)

  describe('whitelist', () => {
    let state

    it('should start the Web Context Policy Manager', async () => {
      const actions = [
        wait('WhiteListView')
      ]
      state = (await drive(WebContextPolicyManager, actions)).state
    })

    it('should fail validation', () => {
      const actions = [
        click(['FloatingActionButton', 0]),
        change(['TextField', -1], { target: { value: 'hello' } }),
        click({ label: 'Save' })
      ]
      return drive(WebContextPolicyManager, actions, { state })
    })
  })
})
