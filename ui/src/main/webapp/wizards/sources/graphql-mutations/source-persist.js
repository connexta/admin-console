import { gql } from 'react-apollo'

const saveSourceMutations = {
  CSW: gql`
    mutation SaveCswSource($config: CswSourceConfiguration!){
      saveCswSource(sourceConfig : $config)
    }
  `,
  WFS: gql`
    mutation SaveWfsSource($config: WfsSourceConfiguration!){
      saveWfsSource(sourceConfig : $config)
    }
  `,
  OpenSearch: gql`
    mutation SaveOpenSearchSource($config: OpenSearchConfiguration!){
      saveOpenSearchSource(sourceConfig : $config)
    }
  `
}

const getSourceMutation = ({ type, config, sourceName, creds }) => {
  let finalConfig = {
    ...config,
    sourceName
  }

  if (creds) {
    finalConfig.creds = creds
  }

  return {
    mutation: saveSourceMutations[type],
    variables: {
      config: finalConfig
    }
  }
}

const saveSource = (props) => {
  const {
    client,
    type,
    config,
    sourceName,
    inputConfigs
  } = props

  return client.mutate(getSourceMutation({
    type,
    config,
    sourceName,
    creds: {
      username: inputConfigs.sourceUserName,
      password: inputConfigs.sourceUserPassword
    }}))
}

export { saveSourceMutations, saveSource }
