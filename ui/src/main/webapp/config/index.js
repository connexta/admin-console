import React from 'react'

import { connect } from 'react-redux'

import { getAllFeature } from './reducer'

const Config = ({ config, onLoad }) => (
  <div>
    <h1>hello from config</h1>
    <button onClick={onLoad}>load</button>
    <pre>{JSON.stringify(config, null, 2)}</pre>
  </div>
)

const mapStateToProps = (state) => ({
  config: state.get('config')
})

export default connect(
  mapStateToProps,
  {
    onLoad: getAllFeature
  }
)(Config)
