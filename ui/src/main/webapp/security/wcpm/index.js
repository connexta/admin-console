import React from 'react'
import { connect } from 'react-redux'
import { compose, gql, graphql } from 'react-apollo'

import visible from 'react-visible'

import { fromJS } from 'immutable'

import Snackbar from 'material-ui/Snackbar'

import {
  Panel,
  H1,
  Disabled
} from './components'

import Description from 'components/Description'

import {
  isEditing,
  edit,
  cancel,
  update,
  submit,
  error,
  dismiss,
  getValue,
  editingToken,
  hasSubmitted,
  getNotification,
  getErrors
} from './reducer'

const NotifyErrors = connect(
  (state) => ({ message: getNotification(state) }),
  { onClose: dismiss }
)(
  ({ message, onClose }) =>
    <Snackbar
      open={message !== null}
      message={message || ''}
      autoHideDuration={5000}
      onRequestClose={onClose} />
)

import Whitelist, { WhitelistEdit, validator as whitelistValidator } from './whitelist'

import Policy, { PolicyEdit, NewPolicy, validator as policyValidator } from './policy'

const connector = (key, validator) => {
  return connect(
    (state, { policies, whitelisted }) => {
      const value = getValue(state)
      return {
        [key]: value,
        serverErrors: getErrors(state),
        errors: hasSubmitted(state)
          ? validator(value, { policies, whitelisted }).toJS() : undefined
      }
    },
    { onUpdate: update, onCancel: cancel }
  )
}

const ConnectedWhitelistEdit = connector('whitelisted', whitelistValidator)(WhitelistEdit)
const ConnectedPolicyEdit = connector('policy', policyValidator)(PolicyEdit)

const insert = (value, index, array) => array.slice(0, index).concat(value, array.slice(index + 1))

let WebContextPolicyManager = (props) => {
  const {
    data,

    whitelisted = [],
    policies = [],

    editing,
    index,

    onEdit,
    onDelete,
    onSavePolicy,
    onSaveWhitelist
  } = props

  const mergedPolicies = (editing && index > -1) ? insert({ editing: true }, index, policies) : policies

  const ps = mergedPolicies.map((policy, i) => {
    if (editing && policy.editing) {
      return (
        <ConnectedPolicyEdit
          data={data}
          number={i + 1}
          policies={mergedPolicies}
          whitelisted={whitelisted}
          onSave={(policy) => onSavePolicy(policy, i)}
          onDelete={() => onDelete(i)} />
      )
    } else {
      return (
        <Policy
          number={i + 1}
          policy={policy}
          onEdit={(value) => onEdit(i, value)} />
      )
    }
  })

  const applyDisabled = (el, i) =>
    (!editing || index === (i - 1)) ? el : <Disabled>{el}</Disabled>
  const applyPanel = (el, i) => <Panel>{el}</Panel>
  const applyKey = (el, i) => <div key={i}>{el}</div>

  const els = [
    !(editing && index === -1)
    ? <Whitelist onEdit={(value) => onEdit(-1, value)} whitelisted={whitelisted} />
    : <ConnectedWhitelistEdit policies={mergedPolicies} onSave={onSaveWhitelist} />
  ].concat(ps)
    .map(applyPanel)
    .map(applyDisabled)
    .map(applyKey)

  return (
    <div>
      {els}
      <NewPolicy disabled={editing} onCreate={(value) => onEdit(mergedPolicies.length, value)} />
      <NotifyErrors />
    </div>
  )
}

WebContextPolicyManager = connect(
  (state) => ({
    editing: isEditing(state),
    index: editingToken(state)
  }),
  (dispatch, { policies = [], whitelisted, onSavePolicy, onSaveWhitelist }) => ({
    onEdit: (...args) => dispatch(edit(...args)),
    onDelete: (index) => {
      onSavePolicy(policies.filter((_, i) => i !== index))
      dispatch(cancel())
    },
    onSaveWhitelist: (whitelist) => {
      dispatch(submit())
      if (whitelistValidator(whitelist, { policies }).isEmpty()) {
        onSaveWhitelist(whitelist)
      } else {
        dispatch(error('Cannot save because of validation issues'))
      }
    },
    onSavePolicy: (policy, index) => {
      dispatch(submit())
      if (policyValidator(policy, { whitelisted, policies: insert([], index, policies) }).isEmpty()) {
        onSavePolicy(insert(policy.toJS(), index, policies))
      } else {
        dispatch(error('Cannot save because of validation issues'))
      }
    }
  })
)(WebContextPolicyManager)

WebContextPolicyManager = visible(WebContextPolicyManager)

WebContextPolicyManager.fragments = {
  wcpm: gql`
    fragment wcpm on WebContextPolicyManager {
      whitelisted
      policies { ...policy }
    }
    ${Policy.fragments.policy}
  `
}

let Root = ({ data: { loading, graphQLErrors, ...fields }, ...props }) => (
  <div>
    <div style={{ padding: 20 }}>
      <H1 style={{textAlign: 'center'}}>Web Context Policy Manager</H1>
      <Description>
        The Web Context Policy Manager defines security policies for all
        subpaths of this web server.  It defines the realms a path should be
        authenticated against, the type of authentication that a path requires,
        and any user attributes that are required for authorization.
      </Description>

      <Description>
        Any subpaths of a configured path will inherit its parent's
        policy. For example, in a system where a policy is configured for '/a',
        its policy applies to '/a/b' and '/a/b/c' unless otherwise specified.
      </Description>
    </div>
    <WebContextPolicyManager visible={!loading} {...props} {...fields.wcpm} data={fields} />
  </div>
)

const Query = gql`
  query Query {
    wcpm { ... wcpm }
    ...policyEdit
  }
  ${WebContextPolicyManager.fragments.wcpm}
  ${PolicyEdit.fragments.policyEdit}
`

const saveContextPolicies = gql`
  mutation Mutation($policies: [ContextPolicyBin!]!) {
    saveContextPolicies(policies: $policies) { ...policy }
  }
  ${Policy.fragments.policy}
`

const saveWhitelistContexts = gql`
  mutation Mutation($whitelisted: [ContextPath]) {
    saveWhitelistContexts(paths: $whitelisted)
  }
`

const queries = compose(
  graphql(saveContextPolicies, {
    props: ({ mutate, ownProps: { onCancel, onError } }) => ({
      onSavePolicy: (policies) => mutate({
        variables: { policies },
        updateQueries: {
          Query: (prev, { mutationResult }) => {
            const policies = mutationResult.data.saveContextPolicies
            return fromJS(prev).setIn(['wcpm', 'policies'], policies).toJS()
          }
        }
      })
      .then(onCancel)
      .catch((err) =>
        onError('An error occured while trying to save the policy', err.graphQLErrors))
    })
  }),
  graphql(saveWhitelistContexts, {
    props: ({ mutate, ownProps: { onCancel, onError } }) => ({
      onSaveWhitelist: (whitelisted) => mutate({
        variables: { whitelisted },
        updateQueries: {
          Query: (prev, { mutationResult }) => {
            const whitelisted = mutationResult.data.saveWhitelistContexts
            return fromJS(prev).setIn(['wcpm', 'whitelisted'], whitelisted).toJS()
          }
        }
      })
      .then(onCancel)
      .catch((err) =>
        onError('An error occured while trying to save the whitelist', err.graphQLErrors))
    })
  }),
  graphql(Query)
)

export default connect(undefined,
  { onCancel: cancel, onError: error }
)(queries(Root))
