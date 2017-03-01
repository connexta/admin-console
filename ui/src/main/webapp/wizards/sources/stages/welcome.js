import React from 'react'
import { connect } from 'react-redux'

import { changeStage } from '../actions'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'

import { CenteredElements } from '../components'

const WelcomeStageView = ({ changeStage }) => (
  <CenteredElements>
    <Title>
      Welcome to the Source Configuration Wizard
    </Title>
    <Description>
      This wizard will guide you through discovering and configuring
      various sources that are used to query metadata from catalogs.
      To begin, make sure you have the hostname and port of the source you plan to configure.
    </Description>
    <ActionGroup>
      <Action
        primary
        label='Begin Source Setup'
        onClick={() => changeStage('discoveryStage')} />
    </ActionGroup>
  </CenteredElements>
)
export default connect(null, {
  changeStage: changeStage
})(WelcomeStageView)
