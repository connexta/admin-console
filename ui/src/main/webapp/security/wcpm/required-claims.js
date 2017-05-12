import React from 'react'

import Flexbox from 'flexbox-react'

import { List, ListItem } from 'material-ui/List'

import EntryMapper from 'components/EntryMapper'

import { Subtitle, Description } from './components'

import {
  Table,
  TableBody,
  TableHeader,
  TableHeaderColumn,
  TableRow,
  TableRowColumn
} from 'material-ui/Table'

const Layout = ({ children }) => (
  <div>
    <Flexbox flexDirection='row' flexWrap='wrap' style={{ padding: '15px 5px' }}>
      <Flexbox flex='1' flexDirection='column' style={{ minWidth: 300, marginRight: 20 }}>
        <div>
          <Subtitle>Required Subject Claims</Subtitle>
          <Description>
            The required attributes and values a subject must have to
            access the set of web contexts.
          </Description>
        </div>
      </Flexbox>
    </Flexbox>
    {children}
  </div>
)

export const RequiredClaimsView = ({ claimsMapping }) => (
  <Layout>
    {claimsMapping.length > 0
      ? <Table selectable={false}>
        <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
          <TableRow>
            <TableHeaderColumn>STS Claim</TableHeaderColumn>
            <TableHeaderColumn>Claim Value</TableHeaderColumn>
          </TableRow>
        </TableHeader>
        <TableBody displayRowCheckbox={false}>
          {claimsMapping.map(({ key, value }, i) =>
            <TableRow key={i}>
              <TableRowColumn>{key}</TableRowColumn>
              <TableRowColumn>{value}</TableRowColumn>
            </TableRow>
          )}
        </TableBody>
      </Table> : <List>
        <ListItem disabled primaryText='No required subject claims setup currently.' />
      </List>}
  </Layout>
)

const RequiredClaimsEdit = ({ sts, claimsMapping, onUpdate }) => (
  <Layout>
    <EntryMapper
      keyLabel='STS Claim'
      valueLabel='Claim Value'
      keys={sts.claims}
      mappings={claimsMapping}
      onChange={onUpdate} />
  </Layout>
)

export default RequiredClaimsEdit
