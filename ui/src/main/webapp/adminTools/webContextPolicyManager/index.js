import React from 'react'
import Paper from 'material-ui/Paper'
import {connect} from 'react-redux'
import Mount from 'react-mount'
import {
  getBins,
  getOptions,
  getEditingBinNumber,
  getConfirmDelete,
  getWcpmErrors,
  isSubmitting
} from '../../reducer'
import {
  addAttribute,
  editAttribute,
  removeAttribute,
  addNewBin,
  editModeOn,
  editModeCancel,
  editRealm,
  updatePolicyBins,
  persistChanges,
  addAttributeMapping,
  removeAttributeMapping,
  confirmRemoveBinAndPersist,
  addContextPath
} from './actions'
import Flexbox from 'flexbox-react'
import IconButton from 'material-ui/IconButton'
import TextField from 'material-ui/TextField'
import Divider from 'material-ui/Divider'
import FloatingActionButton from 'material-ui/FloatingActionButton'
import SelectField from 'material-ui/SelectField'
import MenuItem from 'material-ui/MenuItem'
import FlatButton from 'material-ui/FlatButton'
import RaisedButton from 'material-ui/RaisedButton'
import CircularProgress from 'material-ui/CircularProgress'
import {
  Table,
  TableBody,
  TableHeader,
  TableHeaderColumn,
  TableRow,
  TableRowColumn
} from 'material-ui/Table'
import {cyanA700} from 'material-ui/styles/colors'
import CancelIcon from 'material-ui/svg-icons/content/remove-circle-outline'
import DeleteIcon from 'material-ui/svg-icons/action/delete'
import AddIcon from 'material-ui/svg-icons/content/add-circle-outline'
import ContentAdd from 'material-ui/svg-icons/content/add'
import EditModeIcon from 'material-ui/svg-icons/editor/mode-edit'
import ClearIcon from 'material-ui/svg-icons/content/clear'
import {
  contextPolicyStyle,
  infoTitle,
  infoSubtitle,
  policyBinOuterStyle,
  editPaneStyle,
  newBinStyle,
  infoSubtitleLeft,
  realmNameStyle,
  disabledPanelStyle,
  newBinDisabledStyle,
  contextPathGroupStyle,
  whitelistContextPathGroupStyle,
  claimsAttributeStyle,
  submitting,
  error,
  errorBorder
} from './styles.less'

import confirmable from 'react-confirmable'

let Edit = ({editing, binNumber, editModeOn}) => {
  return !editing ? (
    <FloatingActionButton className={editPaneStyle} onClick={editModeOn}><EditModeIcon /></FloatingActionButton>
  ) : null
}

Edit = connect(null, (dispatch, { binNumber }) => ({ editModeOn: () => dispatch(editModeOn(binNumber)) }))(Edit)

let ContextPathItem = ({ contextPath, binNumber, pathNumber, removePath, editing, attribute }) => (
  <div>
    <Divider />
    <Flexbox flexDirection='row' justifyContent='space-between'>
      <div className={contextPolicyStyle}>{contextPath}</div>
      {editing ? (<IconButton tooltip={'Remove'} tooltipPosition='top-center' onClick={removePath}><CancelIcon /></IconButton>) : null}
    </Flexbox>
  </div>
)
ContextPathItem = connect(null, (dispatch, { binNumber, pathNumber, attribute }) => ({ removePath: () => dispatch(removeAttribute(attribute)(binNumber, pathNumber)) }))(ContextPathItem)

