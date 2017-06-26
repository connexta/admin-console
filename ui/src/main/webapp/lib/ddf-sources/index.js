import { gql } from 'react-apollo'

const CSW = {
  query: gql`
    query discovercsw ($address: Address!, $creds: Credentials) {
      csw {
        discoverCsw(
          address : $address,
          creds : $creds
        )
        {
          cswProfile
          sourceName
          endpointUrl
          cswOutputSchema
          cswSpatialOperator
          creds {
            username
            password
          }
        }
      }
    }
  `,
  mutation: gql`
    mutation SaveCswSource($config: CswSourceConfiguration!){
      createCswSource(source : $config)
    }
  `,
  selector: (result) => result.csw.discoverCsw
}

const WFS = {
  query: gql`
    query discoverWfs ($address: Address!, $creds: Credentials) {
      wfs {
        discoverWfs(
          address : $address,
          creds : $creds
        )
        {
          wfsVersion
          sourceName
          endpointUrl
          creds {
            username
            password
          }
        }
      }
    }
  `,
  mutation: gql`
    mutation SaveWfsSource($config: WfsSourceConfiguration!){
      createWfsSource(source : $config)
    }
  `,
  selector: (result) => result.wfs.discoverWfs
}

const OpenSearch = {
  query: gql`
    query discoverOpenSearch ($address: Address!, $creds: Credentials) {
      openSearch {
        discoverOpenSearch(
          address : $address,
          creds : $creds
        )
        {
          sourceName
          endpointUrl
          creds {
            username
            password
          }
        }
      }
    }
  `,
  mutation: gql`
    mutation SaveOpenSearchSource($config: OpenSearchConfiguration!){
      createOpenSearchSource(source : $config)
    }
  `,
  selector: (result) => result.openSearch.discoverOpenSearch
}

export default { CSW, WFS, OpenSearch }
