import React from 'react'

import Flexbox from 'flexbox-react'
import { Link } from 'react-router'

import Paper from 'material-ui/Paper'
import AccountIcon from 'material-ui/svg-icons/action/account-circle'
import LanguageIcon from 'material-ui/svg-icons/action/language'
import VpnLockIcon from 'material-ui/svg-icons/notification/vpn-lock'
import BugReport from 'material-ui/svg-icons/action/bug-report'
import muiThemeable from 'material-ui/styles/muiThemeable'

import { expandingTile } from './styles.css'

let TileLink = ({ to, title, subtitle, Icon, muiTheme }) => (
  <div>
    <Link to={to}>
      <Paper className={expandingTile}>
        <div style={{width: '100%', height: '100%'}}>
          <Flexbox
            alignItems='center'
            flexDirection='column'
            justifyContent='center'
            style={{width: '100%', height: '100%'}}>

            <p style={{
              fontSize: '18px',
              position: 'relative',
              textAlign: 'center',
              margin: '5px 0px'
            }}>
              {title}
            </p>
            {React.createElement(Icon, { style: { color: muiTheme.palette.primary1Color, width: '50%', height: '50%' } })}
            <p style={{
              fontSize: '14px',
              position: 'relative',
              textAlign: 'center',
              margin: '10px 30px'
            }}>
              {subtitle}
            </p>

          </Flexbox>
        </div>
      </Paper>
    </Link>
  </div>
)
TileLink = muiThemeable()(TileLink)

let Title = ({ children, muiTheme }) => (
  <h1 style={{ color: muiTheme.palette.textColor }}>{children}</h1>
)
Title = muiThemeable()(Title)

const SourcesHomeView = () => (
  <div style={{width: '100%'}}>
    <Title>
      Setup Wizards
    </Title>

    <Flexbox flexDirection='row' flexWrap='wrap' style={{width: '100%'}}>
      <TileLink
        to='/sources'
        title='Source Setup Wizard'
        subtitle='Setup a new source for federating'
        Icon={LanguageIcon} />

      <TileLink
        to='/web-context-policy-manager'
        title='Endpoint Security'
        subtitle='Web context policy management'
        Icon={VpnLockIcon} />

      <TileLink
        to='/ldap'
        title='LDAP Setup Wizard'
        subtitle='Configure LDAP as a login'
        Icon={AccountIcon} />

      <TileLink
          to='/dev'
          title='Developer Tools'
          subtitle='Tools 4 u and me'
          Icon={BugReport} />
    </Flexbox>
  </div>
)

export const Home = SourcesHomeView

