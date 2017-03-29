import { ApolloClient, createNetworkInterface } from 'react-apollo'

const reduxRootSelector = (state) => state.get('apollo')
const networkInterface = createNetworkInterface({
  uri: '/admin/beta/graphql',
  opts: {
    credentials: 'same-origin'
  }
})

export default new ApolloClient({ networkInterface, reduxRootSelector })

