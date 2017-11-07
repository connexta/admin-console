import React from 'react'

import { gql, withApollo } from 'react-apollo'

import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'

import Body from 'components/wizard/Body'
import Navigation, { Back, Finish } from 'components/wizard/Navigation'

const installEmbeddedLdap = (useCase) => ({
  mutation: gql`
    mutation InstallEmbeddedLdap($useCase: LdapUseCase!) {
      installEmbeddedLdap(useCase: $useCase)
    }
  `,
  variables: { useCase }
})

const ConfigureEmbeddedLdap = (props) => {
  const {
    client,
    onError,
    onStartSubmit,
    onEndSubmit,
    next,

    configs: {
      ldapUseCase
    } = {},
    errors: messages = [],

    prev
  } = props

  return (
    <div>
      <Title>Are You Sure You Want to Install Embedded LDAP?</Title>
      <Description>
        { /* todo - add a 'warning-style' box around this <p/> */ }
        <p>
          The embedded LDAP server is used for testing purposes only and
          should not be used in a production environment.
        </p>
      </Description>
      <Body>
        <Navigation>
          <Back onClick={prev} />
          <Finish
            onClick={() => {
              onStartSubmit()
              client.mutate(installEmbeddedLdap(ldapUseCase))
                .then(() => {
                  onEndSubmit()
                  next('final-stage')
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err.graphQLErrors)
                })
            }}
          />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Body>
    </div>
  )
}

export default withApollo(ConfigureEmbeddedLdap)
