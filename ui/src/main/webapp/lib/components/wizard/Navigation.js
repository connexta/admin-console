import React from 'react'

import Flexbox from 'flexbox-react'

import { Link } from 'react-router'

import FlatButton from 'material-ui/FlatButton'
import RaisedButton from 'material-ui/RaisedButton'

import LeftIcon from 'material-ui/svg-icons/hardware/keyboard-arrow-left'
import RightIcon from 'material-ui/svg-icons/hardware/keyboard-arrow-right'
import HomeIcon from 'material-ui/svg-icons/action/home'

export const Home = (props) => (
  <Link to='/'>
    <RaisedButton primary label='Home' labelPosition='before' icon={<HomeIcon />} />
  </Link>
)

export const Begin = ({ name, ...props }) => (
  <RaisedButton primary label={`begin ${name} wizard`} {...props} />
)

export const Back = (props) => (
  <FlatButton primary label='back' labelPosition='after' icon={<LeftIcon />} {...props} />
)

export const Next = (props) => (
  <RaisedButton primary label='next' labelPosition='before' icon={<RightIcon />} {...props} />
)

export const Finish = (props) => (
  <RaisedButton primary label='finish' {...props} />
)

export default ({ children, style = {}, ...rest }) => (
  <Flexbox
    style={{ marginTop: 20, ...style }}
    justifyContent={React.Children.count(children) < 2 ? 'center' : 'space-between'}
    {...rest}>
    {children}
  </Flexbox>
)
