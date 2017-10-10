import { combineReducers } from 'redux-immutable'

import client from '../client'

import wizard, { submarine as wizardSubmarine } from 'admin-wizard/reducer'
import wcpm, { submarine as wcpmSubmarine } from '../security/wcpm/reducer'
import theme, { submarine as themeSubmarine } from 'admin-app-bar/reducer'
import config from '../config/reducer'

export default combineReducers({
  apollo: client.reducer(),
  wizard,
  wcpm,
  theme,
  config
})

// Submarines patch redux selectors which causes issues running unit tests.
// The following if protects against the side effect during tests. Do not remove.
if (process.env.NODE_ENV !== 'ci') {
  wizardSubmarine.init((state) => state.get('wizard'))
  wcpmSubmarine.init((state) => state.get('wcpm'))
  themeSubmarine.init((state) => state.get('theme'))
}
