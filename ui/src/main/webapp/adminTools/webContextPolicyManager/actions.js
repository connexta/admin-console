import { getBins } from '../../reducer'
import { get, post } from 'redux-fetch'

// Bin level
export const replaceAllBins = (bins, whitelistContexts) => ({ type: 'WCPM/REPLACE_ALL_BINS', bins, whitelistContexts })
export const removeBin = () => ({ type: 'WCPM/REMOVE_BIN' })
export const addNewBin = (binNumber) => ({ type: 'WCPM/ADD_BIN', binNumber })
export const editModeOn = (binNumber) => ({ type: 'WCPM/EDIT_MODE_ON', binNumber })
export const editModeCancel = (binNumber) => ({ type: 'WCPM/EDIT_MODE_CANCEL', binNumber })
export const editModeSave = (binNumber) => ({ type: 'WCPM/EDIT_MODE_SAVE', binNumber })
export const confirmRemoveBin = (binNumber) => ({ type: 'WCPM/CONFIRM_REMOVE_BIN', binNumber })
export const cancelRemoveBin = () => ({ type: 'WCPM/CANCEL_REMOVE_BIN' })

// Realm
export const editRealm = (binNumber, value) => ({ type: 'WCPM/EDIT_REALM', binNumber, value })

// Attribute Lists
export const removeAttribute = (attribute) => (binNumber, pathNumber) => ({ type: 'WCPM/REMOVE_ATTRIBUTE_LIST', attribute, binNumber, pathNumber })
export const addAttribute = (attribute) => (binNumber, path) => ({ type: 'WCPM/ADD_ATTRIBUTE_LIST', attribute, binNumber, path })
export const editAttribute = (attribute) => (binNumber, value) => ({ type: 'WCPM/EDIT_ATTRIBUTE_LIST', attribute, binNumber, value })

// Attribute mapping reducers
export const addAttributeMapping = (binNumber) => ({ type: 'WCPM/ADD_ATTRIBUTE_MAPPING', binNumber })
export const removeAttributeMapping = (binNumber, claim) => ({ type: 'WCPM/REMOVE_ATTRIBUTE_MAPPING', binNumber, claim })

// Set Options
export const setPolicyOptions = (options) => ({ type: 'WCPM/SET_OPTIONS', options })

// Errors
export const setError = ({ scope, message }) => ({ type: 'WCPM/ERRORS/SET', scope, message })
export const clearAllErrors = () => ({ type: 'WCPM/ERRORS/CLEAR' })
export const clearComponentError = (scope) => ({ type: 'WCPM/ERRORS/CLEAR_COMPONENT', scope })

