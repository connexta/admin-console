import React from 'react'

import Flexbox from 'flexbox-react'
import confirmable from 'react-confirmable'
import visible from 'react-visible'

import CancelIcon from 'material-ui/svg-icons/content/remove-circle-outline'
import DeleteIcon from 'material-ui/svg-icons/action/delete'
import EditModeIcon from 'material-ui/svg-icons/editor/mode-edit'
import FlatButton from 'material-ui/FlatButton'
import FloatingActionButton from 'material-ui/FloatingActionButton'
import IconButton from 'material-ui/IconButton'
import Paper from 'material-ui/Paper'
import RaisedButton from 'material-ui/RaisedButton'
import muiThemeable from 'material-ui/styles/muiThemeable'

import Des from 'components/Description'

import {
  editRegion,
  editButton
} from './styles.css'

const styles = {
  disabled: {
    backgroundColor: 'rgba(125, 125, 125, 0.25)',
    position: 'absolute',
    left: 0,
    right: 0,
    top: 0,
    bottom: 0,
    zIndex: 200
  }
}

export const Description = ({ children }) => (
  <Des style={{ textAlign: 'left' }}>{children}</Des>
)

export const H1 = muiThemeable()(({ children, muiTheme, style }) => (
  <h1 style={{
    color: muiTheme.palette.textColor,
    ...style
  }}>{children}</h1>
))

export const H2 = muiThemeable()(({ children, muiTheme }) => (
  <h2 style={{
    color: muiTheme.palette.textColor
  }}>{children}</h2>
))

export const Header = muiThemeable()(({ children, style, muiTheme }) => (
  <h2 style={{
    margin: 0,
    color: muiTheme.palette.textColor,
    ...style
  }}>{children}</h2>
))

export const Subtitle = muiThemeable()(({ muiTheme, children }) => (
  <h4 style={{ margin: 0, color: muiTheme.palette.primary1Color }}>{children}</h4>
))

const DeleteIconThemed = muiThemeable()(({ muiTheme }) => (
  <DeleteIcon style={{ color: muiTheme.palette.errorColor }} />
))

export const EditRegion = ({ children, onEdit }) => (
  <div className={editRegion} style={{ position: 'relative' }}>
    <FloatingActionButton className={editButton} onClick={onEdit}>
      <EditModeIcon />
    </FloatingActionButton>
    {children}
  </div>
)

export const Panel = ({ children, style }) => (
  <Paper style={{ marginBottom: '20px', padding: '10px', ...style }}>
    {children}
  </Paper>
)

export const Disabled = ({ children }) => (
  <div style={{ position: 'relative' }}>
    <div style={styles.disabled} />
    {children}
  </div>
)

export const CancelButton = ({ onClick }) => (
  <FlatButton
    label='Cancel'
    labelPosition='after'
    secondary
    onClick={onClick} />
)

const ConfirmDelete = confirmable(IconButton)

export const DeleteButton = ({ onClick, style }) => (
  <div style={style}>
    <ConfirmDelete
      onClick={onClick}
      confirmableMessage='Confirm policy deletion?'
      confirmableStyle={{ margin: '-10px 5px' }}
      tooltip='Delete'
      tooltipPosition='top-center'>
      <DeleteIconThemed />
    </ConfirmDelete>
  </div>
)

export const VisibleDeleteButton = visible(DeleteButton)

export const ConfirmationPanel = ({ onSave, onCancel }) => (
  <Flexbox flexDirection='row' justifyContent='center' style={{ padding: '35px 0px 5px' }}>
    <CancelButton onClick={onCancel} />
    <RaisedButton
      primary
      label='Save'
      onClick={onSave} />
  </Flexbox>
)

export const RemoveButton = ({ onRemove }) => (
  <IconButton onClick={onRemove} tooltip='Remove' tooltipPosition='top-center'>
    <CancelIcon />
  </IconButton>
)

export const EditItem = ({ children, leftIcon, rightIcon, style }) => (
  <Flexbox flexDirection='row' alignItems='center' justifyContent='space-between' style={style}>
    {leftIcon}
    <Flexbox flex='1'>{children}</Flexbox>
    {rightIcon}
  </Flexbox>
)

// TODO: move to a shared location to be reused by other components
const messages = {
  NO_ROOT_CONTEXT: 'Cannot remove the root "/" context path.',
  INVALID_CLAIM_TYPE: 'The given claim is not supported by the system.',
  INVALID_CONTEXT_PATH: 'Invalid context path.',
  MISSING_REQUIRED_FIELD: 'A required field is missing.',
  EMPTY_FIELD: 'The provided field cannot be empty.',
  MISSING_KEY_VALUE: 'The provied mapping is missing value for a key.',
  INVALID_PORT_RANGE: 'The provided port is out of range.',
  INVALID_HOSTNAME: 'The provided hostname is invalid.',
  FAILED_PERSIST: 'Unable to save changes.',
  UNSUPPORTED_ENUM: 'The provided value is not recognized.'
}

export const ServerErrors = muiThemeable()(({ muiTheme, errors = [] }) => (
  <div>
    {errors.map(({ message: code }, i) =>
      <Flexbox
        key={i}
        flexDirection='row'
        justifyContent='center'
        style={{
          padding: '10px 0',
          fontSize: '14px',
          fontWeight: 'bold',
          background: muiTheme.palette.errorColor,
          color: muiTheme.palette.alternateTextColor
        }}>
        {messages[code] || code}
      </Flexbox>)}
  </div>
))

export const Layout = ({ children, subtitle, description }) => (
  <Flexbox flexDirection='row' flexWrap='wrap' style={{ padding: '15px 5px' }}>
    <Flexbox flex='1' flexDirection='column' style={{ minWidth: 300, marginRight: 20 }}>
      <div>
        <Subtitle>{subtitle}</Subtitle>
        <Description>{description}</Description>
      </div>
    </Flexbox>
    <Flexbox flex='1' flexDirection='column' style={{ minWidth: 300 }}>
      {children}
    </Flexbox>
  </Flexbox>
)
