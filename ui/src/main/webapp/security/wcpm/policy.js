import React from 'react'

import { gql } from 'react-apollo'

import ContentAdd from 'material-ui/svg-icons/content/add'
import FloatingActionButton from 'material-ui/FloatingActionButton'
import Flexbox from 'flexbox-react'

import { Record, Map, List } from 'immutable'

import { ConfirmationPanel, EditRegion, Header, ServerErrors, VisibleDeleteButton } from './components'

import Divider from 'material-ui/Divider'

import Spinner from 'components/Spinner'

import ContextPaths, { ContentPathsView } from './context-paths'
import AuthTypes, { AuthTypesView } from './auth-types'
import Realms, { RealmsView } from './realms'
import RequiredClaims, { RequiredClaimsView } from './required-claims'

const Layout = ({ children, number }) => (
  <div style={{ position: 'relative' }}>
    <div style={{ padding: 8 }}>
      <Header>Policy #{number}</Header>
    </div>
    {children}
  </div>
)

const PolicyEdit = (props) => {
  const {
    data: {
      loading,
      sts = { claims: [] },
      wcpm = { realms: [], authTypes: [] }
    },

    policy,
    errors = {},
    serverErrors = {},
    number,

    onUpdate,
    onCancel,
    onSave,
    onDelete
  } = props

  return (
    <Layout number={number}>
      <VisibleDeleteButton style={{ position: 'absolute', right: '0px', top: '0px' }} onClick={onDelete} />
      <Spinner submitting={loading}>
        <ContextPaths
          paths={policy.paths}
          errors={errors.paths}
          onUpdate={(value, path = []) => onUpdate(['paths'].concat(path), value)} />
        <ServerErrors errors={serverErrors.paths} />
        <Divider />
        <AuthTypes
          wcpm={wcpm}
          authTypes={policy.authTypes}
          errors={errors.authTypes}
          onUpdate={(value, path = []) => onUpdate(['authTypes'].concat(path), value)} />
        <ServerErrors errors={serverErrors.authTypes} />
        <Divider />
        <Realms
          wcpm={wcpm}
          realm={policy.realm}
          errors={errors.realm}
          onUpdate={(value, path = []) => onUpdate(['realm'].concat(path), value)} />
        <ServerErrors errors={serverErrors.realm} />
        <Divider />
        <RequiredClaims
          sts={sts}
          claimsMapping={policy.claimsMapping}
          onUpdate={(value, path = []) => onUpdate(['claimsMapping'].concat(path), value)} />
        <ServerErrors errors={serverErrors.claimsMapping} />
        <ConfirmationPanel
          onCancel={onCancel}
          onSave={() => { onSave(policy) }} />
        <ServerErrors errors={serverErrors.all} />
      </Spinner>
    </Layout>
  )
}

PolicyEdit.fragments = {
  policyEdit: gql`
    fragment policyEdit on Query {
      sts { claims }
      wcpm {
        realms
        authTypes
      }
    }
  `
}

const PolicyView = ({ onEdit, policy, number }) => (
  <EditRegion onEdit={() => onEdit(createPolicy(policy))} >
    <Layout number={number}>
      <ContentPathsView paths={policy.paths} />
      <Divider />
      <AuthTypesView authTypes={policy.authTypes} />
      <Divider />
      <RealmsView realm={policy.realm} />
      <Divider />
      <RequiredClaimsView claimsMapping={policy.claimsMapping} />
    </Layout>
  </EditRegion>
)

PolicyView.fragments = {
  policy: gql`
    fragment policy on ContextPolicyBinPayload {
      paths
      authTypes
      realm
      claimsMapping {
        key
        value
      }
    }
  `
}

export { PolicyEdit }
export default PolicyView

const Policy = Record({
  realm: '',
  paths: List(),
  authTypes: List(),
  claimsMapping: List()
})

export const createPolicy = (policy = {}) => {
  return new Policy({
    realm: policy.realm,
    paths: List(policy.paths),
    authTypes: List(policy.authTypes),
    claimsMapping: List(policy.claimsMapping)
  })
}

// checks for a preceding "/" and that there are only URI-valid characters
// ex PASS: '/', '/abc/def', '/abc123#$!'
// ex FAIL: '', 'abc/', '/^^^'
const isValidContextPath = (path) => {
  return /^(\/[A-Za-z0-9-._~:/?#[\]@!$&'()*+,;=`.%]*)+$/g.test(path)
}

const hasTrailingSlash = (path) => {
  return /.+\/$/.test(path)
}

export const validator = (policy, { whitelisted = [], policies = [] } = {}) => {
  let errors = Map()

  if (policy.paths.size === 0) {
    errors = errors.setIn(['paths', 0], 'Must specify at least one context path')
  } else {
    policy.paths.forEach((path, i) => {
      if (!isValidContextPath(path)) {
        errors = errors.setIn(['paths', i], 'Invalid context path')
      } else if (hasTrailingSlash(path)) {
        errors = errors.setIn(['paths', i], 'No trailing slashes allowed')
      } else if (policy.paths.slice(0, i).includes(path)) {
        errors = errors.setIn(['paths', i], 'Path already included in current policy')
      } else if (whitelisted.includes(path)) {
        errors = errors.setIn(['paths', i], 'Path already included in the whitelist')
      } else {
        const found = policies.findIndex(({ paths = [] }) => paths.includes(path))
        if (found > -1) {
          errors = errors.setIn(['paths', i], `Path included in policy #${found + 1}`)
        }
      }
    })
  }

  if (policy.authTypes.size === 0) {
    errors = errors.set('authTypes', 'Must specify at least one auth type')
  }

  if (policy.realm === undefined || policy.realm === '') {
    errors = errors.set('realm', 'Must specify the realm')
  }

  return errors
}

export const NewPolicy = ({ disabled, onCreate }) => (
  <Flexbox style={{ paddingBottom: 20 }} justifyContent='center'>
    <FloatingActionButton disabled={disabled} onClick={() => onCreate(createPolicy())}>
      <ContentAdd />
    </FloatingActionButton>
  </Flexbox>
)
