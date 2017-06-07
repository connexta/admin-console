import React from 'react'
import { connect } from 'react-redux'

import { clearWizard } from 'admin-wizard/actions'

import Flexbox from 'flexbox-react'

import { Link } from 'react-router'

import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import LargeStatusIndicator from 'components/LargeStatusIndicator'

const CompletedStageView = ({ messages, clearWizard }) => (
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
        <Action
          primary
          label='Go Home'
          onClick={clearWizard}
        />
      </Link>
      <Action
        primary
        label='Add Another Source'
        onClick={clearWizard}
      />
    </ActionGroup>
  </Flexbox>
)
export default connect(null, { clearWizard })(CompletedStageView)
