import { combineReducers } from 'redux-immutable'

import home, { submarine as homeSubmarine } from '../home/reducer'
import systemUsage, { submarine as systemUsageSubmarine } from 'system-usage/reducer'
import polling, { submarine as pollingSubmarine } from 'redux-polling'
import fetch, { submarine as fetchSubmarine } from 'redux-fetch'
import wizard, { submarine as wizardSubmarine } from 'admin-wizard/reducer'
import wcpm, { submarine as wcpmSubmarine } from '../adminTools/webContextPolicyManager/reducer'
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

export default combineReducers({ fetch, wizard, backendError, sourceWizard, home, wcpm, polling, systemUsage, theme })

// Submarines patch redux selectors which causes issues running unit tests.
// The following if protects against the side effect during tests. Do not remove.
if (process.env.NODE_ENV !== 'ci') {
  systemUsageSubmarine.init((state) => state.get('systemUsage'))
  pollingSubmarine.init((state) => state.get('polling'))
  homeSubmarine.init((state) => state.get('home'))
  fetchSubmarine.init((state) => state.get('fetch'))
  wizardSubmarine.init((state) => state.get('wizard'))
  wcpmSubmarine.init((state) => state.get('wcpm'))
  sourceSubmarine.init((state) => state.get('sourceWizard'))
  themeSubmarine.init((state) => state.get('theme'))
}
