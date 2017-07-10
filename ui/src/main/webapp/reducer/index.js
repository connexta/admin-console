import { combineReducers } from 'redux-immutable'

import client from '../client'

import wizard, { submarine as wizardSubmarine } from 'admin-wizard/reducer'
import wcpm, { submarine as wcpmSubmarine } from '../security/wcpm/reducer'
import sourceWizard, { submarine as sourceSubmarine } from '../wizards/sources/reducer'
import theme, { submarine as themeSubmarine } from 'admin-app-bar/reducer'

const backendError = (state = {}, { type, err } = {}) => {
  switch (type) {
    case 'BACKEND_ERRORS':
      return err
    case 'CLEAR_BACKEND_ERRORS':
      return {}
    default: return state
  }
}
export const getBackendErrors = (state) => state.get('backendError')

export default combineReducers({
  apollo: client.reducer(),
  wizard,
  backendError,
  sourceWizard,
  wcpm,
  theme
})

// Submarines patch redux selectors which causes issues running unit tests.
// The following if protects against the side effect during tests. Do not remove.
if (process.env.NODE_ENV !== 'ci') {
  wizardSubmarine.init((state) => state.get('wizard'))
  wcpmSubmarine.init((state) => state.get('wcpm'))
  sourceSubmarine.init((state) => state.get('sourceWizard'))
  themeSubmarine.init((state) => state.get('theme'))
}
