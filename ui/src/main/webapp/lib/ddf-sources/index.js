import { gql } from 'react-apollo'

const CSW = {
  query: (variables) => ({
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
    variables
  }),
  mutation: (variables) => ({
    mutation: gql`
      mutation CreateCswSource($config: CswSourceConfiguration!){
        createCswSource(source : $config)
      }
    `,
    variables
  }),
  selector: (result) => result.csw.discover
}

const WFS = {
  query: (variables) => ({
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
    variables
  }),
  mutation: (variables) => ({
    mutation: gql`
      mutation CreateWfsSource($config: WfsSourceConfiguration!){
        createWfsSource(source : $config)
      }
    `,
    variables
  }),
  selector: (result) => result.wfs.discover
}

const OpenSearch = {
  query: (variables) => ({
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
    variables
  }),
  mutation: (variables) => ({
    mutation: gql`
      mutation CreateOpenSearchSource($config: OpenSearchConfiguration!){
        createOpenSearchSource(source : $config)
      }
    `,
    variables
  }),
  selector: (result) => result.openSearch.discover
}

export default { CSW, WFS, OpenSearch }
