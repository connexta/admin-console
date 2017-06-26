import React from 'react'

import { SortableContainer, SortableHandle, SortableElement } from 'react-sortable-hoc'

import MenuItem from 'material-ui/MenuItem'
import SelectField from 'material-ui/SelectField'
import { List, ListItem } from 'material-ui/List'

import IconButton from 'material-ui/IconButton'
import DragIcon from 'material-ui/svg-icons/editor/drag-handle'

import { EditItem, RemoveButton, Layout } from './components'

const DragHandle = SortableHandle(() => <IconButton><DragIcon /></IconButton>)

const SortableItem = SortableElement(({ value, i, checked, onRemove }) =>
  <EditItem
    style={{ cursor: 'pointer' }}
    rightIcon={<RemoveButton onRemove={onRemove} />}
    leftIcon={<DragHandle />}>
    {i + 1}. {value}
  </EditItem>
)

const SortableList = SortableContainer(({ values = [], items = [], onChange }) =>
  <List
    value={values}
    onChange={(_, __, values) => onChange(values)}>
    {values.map((value, i) =>
      <SortableItem key={i} index={i} i={i} value={value} onRemove={() => onChange(undefined, i)} />)}
  </List>
)

const Description = () => (
  <span>
    A list of authentication types that are applied in the specified
    order.
  </span>
)

export const AuthTypesView = ({ authTypes }) => (
  <Layout subtitle='Authentication Types' description={<Description />}>
    <List>
      {authTypes.map((type, i) => <ListItem disabled key={i} primaryText={type} />)}
    </List>
  </Layout>
)

const AuthTypes = ({ wcpm, authTypes, errors, onUpdate }) => {
  const remainingTypes = wcpm.authTypes.filter(x => authTypes.indexOf(x) === -1)
  return (
    <Layout subtitle='Authentication Types' description={<Description />}>
      <SortableList
        lockAxis='y'
        values={authTypes}
        items={wcpm.authTypes}
        onChange={onUpdate}
        onSortEnd={({oldIndex, newIndex}) => {
          const temp = authTypes.get(oldIndex)
          onUpdate(authTypes.delete(oldIndex).insert(newIndex, temp))
        }}
      />
      <EditItem>
        <SelectField
          errorText={errors}
          fullWidth
          disabled={remainingTypes.length === 0}
          hintText={remainingTypes.length === 0 ? 'No More Auth Types' : 'Add Auth Type'}
          onChange={(e, i, value) => onUpdate(value, authTypes.size)}>
          {remainingTypes.map((value, i) =>
            <MenuItem
              key={i}
              value={value}
              primaryText={value}
            />)}
        </SelectField>
      </EditItem>
    </Layout>
  )
}

export default AuthTypes
