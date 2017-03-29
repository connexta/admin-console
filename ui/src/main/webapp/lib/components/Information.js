import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

import { informationDiv, informationLabel, informationP, informationListItemP } from './styles.less'

const Information = ({ id, label, value, muiTheme }) => (
  <div className={informationDiv}>
    <label className={informationLabel} style={{ color: muiTheme.palette.textColor }}>{label}</label>
    { value instanceof Array
      ? (
        <div className={informationP}>
          {value.map((text, i) => (
            <p key={i} className={informationListItemP} style={{ color: muiTheme.palette.textColor }}>
              {text}
            </p>))}
        </div>
        )
      : (<p id={id} className={informationP} style={{ color: muiTheme.palette.textColor }}>{value}</p>)
    }
  </div>
)

export default muiThemeable()(Information)
