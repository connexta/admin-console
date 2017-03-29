import React from 'react'
import { Link } from 'react-router'
import Flexbox from 'flexbox-react'

import Paper from 'material-ui/Paper'
import muiThemeable from 'material-ui/styles/muiThemeable'

import * as styles from './styles.less'

const TileLinkView = ({ to, title, subtitle, Icon, muiTheme }) => (
  <div>
    <Link to={to}>
      <Paper className={styles.main}>
        <div style={{width: '100%', height: '100%'}}>
          <Flexbox
            alignItems='center'
            flexDirection='column'
            justifyContent='center'
            style={{width: '100%', height: '100%'}}>

            <p className={styles.titleTitle}>{title}</p>
            <Icon style={{color: muiTheme.palette.primary1Color, width: '50%', height: '50%'}} />
            <p className={styles.tileSubtitle}>{subtitle}</p>

          </Flexbox>
        </div>
      </Paper>
    </Link>
  </div>
)

export const TileLink = muiThemeable()(TileLinkView)
