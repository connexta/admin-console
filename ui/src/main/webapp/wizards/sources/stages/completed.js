import React from 'react'

import RaisedButton from 'material-ui/RaisedButton'
import ReplayIcon from 'material-ui/svg-icons/av/replay'

import Title from 'components/Title'
import Description from 'components/Description'
import LargeStatusIndicator from 'components/LargeStatusIndicator'
import Body from 'components/wizard/Body'
import Navigation, { Home } from 'components/wizard/Navigation'

export default ({ restart }) => (
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
        <RaisedButton primary label='Add More' labelPosition='after' icon={<ReplayIcon />} onClick={restart} />
        <Home />
      </Navigation>
    </Body>
  </div>
)
