import React from 'react'
import { connect } from 'react-redux'

import { clearWizard } from 'admin-wizard/actions'

import RaisedButton from 'material-ui/RaisedButton'

import ReplayIcon from 'material-ui/svg-icons/av/replay'

import Title from 'components/Title'
import Description from 'components/Description'
import LargeStatusIndicator from 'components/LargeStatusIndicator'
import Body from 'components/wizard/Body'
import Navigation, { Home } from 'components/wizard/Navigation'

const CompletedStageView = ({ messages, clearWizard }) => (
  <div>
    <Title>
      All Done!
    </Title>
    <Description>
      Your source has been added successfully.
    </Description>
    <LargeStatusIndicator success />
    <Body>
      <Navigation>
        <RaisedButton primary label='Add More' labelPosition='after' icon={<ReplayIcon />} onClick={clearWizard} />
        <Home />
      </Navigation>
    </Body>
  </div>
)
export default connect(null, { clearWizard })(CompletedStageView)
