import React from 'react'

import TextField from 'material-ui/TextField'
import { RadioButton, RadioButtonGroup } from 'material-ui/RadioButton'
import MenuItem from 'material-ui/MenuItem'
import SelectField from 'material-ui/SelectField'
import AutoComplete from 'material-ui/AutoComplete'
import IconButton from 'material-ui/IconButton'
import InfoIcon from 'material-ui/svg-icons/action/info'
import muiThemeable from 'material-ui/styles/muiThemeable'

import visible from 'react-visible'

import Flexbox from 'flexbox-react'

const InputView = ({ value = '', label, onEdit, message = {}, tooltip, muiTheme, ...rest }) => {
  for (let key in rest) {
    if (rest[key] === undefined) {
      delete rest[key]
    }
  }
  const { type, message: text } = message

  const messageStyles = {
    SUCCESS: { color: muiTheme.palette.successColor },
    WARNING: { color: muiTheme.palette.warningColor }
  }

  return (
    <Flexbox flexDirection='row' style={{position: 'relative'}}>
      <TextField
        fullWidth
        errorText={text}
        errorStyle={messageStyles[type]}
        value={value}
        floatingLabelText={label}
        onChange={(e) => onEdit(e.target.value)}
        {...rest} />
      {(tooltip)
        ? (<IconButton style={{position: 'absolute', right: '10px', bottom: '0px'}}
          iconStyle={{width: '20px', height: '20px'}}
          tooltip={tooltip}
          touch
          tooltipPosition='top-left'>
          <InfoIcon />
        </IconButton>)
        : null
      }
    </Flexbox>
  )
}

const Input = visible(muiThemeable()(InputView))

const Password = ({ label = 'Password', ...rest }) => (
  <Input type='password' label={label} {...rest} />
)

const Hostname = ({ label = 'Hostname', onEdit, ...rest }) => (
  <Input label={label} onEdit={(value) => onEdit(value.replace(/\s/g, ''))} {...rest} />
)

const InputAutoView = ({ value = '', options = [], type = 'text', message = {}, onEdit, label, tooltip, muiTheme, ...rest }) => {
  const { type: messageType, message: text } = message
  for (let key in rest) {
    if (rest[key] === undefined) {
      delete rest[key]
    }
  }

  const messageStyles = {
    SUCCESS: { color: muiTheme.palette.successColor },
    WARNING: { color: muiTheme.palette.warningColor }
  }

  return (
    <Flexbox flexDirection='row' style={{position: 'relative'}}>
      <AutoComplete
        menuStyle={{maxHeight: 200, overflowY: 'scroll'}}
        fullWidth
        openOnFocus
        dataSource={options.map((value) => ({text: String(value), value}))}
        filter={AutoComplete.noFilter}
        type={type}
        errorText={text}
        errorStyle={messageStyles[messageType]}
        floatingLabelText={label}
        searchText={String(value)}
        onNewRequest={({value}) => {
          onEdit(value)
        }}
        onUpdateInput={(value) => {
          onEdit(type === 'number' ? Number(value) : value)
        }}
        {...rest} />
      {(tooltip)
        ? (<IconButton style={{position: 'absolute', right: '10px', bottom: '0px'}}
          iconStyle={{width: '20px', height: '20px'}}
          tooltip={tooltip}
          touch
          tooltipPosition='top-left'>
          <InfoIcon />
        </IconButton>)
        : null
      }
    </Flexbox>
  )
}

const InputAuto = visible(muiThemeable()(InputAutoView))

const Port = ({ label = 'Port', ...rest }) => (
  <InputAuto type='number' label={label} {...rest} />
)

const SelectView = ({ value = '', options = [], label = 'Select', onEdit, error, tooltip, ...rest }) => {
  const i = options.findIndex((option) => (typeof option === 'object') ? option.name === value.name : option === value)
  for (let key in rest) {
    if (rest[key] === undefined) {
      delete rest[key]
    }
  }

  return (
    <Flexbox flexDirection='row' style={{position: 'relative'}}>
      <SelectField
        fullWidth
        errorText={error}
        value={i}
        onChange={(e, i) => onEdit(options[i])}
        floatingLabelText={label}
        {...rest}>
        {options.map((d, i) => {
          if (typeof d === 'string') {
            return <MenuItem key={i} value={i} primaryText={d} />
          } else if (typeof d === 'object') {
            return <MenuItem key={i} value={i} primaryText={d.name} />
          } else {
            return null
          }
        })}
      </SelectField>
      {(tooltip)
        ? (<IconButton style={{position: 'absolute', right: '10px', bottom: '0px'}}
          iconStyle={{width: '20px', height: '20px'}}
          tooltip={tooltip}
          touch
          tooltipPosition='top-left'>
          <InfoIcon />
        </IconButton>)
        : null
      }
    </Flexbox>
  )
}

const Select = visible(SelectView)

const RadioSelectionView = ({ value, disabled, options = [], onEdit, ...rest }) => {
  for (let key in rest) {
    if (rest[key] === undefined) {
      delete rest[key]
    }
  }

  return (
    <RadioButtonGroup valueSelected={value} onChange={(e, value) => onEdit(value)} {...rest}>
      {options.map((item, i) =>
        <RadioButton style={{fontSize: '16px', padding: '3px'}} key={i} value={item.value}
          label={item.label} disabled={disabled} />)}
    </RadioButtonGroup>
  )
}

const RadioSelection = visible(RadioSelectionView)

export {
  Input,
  InputAuto,
  Password,
  Hostname,
  Port,
  Select,
  RadioSelection
}
