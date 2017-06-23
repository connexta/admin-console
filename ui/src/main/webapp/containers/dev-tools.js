import React from 'react'

import { createDevTools } from 'redux-devtools'

import LogMonitor from 'redux-devtools-log-monitor'
import DockMonitor from 'redux-devtools-dock-monitor'
import Dispatcher from 'redux-devtools-dispatch'
import MultipleMonitors from 'redux-devtools-multiple-monitors'

const DevTools = createDevTools(
  <DockMonitor
    defaultIsVisible={false}
    toggleVisibilityKey='ctrl-h'
    changePositionKey='ctrl-q'
    defaultSize={0.35}
    defaultPosition='right'>
    <MultipleMonitors>
      <LogMonitor
        theme='solarized'
        expandActionRoot={false}
        expandStateRoot={false}
        select={(state) => state} />
      <Dispatcher theme='solarized' />
    </MultipleMonitors>
  </DockMonitor>
)

export default DevTools
