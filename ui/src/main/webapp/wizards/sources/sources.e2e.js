import React from 'react'

import { expect } from 'chai'

import Sources from '.'

import { getWizard } from '../../reducer'

import ddfSources from 'ddf-sources'

const DdfSources = Sources(ddfSources)

const SourcesWizard = () => <DdfSources rootSelector={getWizard} />

import drive, {
  assert,
  wait,
  click,
  edit,
  change,
  next
} from '../../enzyme-driver'

const sourceSelection = (type, name) => [
  assert('Next', 'disabled', true),
  change('SourceRadioButtons', type),
  assert('Next', 'disabled', false),
  next(),
  wait('#confirmationStage'),
  assert('Finish', 'disabled', true),
  edit(['InputView', { label: 'Source Name' }], name),
  assert('Finish', 'disabled', false)
]

describe('<SourcesWizard />', function () {
  this.timeout(10000)

  describe('source discovery stage', () => {
    let state

    it('should start the wizard', async () => {
      const actions = [
        click('Begin')
      ]
      state = (await drive(SourcesWizard, actions)).state
    })

    it('should have next disabled when no hostname', () => {
      const actions = [
        assert('Next', 'disabled', true),
        edit('Hostname', 'hello'),
        assert('Next', 'disabled', false)
      ]
      return drive(SourcesWizard, actions, { state })
    })

    it('should need to provide username and password', () => {
      const actions = [
        edit('Hostname', 'hello'),
        assert('Next', 'disabled', false),
        edit(['InputView', { label: 'Username' }], 'admin'),
        assert(['InputView', { label: 'Password' }], 'errorText', 'Username with no password.'),
        assert('Next', 'disabled', true)
      ]
      return drive(SourcesWizard, actions, { state })
    })

    it('should need to provide password and username', () => {
      const actions = [
        edit('Hostname', 'hello'),
        assert('Next', 'disabled', false),
        edit(['InputView', { label: 'Password' }], 'admin'),
        assert(['InputView', { label: 'Username' }], 'errorText', 'Password with no username.'),
        assert('Next', 'disabled', true)
      ]
      return drive(SourcesWizard, actions, { state })
    })

    it('should find sources from localhost', async () => {
      const actions = [
        edit('Hostname', 'localhost'),
        assert('Next', 'disabled', false),
        next(),
        wait('#sourceSelectionStage'),
        (wrapper) => {
          const labels = wrapper.find('RadioButton').map((el) => el.props().label)
          expect(labels).to.deep.equal(['CSW', 'OpenSearch'])
        }
      ]
      state = (await drive(SourcesWizard, actions, { state })).state
    })

    it('should create a CSW source', () => {
      const actions = [
        ...sourceSelection('CSW', 'csw')
      ]
      return drive(SourcesWizard, actions, { state })
    })

    it('should create an OpenSearch source', () => {
      const actions = [
        ...sourceSelection('OpenSearch', 'open')
      ]
      return drive(SourcesWizard, actions, { state })
    })
  })
})