let NewContextPathItem = ({ binNumber, addPath, onEdit, newPath, attribute, addButtonVisible = true, error }) => (
  <div>
    <Divider />
    <Flexbox style={{ position: 'relative' }} flexDirection='row' justifyContent='space-between'>
      <TextField hintStyle={{ padding: '0px 10px' }} inputStyle={{ padding: '0px 10px' }} style={{ width: '100%' }} id='name' hintText='Add New Path' onChange={(event, value) => onEdit(value)} value={newPath || ''} errorText={error} />
      {(addButtonVisible) ? (<IconButton style={{ position: 'absolute', right: '0px' }} tooltip={'Add'} tooltipPosition='top-center' onClick={addPath}><AddIcon color={cyanA700} /></IconButton>) : null }
      {(newPath && newPath.trim() !== '')
        ? <IconButton style={{ position: 'absolute', left: '-10px', width: '10px', height: '10px' }} iconStyle={{ width: '10px', height: '10px' }} onClick={() => onEdit('')}><ClearIcon /></IconButton>
        : null
      }
    </Flexbox>
  </div>
)
NewContextPathItem = connect((state) => ({
  error: getWcpmErrors(state).contextPaths
}), (dispatch, { binNumber, attribute }) => ({
  addPath: () => dispatch(addContextPath(attribute, binNumber)),
  onEdit: (value) => dispatch(editAttribute(attribute)(binNumber, value))
}))(NewContextPathItem)

let ContextPathGroup = ({ bin, binNumber, editing }) => (
  <div className={(bin.name === 'WHITELIST') ? whitelistContextPathGroupStyle : contextPathGroupStyle}>
    <p className={infoSubtitleLeft}>Context Paths</p>
    {bin.contextPaths.map((contextPath, pathNumber) => (<ContextPathItem attribute='contextPaths' contextPath={contextPath} key={pathNumber} binNumber={binNumber} pathNumber={pathNumber} editing={editing} />))}
    {editing ? <NewContextPathItem binNumber={binNumber} attribute='contextPaths' newPath={bin['newcontextPaths']} /> : null}
  </div>
)

let NewSelectItem = ({ binNumber, addPath, onEdit, newPath, attribute, options, addButtonVisible = true, error }) => (
  <div>
    <Divider />
    <Flexbox style={{ position: 'relative' }} flexDirection='row' justifyContent='space-between'>
      <SelectField hintStyle={{ padding: '0px 10px' }}
        inputStyle={{ padding: '0px 10px' }}
        style={{ width: '100%' }} id='name'
        hintText='Add New Type'
        onChange={(event, i, value) => onEdit(value)}
        value={newPath || ''}
        errorText={error}>
        { options.map((item, key) => (<MenuItem value={item} key={key} primaryText={item} />)) }
      </SelectField>
      {(addButtonVisible) ? (
        <IconButton style={{ position: 'absolute', right: '0px' }}
          tooltip={'Add'} tooltipPosition='top-center'
          onClick={addPath}>
          <AddIcon color={cyanA700} />
        </IconButton>
      ) : null }
      {(newPath && newPath.trim() !== '') ? (
        <IconButton style={{ position: 'absolute', left: '-15px', width: '10px', height: '10px' }}
          iconStyle={{ width: '10px', height: '10px' }}
          onClick={() => onEdit('')}>
          <ClearIcon />
        </IconButton>
      ) : null }
    </Flexbox>
  </div>
)
NewSelectItem = connect(null, (dispatch, { binNumber, attribute }) => ({
  addPath: () => dispatch(addAttribute(attribute)(binNumber)),
  onEdit: (value) => dispatch(editAttribute(attribute)(binNumber, value))
}))(NewSelectItem)

let Realm = ({ bin, binNumber, policyOptions, editRealm, editing }) => {
  return editing ? (
    <Flexbox flexDirection='row'>
      <SelectField fullWidth style={{margin: '0px 10px'}} id='realm' value={bin.realm} onChange={(event, i, value) => editRealm(value)}>
        {policyOptions.realms.map((realm, i) => (<MenuItem value={realm} primaryText={realm} key={i} />))}
      </SelectField>
    </Flexbox>
  ) : (
    <p className={realmNameStyle}>{bin.realm}</p>
  )
}
Realm = connect(
  (state) => ({
    policyOptions: getOptions(state)
  }),
  (dispatch, { binNumber }) => ({
    editRealm: (value) => dispatch(editRealm(binNumber, value))
  }))(Realm)

