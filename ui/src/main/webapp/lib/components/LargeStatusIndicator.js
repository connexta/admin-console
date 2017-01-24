import CheckIcon from 'material-ui/svg-icons/action/check-circle'
import CloseIcon from 'material-ui/svg-icons/navigation/cancel'

import {green500, red500} from 'material-ui/styles/colors'
import { center } from './styles.less'

const statusIndicator = ({
  width: '300px',
  height: '300px'
})

export default ({success}) => (
  <div className={center}>
    {(success)
      ? <CheckIcon style={statusIndicator} color={green500} />
      : <CloseIcon style={statusIndicator} color={red500} />
    }
  </div>
)
