import { gql } from 'react-apollo'
import { getFriendlyMessage } from 'graphql-errors'

export const payloadFragments = {
  cswConfigurationPayload: gql`
    fragment cswSourceConfigurationPayload on CswSourceConfigurationPayload {
      cswProfile
      sourceName
      endpointUrl
      cswOutputSchema
      cswSpatialOperator
      pid
      creds {
        username
        password
      }
    }
  `,
  openSearchConfigurationPayload: gql`
    fragment openSearchConfigurationPayload on OpenSearchConfigurationPayload {
      sourceName
      endpointUrl
      pid
      creds {
        username
        password
      }
    }
  `,
  wfsConfigurationPayload: gql`
    fragment wfsConfigurationPayload on WfsSourceConfigurationPayload {
      wfsVersion
      sourceName
      endpointUrl
      pid
      creds {
        username
        password
      }
    }
  `
}

export const queryFragments = {
  discoverOpenSearch: gql`
    fragment discoverOpenSearch on Query {
      openSearch {
        discoverOpenSearch(
          address : $address,
          creds : $creds
        )
        {
          ...openSearchConfigurationPayload
        }
      }
    }
    ${payloadFragments.openSearchConfigurationPayload}
  `,
  discoverWfs: gql`
    fragment discoverWfs on Query {
      wfs {
        discoverWfs(
          address : $address,
          creds : $creds
        )
        {
          ...wfsConfigurationPayload
        }
      }
    }
    ${payloadFragments.wfsConfigurationPayload}
  `,
  discoverCsw: gql`
    fragment discoverCsw on Query {
      csw {
        discoverCsw(
          address : $address,
          creds : $creds
        )
        {
          ...cswSourceConfigurationPayload
        }
      }
    }
    ${payloadFragments.cswConfigurationPayload}
  `
}

export const queries = {
  CSW: gql`
    query discovercsw ($address: Address!, $creds: Credentials) {
      ...discoverCsw
    }
    ${queryFragments.discoverCsw}
  `,
  OpenSearch: gql`
    query discoverOpenSearch ($address: Address!, $creds: Credentials) {
      ...discoverOpenSearch
    }
    ${queryFragments.discoverOpenSearch}
  `,
  WFS: gql`
    query discoverWfs ($address: Address!, $creds: Credentials) {
      ...discoverWfs
    }
    ${queryFragments.discoverWfs}
  `,
  all: gql`
    query discoverAllSources ($address: Address!, $creds: Credentials) {
      ...discoverOpenSearch
      ...discoverWfs
      ...discoverCsw
    }
    ${queryFragments.discoverOpenSearch}
    ${queryFragments.discoverWfs}
    ${queryFragments.discoverCsw}
  `
}

export const discoverSources = ({ sourceType, configs, discoveryType }) => {
  let query = {
    query: queries[sourceType],
    variables: {},
    fetchPolicy: 'network-only'
  }

  if (discoveryType === 'hostnamePort') {
    query.variables.address = {
      host: {
        hostname: configs.sourceHostName,
        port: configs.sourcePort
      }
    }
  } else {
    query.variables.address = {
      url: configs.endpointUrl
    }
  }

  if (configs.sourceUserName && configs.sourceUserPassword) {
    query.variables.creds = {
      username: configs.sourceUserName,
      password: configs.sourceUserPassword
    }
  }

  return query
}

const nonFatalErrors = [
  'UNKNOWN_ENDPOINT',
  'CANNOT_CONNECT',
  'UNAUTHORIZED'
]

const isFatalError = (code) => !nonFatalErrors.includes(code)

export const groupResponses = (responses) => responses.reduce((acc, response) => {
  const { foundSources = {}, uniqueErrors = [], fatalErrors = [] } = acc
  const { type, sourceType, value } = response

  if (type === 'SUCCESS') {
    foundSources[sourceType] = value
  } else {
    value.forEach((error) => {
      const code = error.message
      if (!uniqueErrors.includes(code)) {
        uniqueErrors.push(code)
        if (isFatalError(code)) {
          fatalErrors.push(code)
        }
      }
    })
  }

  return { foundSources, uniqueErrors, fatalErrors }
}, {})

export const queryAllSources = (props) => {
  const {
    client,
    configs,
    discoveryType,
    startSubmitting,
    endSubmitting
  } = props

  const dispatchQuery = (sourceType, extractConfig) =>
    client.query(discoverSources({ sourceType, configs, discoveryType }))
      .then(({ data }) => ({ type: 'SUCCESS', sourceType, value: extractConfig(data) }))
      .catch((e) => ({ type: 'ERROR', sourceType, value: e.graphQLErrors }))

  startSubmitting()
  return Promise.all([
    dispatchQuery('CSW', (data) => (data.csw.discoverCsw)),
    dispatchQuery('WFS', (data) => (data.wfs.discoverWfs)),
    dispatchQuery('OpenSearch', (data) => (data.openSearch.discoverOpenSearch))
  ]).then((responses) => new Promise((resolve, reject) => {
    endSubmitting()

    const { foundSources, uniqueErrors, fatalErrors } = groupResponses(responses)

    if (Object.keys(foundSources).length > 0) {
      resolve(foundSources)
    } else if (discoveryType === 'url') {
      reject(uniqueErrors.map(getFriendlyMessage))
    } else if (fatalErrors.length > 0) {
      reject(fatalErrors.map(getFriendlyMessage))
    } else {
      resolve({})
    }
  }))
}