let ConfirmationPanel = ({ bin, binNumber, removeBin, saveAndPersist, editModeCancel, editing, confirmRemoveBinAndPersist, confirmDelete, cancelRemoveBin, allowDelete }) => {
  const ConfirmDelete = confirmable(IconButton)

  return editing ? (
    <Flexbox flexDirection='row' justifyContent='center' style={{ padding: '10px 0px 5px' }}>
      <FlatButton style={{ margin: '0 10' }} label='Cancel' labelPosition='after' secondary onClick={editModeCancel} />
      <RaisedButton style={{ margin: '0 10' }} label='Save' primary onClick={saveAndPersist} />
      { (allowDelete) ? (
        <div style={{ position: 'absolute', right: '0px', bottom: '0px' }}>
          <ConfirmDelete
            onClick={removeBin}
            confirmableMessage={'Confirm delete bin?'}
            confirmableStyle={{ margin: '5px' }}
            tooltip={'Delete'}
            tooltipPosition='top-center'>
            <DeleteIcon />
          </ConfirmDelete>
        </div>)
        : null }
    </Flexbox>
  ) : null
}

ConfirmationPanel = connect((state) => ({
  confirmDelete: getConfirmDelete(state)
}), (dispatch, { binNumber }) => ({
  saveAndPersist: () => dispatch(persistChanges(binNumber, '/admin/beta/config/persist/context-policy-manager/edit')),
  editModeCancel: () => dispatch(editModeCancel(binNumber)),
  removeBin: () => dispatch(confirmRemoveBinAndPersist(binNumber, '/admin/beta/config/persist/context-policy-manager/edit'))
}))(ConfirmationPanel)

let AuthTypesGroup = ({ bin, binNumber, policyOptions, editing, error }) => (
  <div>
    {bin.authenticationTypes.map((contextPath, pathNumber) => (<ContextPathItem attribute='authenticationTypes' contextPath={contextPath} key={pathNumber} binNumber={binNumber} pathNumber={pathNumber} editing={editing} />))}
    {editing ? (
      <NewSelectItem binNumber={binNumber} attribute='authenticationTypes' options={policyOptions.authenticationTypes.filter((option) => !bin.authenticationTypes.includes(option))} newPath={bin['newauthenticationTypes']} error={error} />
    ) : null }
  </div>
)
AuthTypesGroup = connect(
  (state) => ({
    error: getWcpmErrors(state).authenticationTypes,
    policyOptions: getOptions(state)
  }))(AuthTypesGroup)

