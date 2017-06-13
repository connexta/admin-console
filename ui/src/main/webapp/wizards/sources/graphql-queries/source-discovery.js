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
          isAvailable
          sourceConfig {
            ...openSearchConfigurationPayload
          }
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
          isAvailable
          sourceConfig {
            ...wfsConfigurationPayload
          }
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
          isAvailable
          sourceConfig {
            ...cswSourceConfigurationPayload
          }
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

export const acceptableErrors = [
  'UNKNOWN_ENDPOINT',
  'CANNOT_CONNECT',
  'UNAUTHORIZED'
]

export const isAcceptableError = (code) => acceptableErrors.includes(code)

// combines, flattens, dedupes, filters out OK errors
export const cleanErrors = (results) => (
  combineAndReduceErrors(results).filter((code) => !isAcceptableError(code))
)

// combines, flattens, and dedupes
export const combineAndReduceErrors = (results) => (
  results.map(({ value }) => value)
    .reduce((acc, value) => acc.concat(value), [])
    .map(({ message }) => message)
    .filter((message, i, errors) => errors.indexOf(message) === i)
)

// gets friendly messages
export const mapToFriendlyMessages = (messages) => (
  messages.map((code) => getFriendlyMessage(code))
)

// removes errors and maps sourceType to sourceConfigs
export const formatSources = (results) => (
  results.filter(({ type }) => type !== 'ERROR')
    .reduce((acc, { sourceType, value }) => ({ ...acc, [sourceType]: value }), {})
)

export const queryAllSources = (props, onPass, onFail) => {
  const {
    client,
    configs,
    discoveryType,
    startSubmitting,
    endSubmitting,
    setDiscoveredEndpoints
  } = props

  const dispatchQuery = (sourceType, extractConfig) =>
    client.query(discoverSources({ sourceType, configs, discoveryType }))
      .then(({ data }) => ({
        type: 'SUCCESS',
        sourceType,
        value: extractConfig(data)
      }))
      .catch((e) => ({ type: 'ERROR', sourceType, value: e.graphQLErrors }))

  startSubmitting()
  Promise.all([
    dispatchQuery('CSW', (data) => (data.csw.discoverCsw.sourceConfig)),
    dispatchQuery('WFS', (data) => (data.wfs.discoverWfs.sourceConfig)),
    dispatchQuery('OpenSearch', (data) => (data.openSearch.discoverOpenSearch.sourceConfig))
  ]).then((values) => {
    endSubmitting()

    // if all results have error, check for causes
    if (values.every(({type}) => type === 'ERROR')) {
      // if performing a URL discovery, display all errors
      if (discoveryType === 'url') {
        if (onFail) onFail(mapToFriendlyMessages(combineAndReduceErrors(values)))
        return
      }

      const uniqueErrors = cleanErrors(values)
      // if no actual errors remain, just set empty source results
      if (uniqueErrors.length === 0) {
        setDiscoveredEndpoints({})
        if (onPass) onPass()
        return
      }
      if (onFail) onFail(mapToFriendlyMessages(uniqueErrors))
    } else {
      // if any results come back successful, set source results
      setDiscoveredEndpoints(formatSources(values))
      if (onPass) onPass()
    }
  })
}
