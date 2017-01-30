import React from 'react'

import { connect } from 'react-redux'
import { getConfig } from '../../reducer'
import { editConfig } from './actions'

import TextField from 'material-ui/TextField'
import { RadioButton, RadioButtonGroup } from 'material-ui/RadioButton'
import MenuItem from 'material-ui/MenuItem'
import SelectField from 'material-ui/SelectField'
import AutoComplete from 'material-ui/AutoComplete'
import IconButton from 'material-ui/IconButton'
import InfoIcon from 'material-ui/svg-icons/action/info'

import Flexbox from 'flexbox-react'

import { green400, orange500 } from 'material-ui/styles/colors'

const mapStateToProps = (state, { id }) => getConfig(state, id)

const mapDispatchToProps = (dispatch, { id }) => ({
  onEdit: (value) => dispatch(editConfig(id, value))
})

const messageStyles = {
  SUCCESS: { color: green400 },
  WARNING: { color: orange500 }
}

const InputView = ({ value = '', label, onEdit, message = {}, tooltip, ...rest }) => {
  const { type, message: text } = message

  return (
    <Flexbox flexDirection='row' style={{ position: 'relative' }}>
      <TextField
        fullWidth
        errorText={text}
        errorStyle={messageStyles[type]}
        value={value}
        floatingLabelText={label}
        onChange={(e) => onEdit(e.target.value)}
        {...rest} />
      {(tooltip)
        ? (<IconButton style={{ position: 'absolute', right: '10px', bottom: '0px' }}
          iconStyle={{ width: '20px', height: '20px' }}
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

const Input = connect(mapStateToProps, mapDispatchToProps)(InputView)

const Password = ({ label = 'Password', ...rest }) => (
  <Input type='password' label={label} {...rest} />
)

const Hostname = ({ label = 'Hostname', ...rest }) => (
  <Input label={label} {...rest} />
)

const InputAutoView = ({ value = '', options = [], type = 'text', message = {}, onEdit, label, tooltip, ...rest }) => {
  const { type: messageType, message: text } = message

  return (
    <Flexbox flexDirection='row' style={{ position: 'relative' }}>
      <AutoComplete
        menuStyle={{ maxHeight: 200, overflowY: 'scroll' }}
        fullWidth
        openOnFocus
        dataSource={options.map((value) => ({ text: String(value), value }))}
        filter={AutoComplete.noFilter}
        type={type}
        errorText={text}
        errorStyle={messageStyles[messageType]}
        floatingLabelText={label}
        searchText={String(value)}
        onNewRequest={({ value }) => { onEdit(value) }}
        onUpdateInput={(value) => { onEdit(type === 'number' ? Number(value) : value) }}
        {...rest} />
      {(tooltip)
        ? (<IconButton style={{ position: 'absolute', right: '10px', bottom: '0px' }}
          iconStyle={{ width: '20px', height: '20px' }}
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

const InputAuto = connect(mapStateToProps, mapDispatchToProps)(InputAutoView)

const Port = ({ value = 0, label = 'Port', ...rest }) => (
  <InputAuto type='number' value={value} label={label} {...rest} />
)

const SelectView = ({ value = '', options = [], label = 'Select', onEdit, error, tooltip, ...rest }) => {
  const i = options.findIndex((option) => (typeof option === 'object') ? option.name === value.name : option === value)
  return (
    <Flexbox flexDirection='row' style={{ position: 'relative' }}>
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
        ? (<IconButton style={{ position: 'absolute', right: '10px', bottom: '0px' }}
          iconStyle={{ width: '20px', height: '20px' }}
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

const Select = connect(mapStateToProps, mapDispatchToProps)(SelectView)

const RadioSelectionView = ({ value, disabled, options = [], onEdit, ...rest }) => (
  <RadioButtonGroup valueSelected={value} onChange={(e, value) => onEdit(value)} {...rest}>
    {options.map((item, i) =>
      <RadioButton style={{ fontSize: '16px', padding: '3px' }} key={i} value={item.value} label={item.label} disabled={disabled} />)}
  </RadioButtonGroup>
)

const RadioSelection = connect(mapStateToProps, mapDispatchToProps)(RadioSelectionView)

export {
  Input,
  InputAuto,
  Password,
  Hostname,
  Port,
  Select,
  RadioSelection
}