let AttributeTableGroup = ({ bin, binNumber, policyOptions, editAttribute, removeAttributeMapping, addAttributeMapping, editing, claimError, attrError }) => (
  <Table selectable={false}>
    <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
      <TableRow>
        <TableHeaderColumn>STS Claim</TableHeaderColumn>
        <TableHeaderColumn style={{ width: 120 }}>Claim Value</TableHeaderColumn>
      </TableRow>
    </TableHeader>
    <TableBody displayRowCheckbox={false}>
      {Object.keys(bin.requiredAttributes).map((key, i) =>
        <TableRow key={i}>
          <TableRowColumn className={claimsAttributeStyle}>
            <span>{key}</span>
          </TableRowColumn>
          <TableRowColumn style={{ width: 120, position: 'relative' }}>
            <span>{bin.requiredAttributes[key]}</span>
            {editing ? (
              <IconButton style={{ position: 'absolute', right: 0, top: 0 }} tooltip={'Remove'} tooltipPosition='top-center' onClick={() => removeAttributeMapping(key)}><CancelIcon /></IconButton>
            ) : null }
          </TableRowColumn>
        </TableRow>)}
      {editing ? (
        <TableRow>
          <TableRowColumn style={{ position: 'relative' }}>
            <SelectField style={{ margin: '0px', width: '100%', fontSize: '14px' }} id='claims' value={bin.newrequiredClaim || ''} onChange={(event, i, value) => editAttribute('requiredClaim', value)} hintText='Claim' errorText={claimError}>
              {policyOptions.claims.map((claim, i) => (<MenuItem style={{ fontSize: '12px' }} value={claim} primaryText={claim} key={i} />))}
            </SelectField>
            {(bin.newrequiredClaim && bin.newrequiredClaim.trim() !== '')
              ? <IconButton style={{ position: 'absolute', left: '-5px', width: '10px', height: '10px' }} iconStyle={{ width: '10px', height: '10px' }} onClick={() => editAttribute('requiredClaim', '')}><ClearIcon /></IconButton>
              : null
            }
          </TableRowColumn>
          <TableRowColumn style={{ width: 120, position: 'relative' }}>
            <TextField fullWidth style={{ margin: '0px', fontSize: '14px' }} id='attributes' value={bin.newrequiredAttribute || ''} onChange={(event, value) => editAttribute('requiredAttribute', value)} hintText='Claim Value' errorText={attrError} />
            <IconButton style={{ position: 'absolute', right: 0, top: 0 }} tooltip={'Add'} tooltipPosition='top-center' onClick={addAttributeMapping}><AddIcon color={cyanA700} /></IconButton>
            {(bin.newrequiredAttribute && bin.newrequiredAttribute.trim() !== '')
              ? <IconButton style={{ position: 'absolute', left: '-5px', width: '10px', height: '10px' }} iconStyle={{ width: '10px', height: '10px' }} onClick={() => editAttribute('requiredAttribute', '')}><ClearIcon /></IconButton>
              : null
            }
          </TableRowColumn>
        </TableRow>
      ) : null }
    </TableBody>
  </Table>
)
AttributeTableGroup = connect(
  (state) => ({
    policyOptions: getOptions(state),
    claimError: getWcpmErrors(state).requiredClaim,
    attrError: getWcpmErrors(state).requiredAttribute
  }),
  (dispatch, { binNumber }) => ({
    addAttributeMapping: () => dispatch(addAttributeMapping(binNumber)),
    removeAttributeMapping: (claim) => dispatch(removeAttributeMapping(binNumber, claim)),
    editAttribute: (attribute, value) => dispatch(editAttribute(attribute)(binNumber, value))
  }))(AttributeTableGroup)

const DisabledPanel = () => (
  <div className={disabledPanelStyle} />
)

const WhitelistBin = ({ policyBin, binNumber, editing, editingBinNumber }) => (
  <Paper className={policyBinOuterStyle} >
    <Flexbox flexDirection='row'>
      <ContextPathGroup binNumber={binNumber} bin={policyBin} editing={editing} />
      <Flexbox flex='1' style={{ padding: '5px' }} flexDirection='column' justifyContent='center'>
        <div>
          <p className={infoTitle}>
            Whitelisted Contexts
          </p>
          <p className={infoSubtitle}>
            The paths listed here will not be checked against any policies and all requests under these paths will be permitted. Use with caution!
          </p>
        </div>
      </Flexbox>
    </Flexbox>
    <ErrorBanner editing={editing} />
    <Edit editing={editing} binNumber={binNumber} />
    <ConfirmationPanel bin={policyBin} binNumber={binNumber} editing={editing} allowDelete={false} />
    { (!editing && editingBinNumber !== null) ? <DisabledPanel /> : null }
  </Paper>
)

let ErrorBanner = ({ editing, messages }) => {
  return (editing && messages && messages.length > 0) ? (
    <div className={errorBorder}>
      { messages.map((message, i) => (<Flexbox key={i} flexDirection='row' justifyContent='center' className={error}>{message}</Flexbox>)) }
    </div>
    ) : null
}
ErrorBanner = connect(
  (state) => ({
    messages: getWcpmErrors(state).general
  }))(ErrorBanner)

