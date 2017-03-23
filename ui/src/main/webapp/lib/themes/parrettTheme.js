import darkBaseTheme from 'material-ui/styles/baseThemes/darkBaseTheme'
import { fromJS } from 'immutable'

export default fromJS(darkBaseTheme).mergeDeep({
  textField: {
    errorColor: '#6b2b3d'
  },
  raisedButton: {
    disabledColor: '#444444'
  },
  palette: {
    accent1Color: '#753229',
    accent2Color: '#55351d',
    accent3Color: '#f1dac3',
    disabledColor: '#444444',
    alternateTextColor: '#f7d57d',
    backdropColor: '#333333',
    canvasColor: '#1f1f1f',
    errorColor: '#6b2b3d',
    primary1Color: '#757474',
    successColor: '#244824',
    textColor: '#f7d57d',
    warningColor: '#8b5300'
  }
}).toJS()
