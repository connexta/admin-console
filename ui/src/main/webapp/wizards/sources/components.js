import React from 'react'
import { connect } from 'react-redux'
import muiThemeable from 'material-ui/styles/muiThemeable'

import { getSourceStage, getStagesClean, getStageProgress } from './reducer'
import { setNavStage } from './actions'

import {RadioButton, RadioButtonGroup} from 'material-ui/RadioButton'
import Flexbox from 'flexbox-react'

import LeftIcon from 'material-ui/svg-icons/hardware/keyboard-arrow-left'
import RightIcon from 'material-ui/svg-icons/hardware/keyboard-arrow-right'

import {
  animated,
  fadeIn,
  navButtonStyles,
  navButtonStylesDisabled,
  sideLines,
  horizontalSideLines
} from './styles.less'

export const CenteredElements = ({ children, stageIndex, style }) => (
  <div style={style} className={[animated, fadeIn].join(' ')}>
    {children}
  </div>
)

export const SourceRadioButtons = ({ disabled, options = {}, valueSelected, onChange }) => {
  return (
    <div style={{display: 'inline-block', margin: '10px'}}>
      <RadioButtonGroup
        name='discoveredEndpoints'
        valueSelected={valueSelected}
        onChange={(event, key) => onChange(key)}
      >
        {Object.keys(options).map((key, i) => (
          <RadioButton disabled={disabled}
            style={{whiteSpace: 'nowrap', padding: '3px', fontSize: '16px'}}
            value={key}
            label={key}
            key={i}
          />))}
      </RadioButtonGroup>
    </div>
  )
}

export const BackNav = ({onClick, disabled, muiTheme}) => {
  if (!disabled) {
    return (
      <div className={navButtonStyles} style={{ backgroundColor: muiTheme.palette.canvasColor }} onClick={onClick}>
        <LeftIcon style={{ color: muiTheme.palette.textColor, height: '100%', width: '100%' }} />
      </div>
    )
  } else {
    return (
      <div className={navButtonStylesDisabled} style={{ backgroundColor: muiTheme.palette.canvasColor }}>
        <LeftIcon style={{ color: muiTheme.palette.textColor, height: '100%', width: '100%' }} />
      </div>
    )
  }
}

const ForwardNavView = ({onClick, clean, currentStage, maxStage, disabled, muiTheme}) => {
  if (clean && (currentStage !== maxStage) && !disabled) {
    return (
      <div className={navButtonStyles} style={{ backgroundColor: muiTheme.palette.canvasColor }} onClick={onClick}>
        <RightIcon style={{ color: muiTheme.palette.textColor, height: '100%', width: '100%' }} />
      </div>
    )
  } else {
    return (
      <div className={navButtonStylesDisabled} style={{ backgroundColor: muiTheme.palette.canvasColor }}>
        <RightIcon style={{ color: muiTheme.palette.textColor, height: '100%', width: '100%' }} />
      </div>
    )
  }
}
export const ForwardNav = connect((state) => ({
  clean: getStagesClean(state),
  maxStage: getStageProgress(state),
  currentStage: getSourceStage(state)}))(ForwardNavView)

const NavPanesView = ({ children, back, forward, setNavStage, backDisabled = false, forwardDisabled = false, muiTheme }) => (
  <Flexbox justifyContent='center' flexDirection='row'>
    <BackNav
      muiTheme={muiTheme}
      disabled={backDisabled}
      onClick={() => setNavStage(back)}
    />
    <CenteredElements style={{ width: '80%' }}>
      {children}
    </CenteredElements>
    <ForwardNav
      muiTheme={muiTheme}
      disabled={forwardDisabled}
      onClick={() => setNavStage(forward)}
      />
  </Flexbox>
)
export const NavPanes = connect(null, { setNavStage: setNavStage })(muiThemeable()(NavPanesView))

const SideLinesView = ({ muiTheme, label }) => (
  <div className={sideLines}>
    <span style={{ color: muiTheme.palette.textColor, backgroundColor: muiTheme.palette.canvasColor }}>
      {label}
    </span>
    <div className={horizontalSideLines} style={{ backgroundColor: muiTheme.palette.textColor }} />
  </div>
)

export const SideLines = muiThemeable()(SideLinesView)

