import React, { Component } from 'react'

import Flexbox from 'flexbox-react'
import { List as IL } from 'immutable'

import TextField from 'material-ui/TextField'
import { List } from 'material-ui/List'

import { RemoveButton } from './components'

class EditableList extends Component {
  render () {
    const {
      list,         // Regular js list or immutable
      onChange,     // change handler
      errors = [],  // Regular js list or immutable
      hintText = 'Add a new item'
    } = this.props

    // immutable.js compatability
    const len = IL.isList(list) ? list.size : list.length
    const err = (i) => IL.isList(errors) ? errors.get(i) : errors[i]

    return (
      <List>
        {list.map((value, index) =>
          <Flexbox key={index} flexDirection='row' alignItems='center' justifyContent='space-between'>
            <Flexbox flex='1'>
              <TextField
                id={`index-${index}`}
                fullWidth
                onKeyPress={(e) => {
                  if (e.key === 'Enter') {
                    // need to setTimeout for ie10
                    // http://stackoverflow.com/questions/19489913/focus-doesnt-working-in-ie
                    setTimeout(() => this.newInput.focus(), 0)
                  }
                }}
                errorText={err(index)}
                value={value}
                onChange={(e) => {
                  const value = e.target.value
                  if (value === '') {
                    setTimeout(() => this.newInput.focus(), 0)
                  }
                  onChange({ value, index })
                }} />
            </Flexbox>
            <RemoveButton onRemove={() => onChange({ index })} />
          </Flexbox>
        ).concat(
          // need to maintain parallel structure to items above so when
          // the new list item gets appended to the dom, the input doesn't
          // lose focus
          <Flexbox key={len} flexDirection='row' alignItems='center' justifyContent='space-between'>
            <Flexbox flex='1'>
              <TextField
                id='new-item'
                hintText={hintText}
                ref={(input) => { this.newInput = input }}
                fullWidth
                value='' // ensure the component stays controlled
                errorText={err(len)}
                onChange={(e) => onChange({ value: e.target.value, index: len })} />
            </Flexbox>
          </Flexbox>
        )}
      </List>
    )
  }
}

export default EditableList
