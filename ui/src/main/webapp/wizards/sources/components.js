import React from 'react'
import { connect } from 'react-redux'

import { getSourceStage, getStagesClean, getConfig, getStageProgress, getConfigTypeById } from './reducer'
import { setNavStage, setConfigSource } from './actions'

import IconButton from 'material-ui/IconButton'
import {RadioButton, RadioButtonGroup} from 'material-ui/RadioButton'
import Flexbox from 'flexbox-react'
import AlertIcon from 'material-ui/svg-icons/alert/warning'
import { editConfigs } from 'admin-wizard/actions'

import LeftIcon from 'material-ui/svg-icons/hardware/keyboard-arrow-left'
import RightIcon from 'material-ui/svg-icons/hardware/keyboard-arrow-right'

import {
  animated,
  fadeIn,
  navButtonStyles,
  navButtonStylesDisabled
} from './styles.less'

export const CenteredElements = ({ children, stageIndex, style }) => (
  <div style={style} className={[animated, fadeIn].join(' ')}>
    {children}
  </div>
)

const prettyName = (id) => id.replace('-', ' ')

const SourceRadioButtonsView = ({ disabled, options = [], onEdits, configurationType, setSource, displayName }) => {
  return (
    <div style={{display: 'inline-block', margin: '10px'}}>
      {options.map((item, i) => (
        <SourceRadioButton key={i} label={prettyName(item.configurationType)} value={item.configurationType} disabled={disabled} valueSelected={configurationType} item={item} onSelect={() => setSource(options[i])} />
      ))}
    </div>
  )
}

const mapStateToProps = (state) => {
  const config = getConfig(state, 'configurationType')

  return {
    configurationType: config === undefined ? undefined : config.value,
    displayName: (id) => getConfigTypeById(state, id)
  }
}

const mapDispatchToProps = (dispatch, { id }) => ({
  setSource: (source) => dispatch(setConfigSource(source)),
  onEdits: (values) => dispatch(editConfigs(values))
})

export const SourceRadioButtons = connect(mapStateToProps, mapDispatchToProps)(SourceRadioButtonsView)

const alertMessage = 'SSL certificate is untrusted and possibly insecure'

const SourceRadioButton = ({ disabled, value, label, valueSelected = 'undefined', onSelect, item }) => {
  if (item.trustedCertAuthority) {
    return (
      <div>
        <RadioButtonGroup name={value} valueSelected={valueSelected} onChange={onSelect}>
          <RadioButton disabled={disabled}
            style={{whiteSpace: 'nowrap', padding: '3px', fontSize: '16px'}}
            value={value}
            label={label} />
        </RadioButtonGroup>
      </div>
    )
  } else {
    return (
      <div>
        <RadioButtonGroup style={{ display: 'inline-block', color: '#f90' }} name={value} valueSelected={valueSelected} onChange={onSelect}>
          <RadioButton disabled={disabled}
            style={{
              display: 'inline-block',
              whiteSpace: 'nowrap',
              padding: '3px',
              fontSize: '16px'
            }}
            value={value}
            labelStyle={{ color: '#f90' }}
            label={label} />
        </RadioButtonGroup>
        <IconButton
          touch
          iconStyle={{
            color: '#f90'
          }}
          style={{
            display: 'inline-block',
            color: '#f00',
            width: '24px',
            height: '24px',
            padding: '0px'
          }}
          tooltip={alertMessage}
          tooltipPosition='top-left'>
          <AlertIcon />
        </IconButton>
      </div>
    )
  }
}

export const BackNav = ({onClick, disabled}) => {
  if (!disabled) {
    return (
      <div className={navButtonStyles} onClick={onClick}>
        <LeftIcon style={{height: '100%', width: '100%'}} />
      </div>
    )
  } else {
    return (
      <div className={navButtonStylesDisabled}>
        <LeftIcon style={{color: 'lightgrey', height: '100%', width: '100%'}} />
      </div>
    )
  }
}

const ForwardNavView = ({onClick, clean, currentStage, maxStage, disabled}) => {
  if (clean && (currentStage !== maxStage) && !disabled) {
    return (
      <div className={navButtonStyles} onClick={onClick}>
        <RightIcon style={{height: '100%', width: '100%'}} />
      </div>
    )
  } else {
    return (
      <div className={navButtonStylesDisabled}>
        <RightIcon style={{color: 'lightgrey', height: '100%', width: '100%'}} />
      </div>
    )
  }
}
export const ForwardNav = connect((state) => ({
  clean: getStagesClean(state),
  maxStage: getStageProgress(state),
  currentStage: getSourceStage(state)}))(ForwardNavView)

const NavPanesView = ({ children, backClickTarget, forwardClickTarget, setNavStage, backDisabled = false, forwardDisabled = false }) => (
  <Flexbox justifyContent='center' flexDirection='row'>
    <BackNav disabled={backDisabled} onClick={() => setNavStage(backClickTarget)} />
    <CenteredElements style={{ width: '80%' }}>
      {children}
    </CenteredElements>
    <ForwardNav disabled={forwardDisabled} onClick={() => setNavStage(forwardClickTarget)} />
  </Flexbox>
)
export const NavPanes = connect(null, { setNavStage: setNavStage })(NavPanesView)

