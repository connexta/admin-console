import React from 'react'
import { connect } from 'react-redux'
import { getStage, getIsSubmitting } from './reducer'

import Flexbox from 'flexbox-react'
import CircularProgress from 'material-ui/CircularProgress'
import Paper from 'material-ui/Paper'

import styles from './styles.less'

import Mount from 'react-mount'
import { clearWizard } from 'admin-wizard/actions'

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

export default (sources) => {
  const StageRouter = connect(
    (state) => ({ stage: getStage(state) })
  )(({ stage }) => React.createElement(stageMapping[stage], { sources }))

  const SourceApp = ({ isSubmitting = false, value = {}, setDefaults, messages }) => (
    <Wizard id='sources'>
      <Paper className={styles.main}>
        {isSubmitting
          ? <div className={styles.submitting}>
            <Flexbox justifyContent='center' alignItems='center' width='100%'>
              <CircularProgress size={60} thickness={7} />
            </Flexbox>
          </div>
          : null}
        <StageRouter />
      </Paper>
    </Wizard>
  )
  return connect((state) => ({
    isSubmitting: getIsSubmitting(state)
  }))(SourceApp)
}
