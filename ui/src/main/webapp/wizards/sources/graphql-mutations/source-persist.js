const getSourceMutation = ({ mutation, config, sourceName, creds }) => {
  let finalConfig = {
    ...config,
    sourceName
  }

  if (creds) {
    finalConfig.creds = creds
  }

  return {
    mutation: mutation,
    variables: {
      config: finalConfig
    }
  }
}

export const saveSource = (props) => {
  const {
    client,
    type,
    sources,
    config,
    sourceName,
    inputConfigs
  } = props

  return client.mutate(getSourceMutation({
    mutation: sources[type].mutation,
    config,
    sourceName,
    creds: {
      username: (inputConfigs.sourceUserName || undefined),
      password: (inputConfigs.sourceUserPassword || undefined)
    }}))
}

