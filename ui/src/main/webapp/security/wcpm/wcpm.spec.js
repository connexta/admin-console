import React from 'react'
import { createStore } from 'redux'
import { Provider } from 'react-redux'
import { ApolloProvider, ApolloClient } from 'react-apollo'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'

import { expect } from 'chai'
import { mount } from 'enzyme'

import Policy, { PolicyEdit } from './policy'
import Whitelist, { WhitelistEdit } from './whitelist'

import {
  EditRegion,
  CancelButton,
  DeleteButton,
  Disabled
} from './components'

import reducer from './reducer'
import WebContextPolicyManager from './'

const createClient = (handler) => {
  const query = (request) => new Promise((resolve, reject) => {
    handler(request, resolve, reject)
  })

  return new ApolloClient({ networkInterface: { query } })
}

describe('<WebContextPolicyManager />', () => {
  let wrapper

  const policy = {
    paths: [],
    authTypes: [],
    claimsMapping: [],
    __typename: 'ContextPolicyBinPayload'
  }

  const policies = [ policy, policy, policy ]
  const whitelisted = [ '/a', '/b', '/c' ]

  const store = createStore((state, action) => {
    if (action.type === 'RESET') {
      return action.state
    }

    return reducer(state, action)
  })

  const stack = []

  const save = () => {
    stack.push(store.getState())
  }

  const restore = () => {
    if (stack.length === 0) {
      throw Error('cannot restore to unknown previous state')
    }
    store.dispatch({ type: 'RESET', state: stack.pop() })
  }

  const client = createClient((request, resolve) => {
    resolve({
      data: {
        wcpm: {
          policies,
          whitelisted,
          authTypes: ['guest'],
          __typename: 'WebContextPolicyManager'
        },
        sts: {
          claims: ['a', 'b', 'c'],
          __typename: 'SecurityTokenService'
        },
        __typename: 'Query'
      }
    })
  })

  it('should query for whitelisted and policies', (done) => {
    wrapper = mount(
      <Provider store={store}>
        <ApolloProvider client={client}>
          <MuiThemeProvider>
            <WebContextPolicyManager />
          </MuiThemeProvider>
        </ApolloProvider>
      </Provider>
    )
    setTimeout(done, 0)
  })

  it('should render queries and whitelist correctly', () => {
    expect(wrapper.find(Whitelist)).to.have.length(1)
    expect(wrapper.find(Policy)).to.have.length(policies.length)
  })

  describe('<Whitelist />', () => {
    before(save)
    after(restore)

    it('should let user edit whitelist', () => {
      const button = wrapper.find(Whitelist).find(EditRegion).find('button')
      button.simulate('click')
      expect(wrapper.find(WhitelistEdit)).to.have.length(1)
    })

    it('should disable all other bin', () => {
      expect(wrapper.find(Disabled)).to.have.length(policies.length)
    })

    describe('<WhitelistEdit />', () => {
      // make each test independent by restoring state after each test
      beforeEach(save)
      afterEach(restore)

      it('should let user cancel editing the whitelist', () => {
        const cancel = wrapper.find(WhitelistEdit).find(CancelButton)
        cancel.simulate('click')
        expect(wrapper.find(WhitelistEdit)).to.have.length(0)
      })

      it('should not let user delete the whitelist', () => {
        const del = wrapper.find(WhitelistEdit).find(DeleteButton)
        expect(del).to.have.length(0)
      })
    })
  })

  describe('<Policy />', () => {
    before(save)
    after(restore)

    it('should let users edit a policy', () => {
      const button = wrapper.find(Policy).first().find(EditRegion).find('button')
      button.simulate('click')
      expect(wrapper.find(PolicyEdit)).to.have.length(1)
    })

    describe('<PolicyEdit />', () => {
      // make each test independent by restoring state after each test
      beforeEach(save)
      afterEach(restore)

      it('should let user cancel editing a policy', () => {
        const cancel = wrapper.find(PolicyEdit).find(CancelButton)
        cancel.simulate('click')
        expect(wrapper.find(PolicyEdit)).to.have.length(0)
      })

      it('should let user delete a policy', () => {
        expect(wrapper.find(PolicyEdit).find(DeleteButton)).to.have.length(1)
      })

      it('should confirm when user tries to delete a policy', () => {
        wrapper.find(PolicyEdit).find(DeleteButton).find('button').simulate('click')
        const confirmDelete = wrapper.find(PolicyEdit).find('p')
          .findWhere((node) => node.text() === 'Confirm policy deletion?')
        expect(confirmDelete).to.have.length(1)
      })
    })
  })
})
