import React from 'react'
import { connect } from 'react-redux'

import { fetchSystemUsageProperties, acceptSystemUsage } from './actions'
import { getSystemUsageProperties, getSystemUsageAccepted } from './reducer'

import Dialog from 'material-ui/Dialog'
import FlatButton from 'material-ui/FlatButton'

const isInIframe = () => {
  return window !== window.top
}

let Modal = ({ settings, fetchSystemUsageProperties, getSystemUsageAccepted, acceptSystemUsage }) => {
  if (settings && settings.systemUsageEnabled && !isInIframe()) {
    const acceptButton = (
      <FlatButton
        label='OK'
        primary
        onTouchTap={acceptSystemUsage}
      />
    )
    return (
      <Dialog
        title={settings.systemUsageTitle}
        actions={acceptButton}
        modal
        open={!getSystemUsageAccepted}>
        {settings.systemUsageMessage}
      </Dialog>
    )
  } else {
    return (<div />)
  }
}

export default connect((state) => ({
  settings: getSystemUsageProperties(state),
  getSystemUsageAccepted: getSystemUsageAccepted(state)
}), {
  fetchSystemUsageProperties: fetchSystemUsageProperties,
  acceptSystemUsage: acceptSystemUsage
})(Modal)
