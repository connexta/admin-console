const discoverSources = ({ query, configs, discoveryType }) => {
  let queryObject = {
    query,
    variables: {},
    fetchPolicy: 'network-only'
  }

  if (discoveryType === 'hostnamePort') {
    queryObject.variables.address = {
      host: {
        hostname: configs.sourceHostName,
        port: configs.sourcePort
      }
    }
  } else {
    queryObject.variables.address = {
      url: configs.endpointUrl
    }
  }

  if (configs.sourceUserName && configs.sourceUserPassword) {
    queryObject.variables.creds = {
      username: configs.sourceUserName,
      password: configs.sourceUserPassword
    }
  }
  return queryObject
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
    sources
  } = props

  const dispatchQuery = (sourceType, query, selector) =>
    client.query(discoverSources({ query, configs, discoveryType }))
      .then(({ data }) => ({ type: 'SUCCESS', sourceType, value: selector(data) }))
      .catch((e) => ({ type: 'ERROR', sourceType, value: e.graphQLErrors }))

  return Promise.all(Object.keys(sources).map((key) =>
    dispatchQuery(key, sources[key].query, sources[key].selector)
  )).then((responses) => new Promise((resolve, reject) => {
    const { foundSources, uniqueErrors, fatalErrors } = groupResponses(responses)

    if (Object.keys(foundSources).length > 0) {
      resolve(foundSources)
    } else if (discoveryType === 'url') {
      reject(uniqueErrors.map((message) => ({ message })))
    } else if (fatalErrors.length > 0) {
      reject(fatalErrors.map((message) => ({ message })))
    } else {
      resolve({})
    }
  }))
}
