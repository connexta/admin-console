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
          pid
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
      saveCswSource(source : $config)
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
          pid
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
      saveWfsSource(source : $config)
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
          pid
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
      saveOpenSearchSource(source : $config)
    }
  `,
  selector: (result) => result.openSearch.discoverOpenSearch
}

export default { CSW, WFS, OpenSearch }
