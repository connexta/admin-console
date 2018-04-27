import React, { Component } from 'react'
import { Map, Set } from 'immutable'

import muiThemeable from 'material-ui/styles/muiThemeable'

import {
  Card,
  CardTitle,
  CardText
} from 'material-ui/Card'

import {
  Toolbar,
  ToolbarGroup,
  ToolbarTitle
} from 'material-ui/Toolbar'

import AppBar from 'material-ui/AppBar'
import Drawer from 'material-ui/Drawer'
import MenuItem from 'material-ui/MenuItem'
import Paper from 'material-ui/Paper'
import Toggle from 'material-ui/Toggle'

import Pass from 'material-ui/svg-icons/action/check-circle'
import Fail from 'material-ui/svg-icons/navigation/cancel'
import Timer from 'material-ui/svg-icons/image/timer'
import CircularProgress from 'material-ui/CircularProgress'
import LinearProgress from 'material-ui/LinearProgress'

import Divider from 'material-ui/Divider'

import Flexbox from 'flexbox-react'

const stats = (tests, total) => tests.reduce((stats, test) => {
  switch (test.get('state')) {
    case 'pass':
      return stats.update('pass', 0, (n) => n + 1)
    case 'fail':
      return stats.update('fail', 0, (n) => n + 1)
    case 'pending':
      return stats.update('pending', 0, (n) => n + 1)
    default:
      return stats
  }
}, Map()).set('progress', tests.size / total * 100 | 0)

const escapeRe = (s) => s.replace(/[-/\\^$*+?.()|[\]{}]/g, '\\$&')

const Result = muiThemeable()(
  ({ test, muiTheme: { palette } }) => {
    const state = test.get('state')
    const error = test.get('error')

    const color = state === 'pass'
      ? palette.primary1Color
      : state === 'fail'
      ? palette.accent1Color
      : palette.primary3Color

    return (
      <CardText style={{ borderLeft: `3px solid ${color}` }}>
        <Flexbox justifyContent='space-between' alignItems='center'>
          <Flexbox alignItems='center'>
            {state === 'pass'
              ? <Pass color={color} />
              : state === 'fail'
              ? <Fail color={color} />
              : <CircularProgress size={20} color={color} />}
            <div style={{ margin: '0 5px' }}>{test.get('title')}</div>
          </Flexbox>
          <Flexbox alignItems='center'>
            <span style={{ margin: '0 5px' }}>{test.get('duration')} ms</span> <Timer />
          </Flexbox>
        </Flexbox>
        {state === 'fail'
          ? <div>
            <pre style={{ maxWidth: '100%', overflowX: 'scroll' }}>{error.stack}</pre>
          </div>
          : null}
      </CardText>
    )
  }
)

const Background = muiThemeable()(
  ({ children, muiTheme: { palette } }) => (
    <div style={{
      background: palette.borderColor,
      minHeight: '100vh'
    }}>
      {children}
    </div>
  )
)

const RenderContext = muiThemeable()(
  ({ muiTheme: { palette } }) => (
    <div style={{ padding: '20px 0' }}>
      <Paper>
        <Toolbar>
          <ToolbarGroup>
            <ToolbarTitle text='Render Context' />
          </ToolbarGroup>
        </Toolbar>
        <div style={{ padding: 1 }} id='here' />
      </Paper>
    </div>
  )
)

class Reporter extends Component {
  constructor (props) {
    super(props)
    this.state = {
      open: false,
      show: Set(['pass', 'fail', 'pending', 'skip'])
    }
  }
  render () {
    const { tests, total } = this.props
    const { open, show } = this.state

    const s = stats(tests, total)

    const suites = tests
      .filter((test) => show.has(test.get('state')))
      .groupBy((test) => test.get('path'))

    return (
      <Background>
        <AppBar
          title='Mocha HTML Reporter'
          onLeftIconButtonTouchTap={() => this.setState({ open: true })} />
        <LinearProgress style={{ borderRadius: 0 }} mode='determinate' value={s.get('progress')} />
        <Drawer
          docked={false}
          width={200}
          open={open}
          onRequestChange={(open) => this.setState({ open })}>
          <MenuItem>
            <Toggle
              style={{ display: 'inline-block' }}
              label='Show Passed'
              toggled={show.has('pass')}
              onToggle={(_, isInputChecked) => this.setState({
                show: isInputChecked ? show.add('pass') : show.remove('pass')
              })} />
          </MenuItem>
          <MenuItem>
            <Toggle
              style={{ display: 'inline-block' }}
              label='Show Failed'
              toggled={show.has('fail')}
              onToggle={(_, isInputChecked) => this.setState({
                show: isInputChecked ? show.add('fail') : show.remove('fail')
              })} />
          </MenuItem>
          <MenuItem>
            <Toggle
              style={{ display: 'inline-block' }}
              label='Show Pending'
              toggled={show.has('pending')}
              onToggle={(_, isInputChecked) => this.setState({
                show: isInputChecked ? show.add('pending') : show.remove('pending')
              })} />
          </MenuItem>
          <MenuItem>
            <Toggle
              style={{ display: 'inline-block' }}
              label='Show Skipped'
              toggled={show.has('skip')}
              onToggle={(_, isInputChecked) => this.setState({
                show: isInputChecked ? show.add('skip') : show.remove('skip')
              })} />
          </MenuItem>
        </Drawer>
        <style>{`
          #mocha { margin: 0 }
        `}</style>
        <div style={{ width: 960, margin: '0 auto', padding: '20px 0' }}>
          <pre style={{ margin: 0 }}>{JSON.stringify(s)}</pre>
          <RenderContext />
          {suites.entrySeq().map(([path, suite], key) => {
            const fragments = path.filter((s) => s !== '')
            const href = `?grep=${encodeURIComponent(escapeRe(fragments.join(' ')))}`
            const file = fragments.get(0)
            const title = fragments.slice(1).join(' - ')
            return (
              <Card key={key} style={{ marginTop: key === 0 ? 0 : 34 }}>
                <CardTitle
                  title={<a style={{textDecoration: 'none', color: 'inherit'}} href={href}>{title}</a>}
                  subtitle={file}
                />
                {suite.map((test, i) =>
                  <div key={i}>
                    <Divider />
                    <Result test={test} />
                  </div>
                )}
              </Card>
            )
          })}
        </div>
      </Background>
    )
  }
}

export default Reporter
