import React from 'react'
import { connect } from 'react-redux'
import { getStage, getIsSubmitting } from './reducer'

import Paper from 'material-ui/Paper'

import Mount from 'react-mount'
import { clearWizard } from 'admin-wizard/actions'

import Spinner from 'components/Spinner'

import WelcomeStage from './stages/welcome'
import DiscoveryStage from './stages/discovery'
import SourceSelectionStage from './stages/source-selection'
import ConfirmationStage from './stages/confirmation'
import CompletedStage from './stages/completed'


const WizardView = ({ id, children, clearWizard }) => (
  <Mount off={clearWizard} key={id}>{children}</Mount>
)

const Wizard = connect(null, { clearWizard })(WizardView)

const stageMapping = {
  welcomeStage: WelcomeStage,
  discoveryStage: DiscoveryStage,
  sourceSelectionStage: SourceSelectionStage,
  confirmationStage: ConfirmationStage,
  completedStage: CompletedStage
}

const styles = ({
  main: {
    margin: '20px 0',
    padding: '40px',
    position: 'relative'
  },
  submitting: {
    position: 'absolute',
    top: 0,
    bottom: 0,
    right: 0,
    left: 0,
    background: 'rgba(0, 0, 0, 0.1)',
    zIndex: 9001,
    display: 'flex'
  }
})

export default (sources) => {
  const StageRouter = connect(
    (state) => ({ stage: getStage(state) })
  )(({ stage }) => React.createElement(stageMapping[stage], { sources }))

  const SourceApp = ({ isSubmitting = false, value = {}, setDefaults, messages }) => (
    <Wizard id='sources'>
      <Paper style={styles.main}>
        <Spinner submitting={isSubmitting}>
          <StageRouter/>
        </Spinner>
      </Paper>
    </Wizard>
  )
  return connect((state) => ({
    isSubmitting: getIsSubmitting(state)
  }))(SourceApp)
}
