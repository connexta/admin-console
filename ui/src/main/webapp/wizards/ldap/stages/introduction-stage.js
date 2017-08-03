import React from 'react'

import Title from 'components/Title'
import Description from 'components/Description'

import Navigation, { Begin } from 'components/wizard/Navigation'

const IntroductionStage = ({ next, configs: { ldapUseCase } = {} }) => (
  <div>
    <Title>Welcome to the LDAP Configuration Wizard</Title>
    <Description>
      This wizard will guide you through configuring an LDAP store. An LDAP can be configured
      as an authentication source for users to log in and/or it can be setup as an attribute
      store to provide user attributes to a different authentication source.
    </Description>
    <Navigation>
      <Begin
        name='ldap'
        onClick={() => next('use-case-stage')} />
    </Navigation>
  </div>
)

export default IntroductionStage
