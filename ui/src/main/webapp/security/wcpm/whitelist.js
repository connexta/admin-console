import React from 'react'

import { fromJS, List as IList } from 'immutable'

import { List, ListItem } from 'material-ui/List'

import { Header, EditRegion, ConfirmationPanel, ServerErrors } from './components'

import Description from 'components/Description'

import EditableList from './editable-list'

const Layout = ({ children }) => (
  <div>
    <div style={{ padding: 8, textAlign: 'center' }}>
      <Header>Whitelisted Contexts</Header>
      <Description>
        Whitelisted context paths are trusted which will bypass security.
        Any sub-contexts of a whitelisted context path will be whitelisted
        as well, unless they are specifically assigned a policy.
      </Description>
    </div>
    {children}
  </div>
)

const WhiteListView = ({ whitelisted, onEdit }) => (
  <EditRegion onEdit={() => onEdit(fromJS(whitelisted))}>
    <Layout>
      <List>
        {whitelisted.map((path, i) => <ListItem disabled key={i} primaryText={path} />)}
      </List>
    </Layout>
  </EditRegion>
)

export const WhitelistEdit = ({ whitelisted = [], errors, serverErrors = {}, onUpdate, onCancel, onSave }) => (
  <Layout>
    <div style={{ padding: 8 }}>
      <EditableList
        hintText='Add New Path'
        list={whitelisted}
        errors={errors}
        onChange={({ value, index }) => onUpdate([index], value === '' ? undefined : value)} />
      <ConfirmationPanel onCancel={onCancel} onSave={() => onSave(whitelisted)} />
      <ServerErrors errors={serverErrors.all} />
    </div>
  </Layout>
)

const isValidContextPath = (path) => {
  return /^(\/[A-Za-z0-9-._~:/?#[\]@!$&'()*+,;=`.%]*)+$/g.test(path)
}

const hasTrailingSlash = (path) => {
  return /.+\/$/.test(path)
}

export const validator = (whitelist, { policies = [] } = {}) => {
  let errors = IList()

  whitelist.forEach((path, i) => {
    if (!isValidContextPath(path)) {
      errors = errors.set(i, 'Invalid context path')
    } else if (hasTrailingSlash(path)) {
      errors = errors.set(i, 'No trailing slashes allowed')
    } else if (whitelist.slice(0, i).includes(path)) {
      errors = errors.set(i, 'Path already included in the whitelist')
    } else {
      const found = policies.findIndex(({ paths }) => paths.includes(path))
      if (found > -1) {
        errors = errors.set(i, `Path included in policy #${found + 1}`)
      }
    }
  })

  return errors
}

export default WhiteListView

