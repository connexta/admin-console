import React from 'react'

import { gql, withApollo } from 'react-apollo'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Message from 'components/Message'

import Body from 'components/wizard/Body'
import Navigation, { Back, Finish } from 'components/wizard/Navigation'

const useCaseDescription = (ldapUseCase) => {
  switch (ldapUseCase) {
    case 'authenticationAndAttributeStore':
      return 'authentication source & attribute store'
    case 'authentication' :
      return 'authentication source'
    default:
      return 'credential store'
  }
}

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

    disabled,
    submitting,
    configs: {
      ldapUseCase
    } = {},
    messages = [],

    prev
  } = props

  return (
    <Stage submitting={submitting}>
      <Title>Are You Sure You Want to Install Embedded LDAP?</Title>
      <Description>
        { /* todo - add a 'warning-style' box around this <p/> */ }
        <p>
          The embedded LDAP server is used for testing purposes only and
          should not be used in a production environment.
        </p>
        <p>
          Installing Embedded LDAP will start up the internal LDAP and
          configure it as a {useCaseDescription(ldapUseCase)}.
        </p>
      </Description>
      <Body>
        <Navigation>
          <Back
            onClick={prev}
            disabled={disabled} />
          <Finish
            onClick={() => {
              onStartSubmit()
              client.mutate(installEmbeddedLdap(ldapUseCase))
                .then(() => {
                  onEndSubmit()
                  onError([])
                  next({ nextStageId: 'final-stage' })
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err.graphQLErrors)
                })
            }}
            disabled={disabled} />
        </Navigation>
        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Body>
    </Stage>
  )
}

export default withApollo(ConfigureEmbeddedLdap)
