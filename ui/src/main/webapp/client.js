import { ApolloClient } from 'react-apollo'
import { createBatchingNetworkInterface } from 'apollo-client'

const reduxRootSelector = (state) => state.get('apollo')
const networkInterface = createBatchingNetworkInterface({
  uri: '/admin/hub/graphql',
  batchInterval: 100,
  opts: {
    credentials: 'same-origin'
  }
})

export default new ApolloClient({
  networkInterface,
  reduxRootSelector
})
