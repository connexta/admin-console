import { Component } from 'react'

export default class extends Component {
  componentWillMount () {
    const { on, ...rest } = this.props
    delete rest.off // delete potentially passed prop
    if (typeof on === 'function') {
      on(rest)
    }
  }
  componentWillUnmount () {
    if (typeof this.props.off === 'function') {
      this.props.off()
    }
  }
  render () {
    if (this.props.children !== undefined) {
      return this.props.children
    }

    return null
  }
}
