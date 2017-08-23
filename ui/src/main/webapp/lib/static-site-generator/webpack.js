import React from 'react'

import { renderToStaticMarkup } from 'react-dom/server'

import Html from './html'

export default ({ webpack }) => '<!DOCTYPE html>' +
  renderToStaticMarkup(<Html assets={webpack.assets.map(({ name }) => name)} />)
