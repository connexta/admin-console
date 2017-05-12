import React from 'react'

import MenuItem from 'material-ui/MenuItem'
import SelectField from 'material-ui/SelectField'
import { List, ListItem } from 'material-ui/List'

import { Layout } from './components'

const Description = () => (
  <span>
    Realm supporting each context.
  </span>
)

export const RealmsView = ({ realm }) => (
  <Layout subtitle='Realm' description={<Description />}>
    <List>
      <ListItem disabled primaryText={realm} />
    </List>
  </Layout>
)

const RealmsEdit = ({ wcpm, errors, realm, onUpdate }) => (
  <Layout subtitle='Realm' description={<Description />}>
    <SelectField fullWidth errorText={errors} value={realm} onChange={(event, i, value) => onUpdate(value)}>
      {wcpm.realms.map((realm, i) => <MenuItem key={i} value={realm} primaryText={realm} />)}
    </SelectField>
  </Layout>
)

export default RealmsEdit
