import { combineReducers } from 'redux-immutable'

import client from '../client'

import wizard from 'admin-wizard/reducer'
import wcpm from '../security/wcpm/reducer'
import theme from 'admin-app-bar/reducer'

export default combineReducers({
  apollo: client.reducer(),
  wizard,
  wcpm,
  theme
})

export const getWizard = (state) => state.get('wizard')
export const getWcpm = (state) => state.get('wcpm')
export const getTheme = (state) => state.get('theme')
