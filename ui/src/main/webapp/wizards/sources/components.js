import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

import { RadioButton, RadioButtonGroup } from 'material-ui/RadioButton'

import {
  animated,
  fadeIn,
  sideLines
} from './styles.css'

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

const horizontalSideLinesStyle = ({
  width: '100%',
  display: 'block',
  height: '1px',
  position: 'absolute',
  top: '50%',
  zIndex: 1
})

const SideLinesView = ({ muiTheme, label }) => (
  <div className={sideLines}>
    <span style={{ color: muiTheme.palette.textColor, backgroundColor: muiTheme.palette.canvasColor }}>
      {label}
    </span>
    <div style={{ ...horizontalSideLinesStyle, backgroundColor: muiTheme.palette.textColor }} />
  </div>
)

export const SideLines = muiThemeable()(SideLinesView)