const PolicyBin = ({ policyBin, binNumber, editing, editingBinNumber }) => (
  <Paper className={policyBinOuterStyle} >
    <Flexbox flexDirection='row'>
      <ContextPathGroup binNumber={binNumber} bin={policyBin} editing={editing} />
      <div style={{ width: '20%', padding: '5px', boxSizing: 'border-box' }}>
        <p className={infoSubtitleLeft}>Realm</p>
        <Divider />
        <Realm bin={policyBin} binNumber={binNumber} editing={editing} />
        <p className={infoSubtitleLeft}>Authentication Types</p>
        <AuthTypesGroup bin={policyBin} binNumber={binNumber} editing={editing} />
      </div>
      <div style={{ width: '60%', padding: '5px', boxSizing: 'border-box' }}>
        <p className={infoSubtitleLeft}>{(editing) ? 'Required Subject Claims (Optional)' : 'Required Subject Claims'}</p>
        <Divider />
        <div>
          <AttributeTableGroup bin={policyBin} binNumber={binNumber} editing={editing} />
        </div>
      </div>
    </Flexbox>
    <ErrorBanner editing={editing} />
    <Edit editing={editing} binNumber={binNumber} />
    <ConfirmationPanel bin={policyBin} binNumber={binNumber} editing={editing} allowDelete={!policyBin.name} /> { /* Named bins are special bins that are non-deletable, ex. 'Whitelist' or 'NewBin' */ }
    { (!editing && editingBinNumber !== null) ? <DisabledPanel /> : null }
  </Paper>
)

let PolicyBins = ({ policies, editingBinNumber }) => (
  <div>
    { policies.map((policyBin, binNumber) => {
      return (policyBin.name === 'WHITELIST')
        ? (<WhitelistBin policyBin={policyBin} key={binNumber} binNumber={binNumber} editing={binNumber === editingBinNumber} editingBinNumber={editingBinNumber} />)
        : (<PolicyBin policyBin={policyBin} key={binNumber} binNumber={binNumber} editing={binNumber === editingBinNumber} editingBinNumber={editingBinNumber} />)
    })}
    <NewBin editing={editingBinNumber !== null} />
  </div>
)
PolicyBins = connect((state) => ({
  policies: getBins(state),
  editingBinNumber: getEditingBinNumber(state)
}))(PolicyBins)

let NewBin = ({ policies, addNewBin, nextBinNumber, editing }) => {
  if (editing) {
    return (
      <Paper style={{ position: 'relative', width: '100%', height: '100px', margin: '5px 0px', textAlign: 'center', backgroundColor: '#EEE' }} >
        <Flexbox className={newBinDisabledStyle} flexDirection='column' justifyContent='center' alignItems='center'>
          <FloatingActionButton disabled>
            <ContentAdd />
          </FloatingActionButton>
        </Flexbox>
      </Paper>
    )
  } else {
    return (
      <Paper style={{ position: 'relative', width: '100%', height: '100px', margin: '5px 0px', textAlign: 'center', backgroundColor: '#EEE' }} onClick={() => addNewBin(nextBinNumber)}>
        <Flexbox className={newBinStyle} flexDirection='column' justifyContent='center' alignItems='center'>
          <FloatingActionButton>
            <ContentAdd />
          </FloatingActionButton>
        </Flexbox>
      </Paper>
    )
  }
}
NewBin = connect((state) => ({ nextBinNumber: getBins(state).length }), { addNewBin })(NewBin)

let wcpm = ({ updatePolicyBins, isSubmitting }) => (
  <Mount on={() => updatePolicyBins('/admin/beta/config/configurations/context-policy-manager')}>
    <div>
      <div style={{ padding: 20 }}>
        <p className={infoTitle}>Web Context Policy Manager</p>
        <p className={infoSubtitle}>
          The Web Context Policy Manager defines security policies for all subpaths of this web server.
          It defines the realms a path should be authenticated against, the type of authentication that
          a path requires, and any user attributes that are required for authorization.
        </p>

        <p className={infoSubtitle}>
          Any subpaths of a configured path will inherit its parent's policy. For example, in a system
          where a policy is configured for '/a', its policy applies to '/a/b' and '/a/b/c' unless otherwise
          specified.
        </p>
      </div>
      <PolicyBins />
      {isSubmitting
        ? (<div className={submitting}>
          <Flexbox justifyContent='center' alignItems='center' width='100%'>
            <CircularProgress size={60} thickness={7} />
          </Flexbox>
        </div>)
        : null
      }
    </div>
  </Mount>
)
export default connect(
  (state) => ({
    isSubmitting: isSubmitting(state, 'wcpm')
  }), {
    updatePolicyBins
  })(wcpm)
