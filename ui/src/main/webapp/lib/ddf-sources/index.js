import { gql } from 'react-apollo'

const CSW = {
  query: gql`
    query discovercsw ($address: Address!, $creds: Credentials) {
      csw {
        discover(
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
    mutation CreateCswSource($config: CswSourceConfiguration!){
      createCswSource(source : $config)
    }
  `,
  selector: (result) => result.csw.discover
}

const WFS = {
  query: gql`
    query discoverWfs ($address: Address!, $creds: Credentials) {
      wfs {
        discover(
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
    mutation CreateWfsSource($config: WfsSourceConfiguration!){
      createWfsSource(source : $config)
    }
  `,
  selector: (result) => result.wfs.discover
}

const OpenSearch = {
  query: gql`
    query discoverOpenSearch ($address: Address!, $creds: Credentials) {
      openSearch {
        discover(
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
    mutation CreateOpenSearchSource($config: OpenSearchConfiguration!){
      createOpenSearchSource(source : $config)
    }
  `,
  selector: (result) => result.openSearch.discover
}

export default { CSW, WFS, OpenSearch }
