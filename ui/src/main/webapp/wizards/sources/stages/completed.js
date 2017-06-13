import React from 'react'
import { connect } from 'react-redux'

import { clearWizard } from 'admin-wizard/actions'

import { Link } from 'react-router'

import FlatButton from 'material-ui/FlatButton'

import ReplayIcon from 'material-ui/svg-icons/av/replay'
import HomeIcon from 'material-ui/svg-icons/action/home'

import Title from 'components/Title'
import Description from 'components/Description'
import LargeStatusIndicator from 'components/LargeStatusIndicator'
import { Navigator } from 'components/WizardNavigator'

const CompletedStageView = ({ messages, clearWizard }) => (
  <div>
    <Title>
      All Done!
    </Title>
    <Description>
      Your source has been added successfully.
    </Description>
    <LargeStatusIndicator success />
    <div style={{ maxWidth: 600, margin: '0 auto' }}>
      <Navigator
        max={3}
        value={3}
        left={
          <FlatButton primary label='Add More' labelPosition='after' icon={<ReplayIcon />} onClick={clearWizard} />
        }
        right={
          <Link to='/' >
            <FlatButton primary label='Home' labelPosition='before' icon={<HomeIcon />} onClick={clearWizard} />
          </Link>
        }
      />
    </div>
  </div>
)
export default connect(null, { clearWizard })(CompletedStageView)
