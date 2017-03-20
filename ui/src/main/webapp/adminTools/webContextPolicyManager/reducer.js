import { combineReducers } from 'redux-immutable'
import { fromJS, List } from 'immutable'

import sub from 'redux-submarine'

const emptyBin = {
  name: 'NewBin',
  realm: '',
  authenticationTypes: [],
  requiredAttributes: {},
  contextPaths: []
}

const bins = (state = List(), { type, bin, bins, whitelistContexts, path, binNumber, pathNumber, value, attribute, claim }) => {
  switch (type) {
    case 'WCPM/REPLACE_ALL_BINS':
      let whitelistBin = {...emptyBin, name: 'WHITELIST', contextPaths: whitelistContexts}
      return fromJS([whitelistBin, ...bins])

    // Bin Level
    case 'WCPM/ADD_BIN':
      return state.push(fromJS(emptyBin))
    case 'WCPM/CONFIRM_REMOVE_BIN':
      return state.delete(binNumber)
    case 'WCPM/EDIT_MODE_ON':
      return state.update(binNumber, (bin) => bin.merge({ beforeEdit: bin }))
    case 'WCPM/EDIT_MODE_CANCEL':
      if (state.hasIn([binNumber, 'beforeEdit'])) {
        return state.update(binNumber, (bin) => bin.get('beforeEdit'))
      } else {
        return state.delete(binNumber)
      }
    case 'WCPM/EDIT_MODE_SAVE':
      return state.update(binNumber, (bin) => bin.delete('beforeEdit')).update(binNumber, (bin) => bin.delete('name'))

    // Realm
    case 'WCPM/EDIT_REALM':
      return state.setIn([binNumber, 'realm'], value)

    // Attribute Lists
    case 'WCPM/ADD_ATTRIBUTE_LIST':
      // check for empty values
      let newValue = state.getIn([binNumber, 'new' + attribute])
      if (!newValue || newValue.trim() === '') { return state }
      return state.update(binNumber, (bin) => bin.update(attribute, (paths) => paths.push(newValue)).set('new' + attribute, ''))
    case 'WCPM/REMOVE_ATTRIBUTE_LIST':
      return state.deleteIn([binNumber, attribute, pathNumber])
    case 'WCPM/EDIT_ATTRIBUTE_LIST':
      return state.setIn([binNumber, 'new' + attribute], value)

    // Attribute Mappings
    case 'WCPM/ADD_ATTRIBUTE_MAPPING':
      // TODO: don't allow adding a key that already exists
      // check for empty value
      let newClaim = state.getIn([binNumber, 'newrequiredClaim'])
      let newAttr = state.getIn([binNumber, 'newrequiredAttribute'])
      if (!newClaim || !newAttr || newClaim.trim() === '' || newAttr.trim() === '') { return state }
      return state.setIn([binNumber, 'requiredAttributes', newClaim], newAttr).setIn([binNumber, 'newrequiredAttribute'], '').setIn([binNumber, 'newrequiredClaim'], '')
    case 'WCPM/REMOVE_ATTRIBUTE_MAPPING':
      return state.deleteIn([binNumber, 'requiredAttributes', claim])

    default:
      return state
  }
}
const emptyClaims = ({
  realms: [],
  authenticationTypes: [],
  claims: []
})

const options = (state = fromJS(emptyClaims), { type, options }) => {
  switch (type) {
    case 'WCPM/SET_OPTIONS':
      // add blank option to clear
      options.authenticationTypes = ['', ...options.authenticationTypes]
      options.claims = ['', ...options.claims]
      return fromJS(options)
    default:
      return state
  }
}

const editingBinNumber = (state = null, { type, binNumber }) => {
  switch (type) {
    case 'WCPM/EDIT_MODE_ON':
      return binNumber
    case 'WCPM/EDIT_MODE_OFF':
      return null
    case 'WCPM/EDIT_MODE_CANCEL':
      return null
    case 'WCPM/EDIT_MODE_SAVE':
      return null
    case 'WCPM/ADD_BIN':
      return binNumber
    case 'WCPM/CONFIRM_REMOVE_BIN':
      return null
    default:
      return state
  }
}

const confirmDelete = (state = false, { type, binNumber }) => {
  switch (type) {
    case 'WCPM/REMOVE_BIN':
      return true
    case 'WCPM/CANCEL_REMOVE_BIN':
      return false
    case 'WCPM/CONFIRM_REMOVE_BIN':
      return null
    case 'WCPM/EDIT_MODE_CANCEL':
      return null
    case 'WCPM/EDIT_MODE_SAVE':
      return null
    default:
      return state
  }
}

const wcpmErrors = (state = fromJS({ general: [] }), { type, scope, message }) => {
  switch (type) {
    case 'WCPM/ERRORS/SET':
      return (scope === 'general')
        ? state.updateIn([scope], (scope) => (scope.push(message)))
        : state.set(scope, message)
    case 'WCPM/ERRORS/CLEAR_COMPONENT':
      return state.delete(scope)
    case 'WCPM/ERRORS/CLEAR':
    case 'WCPM/EDIT_MODE_SAVE':
    case 'WCPM/EDIT_MODE_CANCEL':
      return fromJS({ general: [] })
    default:
      return state
  }
}

export const submarine = sub()
export const getOptions = (state) => submarine(state).get('options').toJS()
export const getBins = (state) => submarine(state).get('bins').toJS()
export const getEditingBinNumber = (state) => submarine(state).get('editingBinNumber')
export const getConfirmDelete = (state) => submarine(state).get('confirmDelete')
export const getWcpmErrors = (state) => submarine(state).get('wcpmErrors').toJS()
export const hasPath = (state, path, binNumber) => {
  const allBins = submarine(state).get('bins')
  const binCheck = (bin) => bin.get('contextPaths').some(path => path === '/')
  if (binNumber) {
    return binCheck(allBins.get(binNumber))
  } else {
    return allBins.some(bin => binCheck(bin))
  }
}

export default combineReducers({ bins, options, editingBinNumber, confirmDelete, wcpmErrors })

/*
// Example State Data Structure
const mockPolicies = [
  {
    realm: 'LDAP',
    authenticationTypes: [
      'authType3',
      'authType4'
    ],
    requiredAttributes: {
        http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role: 'system-admin',
        http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role: 'system-admin'
    }
    contextPaths: [
      '/'
    ]
  }
]

const mockClaims = ({
  realms: [
    'karaf',
    'LDAP',
    'IDP'
  ],
  authenticationTypes: [
    'karaf',
    'basic',
    'GUEST',
    'LDAP',
    'IDP',
    'SAML',
    'PKI'
  ],
  claims: [
    '{http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=system-user}',
    '{http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=system-admin}',
    '{http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role=system-other}'
  ]
})
*/