// checks for a preceding "/" and that there are only URI-valid characters
// ex PASS: '/', '/abc/def', '/abc123#$!'
// ex FAIL: '', 'abc/', '/^^^'
const isValidContextPath = (path) => {
  return /^(\/[A-Za-z0-9-._~:/?#[\]@!$&'()*+,;=`.%]*)+$/g.test(path)
}

// Persist Field Validations
export const addContextPath = (attribute, binNumber) => (dispatch, getState) => {
  dispatch(clearComponentError('contextPaths'))

  const bins = getBins(getState())
  const newPath = bins[binNumber].newcontextPaths

  // test for non-empty path
  if (!newPath || newPath.trim() === '') { return }

  // simple test for invalid path - backend also validates paths as an additional precaution
  if (!isValidContextPath(newPath)) {
    dispatch(setError({ scope: 'contextPaths', message: 'Invalid context path.' }))
    return
  }

  // test for duplicate paths
  let duplicateBinNumber
  bins.forEach((bin, binNumber) => bin.contextPaths.forEach((oldPath) => { oldPath === newPath ? duplicateBinNumber = binNumber : null }))

  if (duplicateBinNumber !== undefined) {
    if (duplicateBinNumber === 0) {
      dispatch(setError({ scope: 'contextPaths', message: 'This path is in the Whitelist.' }))
      return
    }
    dispatch(setError({ scope: 'contextPaths', message: 'This path is already being used in bin #' + duplicateBinNumber + '.' }))
    return
  }

  dispatch(addAttribute(attribute)(binNumber))
}

// Fetch
export const updatePolicyBins = (url) => async (dispatch) => {
  const res = await dispatch(get(url, { id: 'wcpm' }))
  const json = await res.json()

  if (res.status === 200) {
    dispatch(replaceAllBins(json[0].contextPolicyBins, json[0].whiteListContexts))
    dispatch(fetchOptions('/admin/beta/config/probe/context-policy-manager/options'))
  }
}

const isBlank = (string) => { return !string || !string.trim() }
const errorResult = (scope, message) => ({ scope, message })

const checkContextPathValidity = (bin) => {
  let errors = []

  if (!isBlank(bin.newcontextPaths)) {
    errors.push(errorResult('contextPaths', 'Field edited but not added. Please add or clear before saving.'))
  }
  if (bin.contextPaths.length === 0) {
    errors.push(errorResult('general', 'Must have at least 1 Context Path'))
  } else if (bin.contextPaths.some((path) => (isBlank(path)))) {
    errors.push(errorResult('general', 'Cannot have empty Context Paths'))
  }
  return errors
}

const checkRealmValidity = (bin) => {
  let errors = []
  if (bin.name === 'WHITELIST') return errors

  if (isBlank(bin.realm)) {
    errors.push(errorResult('general', 'Realm cannot be blank.'))
  }
  return errors
}

const checkAuthenticationTypeValidity = (bin) => {
  let errors = []
  if (bin.name === 'WHITELIST') return errors

  if (!isBlank(bin.newauthenticationTypes)) {
    errors.push(errorResult('authTypes', 'Field edited but not added. Please add or clear before saving.'))
  }
  if (bin.authenticationTypes.length === 0) {
    errors.push(errorResult('general', 'Must have at least 1 Authentication Type'))
  } else if (bin.authenticationTypes.some((type) => (isBlank(type)))) {
    errors.push(errorResult('general', 'Cannot have empty Authentication Types'))
  }
  return errors
}

const checkRequiredAttributesValidity = (bin) => {
  let errors = []
  if (bin.name === 'WHITELIST') return errors

  if (!isBlank(bin.newrequiredClaim)) {
    errors.push(errorResult('requiredClaim', 'Field edited but not added. Please add or clear before saving.'))
  }
  if (!isBlank(bin.newrequiredAttribute)) {
    errors.push(errorResult('requiredAttribute', 'Field edited but not added. Please add or clear before saving.'))
  }
  return errors
}

export const persistChanges = (binNumber, url, isDeleting) => async (dispatch, getState) => {
  dispatch(clearAllErrors())
  // Check for non-empty edit fields
  const bin = getBins(getState())[getState().toJS().wcpm.editingBinNumber]

  // do not perform field validation checks if bin is being removed
  if (!isDeleting) {
    let hasErrors = false

    checkContextPathValidity(bin).forEach((error) => {
      hasErrors = true
      dispatch(setError(error))
    })
    checkRealmValidity(bin).forEach((error) => {
      hasErrors = true
      dispatch(setError(error))
    })
    checkAuthenticationTypeValidity(bin).forEach((error) => {
      hasErrors = true
      dispatch(setError(error))
    })
    checkRequiredAttributesValidity(bin).forEach((error) => {
      hasErrors = true
      dispatch(setError(error))
    })
    if (hasErrors) {
      return
    }
  }

  // TODO: check for duplicate context paths

  // dispatch(editModeSave(binNumber))

  const formattedBody = {
    configurationType: 'context-policy-manager',
    contextPolicyBins: getBins(getState()).slice(1),
    whiteListContexts: getBins(getState())[0].contextPaths
  }

  const body = JSON.stringify(formattedBody)

  const res = await dispatch(post(url, { id: 'wcpm', body }))
  const json = await res.json()

  // check for server exceptions
  if (json.messages[0].exceptions && json.messages[0].exceptions.length > 0) {
    dispatch(setError({ scope: 'general', message: 'The server encountered an error. Please check the server logs for more information.' }))
    return
  }
  // handle responses
  const result = json.messages[0].subType
  if (result === 'SUCCESSFUL_PERSIST') {
    dispatch(editModeSave(binNumber))
  } else {
    dispatch(setError({ scope: 'general', message: 'Could not save. Reason for issue: ' + json.messages[0].message }))
  }
}

export const fetchOptions = (url) => async (dispatch, getState) => {
  const formattedBody = {
    configurationType: 'context-policy-manager',
    contextPolicyBins: getBins(getState()).slice(1),
    whiteListContexts: getBins(getState())[0].contextPaths
  }

  const body = JSON.stringify(formattedBody)

  const res = await dispatch(post(url, { id: 'wcpm', body }))
  const json = await res.json()

  if (res.status === 200) {
    dispatch(setPolicyOptions(json.probeResults))
  }
}

export const confirmRemoveBinAndPersist = (binNumber, url) => (dispatch) => {
  dispatch(confirmRemoveBin(binNumber))
  dispatch(persistChanges(binNumber, url, true))
}
