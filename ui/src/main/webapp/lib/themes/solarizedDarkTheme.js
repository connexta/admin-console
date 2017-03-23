import darkBaseTheme from 'material-ui/styles/baseThemes/darkBaseTheme'
import { fromJS } from 'immutable'

export default fromJS(darkBaseTheme).mergeDeep({
  palette: {
    accent1Color: '#D33682',
    accent2Color: '#073642',
    accent3Color: '#CB4B16',
    alternateTextColor: '#FDF6E3',
    backdropColor: '#073642',
    canvasColor: '#002B36',
    errorColor: '#DC322F',
    primary1Color: '#268bd2',
    successColor: '#859900',
    textColor: '#93A1A1',
    warningColor: '#B58900',
    disabledColor: '#586e75',
    borderColor: '#073642'
  },
  textField: {
    errorColor: '#6b2b3d'
  },
  raisedButton: {
    disabledColor: '#586e75'
  }
}).toJS()
