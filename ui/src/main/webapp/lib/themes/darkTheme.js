import darkBaseTheme from 'material-ui/styles/baseThemes/darkBaseTheme'
import { fromJS } from 'immutable'

export default fromJS(darkBaseTheme).mergeDeep({
  textField: {
    errorColor: '#8B2635'
  },
  raisedButton: {
    disabledColor: '#444444'
  },
  palette: {
    textColor: '#EEEEEE',
    alternateTextColor: '#000000',
    primary1Color: '#3E92CC',
    accent1Color: '#B03D67',
    accent2Color: '#2A3943',
    backdropColor: '#444444',
    errorColor: '#8B2635',
    warningColor: '#DC8201',
    successColor: '#58955D',
    canvasColor: '#2A2B2E',
    disabledColor: '#444444'
  }
}).toJS()
