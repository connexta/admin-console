export const saveSource = (props) => {
  const {
    client,
    type,
    sources,
    config,
    sourceName,
    inputConfigs
  } = props

  const mutation = sources[type].mutation

  const variables = {
    config: {
      ...config,
      sourceName,
      creds: {
        username: inputConfigs.sourceUserName || undefined,
        password: inputConfigs.sourceUserPassword || undefined
      }
    }
  }

  return client.mutate(mutation(variables))
}
