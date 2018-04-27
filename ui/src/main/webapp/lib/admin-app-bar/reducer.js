import { fromJS } from 'immutable'

import defaultTheme from 'themes/defaultTheme'
import darkTheme from 'themes/darkTheme'
import adminTheme from 'themes/adminTheme'
import parrettTheme from 'themes/parrettTheme'
import solarizedDarkTheme from 'themes/solarizedDarkTheme'

const presetThemes = {
  'Admin': adminTheme,
  'Material': defaultTheme,
  'Dark': darkTheme,
  'Parrett': parrettTheme,
  'Solarized Dark': solarizedDarkTheme
}

export default (state = fromJS(adminTheme), { type, path, value, themeName }) => {
  switch (type) {
    case 'THEME/SET_COLOR':
      const temp = state.setIn(path, value)
      return temp.setIn(['textField', 'errorColor'], temp.getIn(['palette', 'errorColor']))
        .setIn(['raisedButton', 'disabledColor'], temp.getIn(['palette', 'disabledColor']))
    case 'THEME/SET_PRESET':
      return fromJS(presetThemes[themeName])
    default:
      return state
  }
}

export const getTheme = (state) => state.toJS()
