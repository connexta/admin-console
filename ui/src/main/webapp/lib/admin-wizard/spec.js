import React from 'react'

import { expect } from 'chai'
import { mount } from 'enzyme'

import { createStore } from 'redux'
import { Provider } from 'react-redux'

import Mount from 'react-mount'

import CircularProgress from 'material-ui/CircularProgress'
import MTP from 'material-ui/styles/MuiThemeProvider'

import Wizard from './wizard'
import { createWizard } from './'
import reducer from './reducer'

import * as enhancers from './enhancers'

const {
  withConfigs,
  withOptions,
  withErrors,
  withLoading
} = enhancers

const withContainer = (Component, state = {}) => class extends React.Component {
  constructor (props) {
    super(props)
    this.state = { value: undefined, ...state }
  }
  render () {
    return (
      <Component
        state={this.state.value}
        setState={(value) => this.setState({ value })} />
    )
  }
}

describe('admin-wizard', () => {
  it('should clear shared state on wizard un-mount', (done) => {
    const clearShared = () => done()

    const Wrapper = ({ render = true }) => render
      ? <Wizard
        clearShared={clearShared}
        stageId='hello'
        stages={{ hello: () => null }}
      />
      : null

    mount(<Wrapper />).setProps({ render: false })
  })

  it('should clear local state on every stage transition', (done) => {
    const clearLocal = () => done()

    const Wrapper = ({ stageId = 'hello' }) =>
      <Wizard
        clearLocal={clearLocal}
        stageId={stageId}
        stages={{ hello: () => null, world: () => null }}
      />

    mount(<Wrapper />).setProps({ stageId: 'world' })
  })

  it('should pass local and shared for enhancers', () => {
    const Stage = (props) => {
      const keys = Object.keys(props)

      expect(keys).to.include('local')
      expect(props.setLocal).to.be.instanceof(Function)
      expect(keys).to.include('shared')
      expect(props.setShared).to.be.instanceof(Function)

      expect(props.restart).to.be.instanceof(Function)
      return null
    }

    const store = createStore(reducer)
    const Wizard = createWizard('test', { 'introduction-stage': Stage })
    mount(<Provider store={store}><MTP><Wizard /></MTP></Provider>)
  })

  describe('enhancers', () => {
    it('they should pass through all props', () => {
      const props = { hello: 'world' }
      Object.keys(enhancers)
        .map((key) => enhancers[key])
        .forEach((enhancer) => {
          const Dummy = () => null
          const Wrapper = enhancer(Dummy)
          const wrapper = mount(<MTP><Wrapper props={props} /></MTP>)
          expect(wrapper.find(Dummy).prop('hello')).to.equal('world')
        })
    })

    describe('withConfigs(Component)', () => {
      it('should pass configs, onEdit, setDefaults props', () => {
        const Component = withConfigs(({ configs, onEdit, setDefaults }) => {
          expect(configs).to.deep.equal({})
          expect(onEdit).to.be.instanceof(Function)
          expect(setDefaults).to.be.instanceof(Function)
          return null
        })

        mount(<Component />)
      })

      it('should render the state correctly', () => {
        const state = { key: true }

        const Component = withConfigs(({ configs }) =>
          <div className={configs.key ? 'true' : 'false'} />
        )

        const wrapper = mount(<Component state={state} />)
        expect(wrapper.find('.true')).to.have.length(1)
      })

      it('should set default config values', (done) => {
        const setState = (state) => {
          expect(state.get('key')).to.equal('value')
          done()
        }

        const Component = withConfigs(({ setDefaults }) =>
          <Mount on={() => setDefaults({ key: 'value' })} />
        )

        mount(<Component setState={setState} />)
      })

      it('should not allow defaults to override existing values', (done) => {
        const state = { key: 'existing-value' }

        const setState = (state) => {
          expect(state.get('key')).to.equal('existing-value')
          done()
        }

        const Component = withConfigs(({ setDefaults }) =>
          <Mount on={() => setDefaults({ key: 'new-value' })} />
        )

        mount(<Component state={state} setState={setState} />)
      })

      it('should edit a config value', (done) => {
        const setState = (state) => {
          expect(state.get('key')).to.equal('value')
          done()
        }

        const Component = withConfigs(({ onEdit }) =>
          <Mount on={() => onEdit('key')('value')} />
        )

        mount(<Component setState={setState} />)
      })
    })

    describe('withOptions(Component)', () => {
      it('should pass options and setOptions props', () => {
        const Component = withOptions(({ options, setOptions }) => {
          expect(options).to.not.equal(undefined)
          expect(setOptions).to.be.instanceof(Function)
          return null
        })

        mount(<Component />)
      })
    })

    describe('withErrors(Component)', () => {
      it('should pass errors, onError props', () => {
        const Component = withErrors(({ errors, onError }) => {
          expect(errors).to.deep.equal([])
          expect(onError).to.be.instanceof(Function)
          return null
        })

        mount(<Component />)
      })

      it('should set error values', () => {
        const Component = withErrors(({ errors, onError }) =>
          <div className={errors.length}>
            <Mount on={() => onError({
              graphQLErrors: [{ message: 'hello' }]
            })} />
          </div>
        )

        const Container = withContainer(Component)

        const wrapper = mount(<Container />)
        expect(wrapper.find('.1')).to.have.length(1)
      })
    })

    describe('withLoading(Component)', () => {
      it('should pass submitting, onStartSubmit and onEndSubmit props', () => {
        const Component = withLoading(({ onStartSubmit, onEndSubmit }) => {
          expect(onStartSubmit).to.be.instanceof(Function)
          expect(onEndSubmit).to.be.instanceof(Function)
          return null
        })

        mount(<Component />)
      })

      it('should not not display the spinner by default', () => {
        const Component = withLoading(({ onStartSubmit }) => <div />)

        const Container = withContainer(Component)

        const wrapper = mount(<MTP><Container /></MTP>)
        expect(wrapper.find(CircularProgress)).to.have.length(0)
      })

      it('should display the spinner on start submit', () => {
        const Component = withLoading(({ onStartSubmit }) =>
          <Mount on={() => onStartSubmit()} />
        )

        const Container = withContainer(Component)

        const wrapper = mount(<MTP><Container /></MTP>)
        expect(wrapper.find(CircularProgress)).to.have.length(1)
      })

      it('should remove the spinner on end submit', () => {
        const Component = withLoading(({ onEndSubmit }) =>
          <Mount on={() => onEndSubmit()} />
        )

        const Container = withContainer(Component, { value: true })

        const wrapper = mount(<MTP><Container /></MTP>)
        expect(wrapper.find(CircularProgress)).to.have.length(0)
      })
    })
  })
})
