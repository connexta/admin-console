import React from 'react'

import { List, ListItem } from 'material-ui/List'

import { Layout } from './components'
import EditableList from './editable-list'

const Description = () => (
  <span>
    A context path is the prefix of the URL paths to which these policies
    apply.
  </span>
)

export const ContentPathsView = ({ paths }) => (
  <Layout subtitle='Context Paths' description={<Description />}>
    <List>
      {paths.map((path, i) => <ListItem disabled key={i} primaryText={path} />)}
    </List>
  </Layout>
)

const ContextPathsEdit = ({ paths, errors = [], onUpdate }) => (
  <Layout subtitle='Context Paths' description={<Description />}>
    <EditableList
      hintText='Add New Path'
      list={paths}
      errors={errors}
      onChange={({ value, index }) => onUpdate(value === '' ? undefined : value, index)} />
  </Layout>
)

export default ContextPathsEdit
