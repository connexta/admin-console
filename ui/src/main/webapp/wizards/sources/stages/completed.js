import React from 'react'
import { connect } from 'react-redux'

import { resetSourceWizardState } from '../actions'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import LargeStatusIndicator from 'components/LargeStatusIndicator'

import Flexbox from 'flexbox-react'

import { Link } from 'react-router'

const CompletedStageView = ({ messages, resetSourceWizardState }) => (
  <Flexbox justifyContent='center' flexDirection='column'>
    <Title>
      All Done!
    </Title>
    <Description>
      Your source has been added successfully.
    </Description>
    <LargeStatusIndicator success />
    <ActionGroup>
      <Link to='/'>
        <Action primary label='Go Home' onClick={resetSourceWizardState} />
      </Link>
      <Action primary label='Add Another Source' onClick={resetSourceWizardState} />
    </ActionGroup>
  </Flexbox>
)
export default connect(null, { resetSourceWizardState })(CompletedStageView)
