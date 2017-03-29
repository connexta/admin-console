import lightBaseTheme from 'material-ui/styles/baseThemes/lightBaseTheme'
import { fromJS } from 'immutable'

export default fromJS(lightBaseTheme).mergeDeep({
  textField: {
    errorColor: '#F44336'
  },
  raisedButton: {
    disabledColor: '#DDDDDD'
  },
  tableRow: {
    selectedColor: '#DDDDDD'
  },
  palette: {
    errorColor: '#F44336',
    warningColor: '#FF9800',
    successColor: '#4CAF50',
    accent2Color: '#FFFFFF',
    backdropColor: '#EEEEEE',
    disabledColor: '#DDDDDD'
  }
}).toJS()
