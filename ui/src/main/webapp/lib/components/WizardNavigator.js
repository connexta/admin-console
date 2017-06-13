import React from 'react'

import Flexbox from 'flexbox-react'
import FlatButton from 'material-ui/FlatButton'
import LinearProgress from 'material-ui/LinearProgress'

import LeftIcon from 'material-ui/svg-icons/hardware/keyboard-arrow-left'
import RightIcon from 'material-ui/svg-icons/hardware/keyboard-arrow-right'

const BackNav = ({ label = 'Back', onClick = () => {}, disabled = false, ...rest }) => (
  <FlatButton primary disabled={disabled} label={label} onClick={onClick} labelPosition='after' {...rest} icon={<LeftIcon />} />
)

const NextNav = ({ label = 'Next', onClick = () => {}, disabled = false, ...rest }) => (
  <FlatButton primary disabled={disabled} label={label} onClick={onClick} labelPosition='before' {...rest} icon={<RightIcon />} />
)

const Navigator = (props) => {
  const {
    right = <NextNav />,
    left = <BackNav />,
    style = {},
    min = 0,
    max = 10,
    value = 0,
    ...rest
  } = props

  return (
    <Flexbox
      style={{ marginTop: 20, ...style }}
      justifyContent='space-between'
      alignItems='center'
      {...rest}>
      {left}
      <Flexbox flex='1' style={{ margin: '0 8px' }}>
        <LinearProgress mode='determinate' min={min} max={max} value={value} />
      </Flexbox>
      {right}
    </Flexbox>
  )
}

export { Navigator, BackNav, NextNav }
