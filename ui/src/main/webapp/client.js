import { ApolloClient } from 'react-apollo'
import { createBatchingNetworkInterface } from 'apollo-client'
import { Map, List, fromJS } from 'immutable'

const reduxRootSelector = (state) => state.get('apollo')
const networkInterface = createBatchingNetworkInterface({
  uri: '/admin/hub/graphql',
  batchInterval: 100,
  opts: {
    credentials: 'same-origin'
  }
})

const stripTypename = (obj) => {
  if (Map.isMap(obj)) {
    return obj.remove('__typename').map(stripTypename)
  } else if (List.isList(obj)) {
    return obj.map(stripTypename)
  } else {
    return obj
  }
}

const middleware = {
  applyBatchMiddleware ({ requests }, next) {
    requests.forEach(({ variables }, i) => {
      const stripped = stripTypename(fromJS(variables))
      if (Map.isMap(stripped) || List.isList(stripped)) {
        requests[i].variables = stripped.toJS()
      }
    })
    next()
  }
}

networkInterface.use([middleware])

export default new ApolloClient({
  networkInterface,
  reduxRootSelector
})
