import React from 'react'

import { Link } from 'react-router'

import Title from 'components/Title'
import Description from 'components/Description'
import LargeStatusIndicator from 'components/LargeStatusIndicator'

import Navigation, { Finish } from 'components/wizard/Navigation'

export default () => (
  <div>
    <Title>LDAP Configurations Have Been Successfully Saved!</Title>

    <LargeStatusIndicator success />

    <Description>
      Now that your LDAP is configured, the final step is to secure
      endpoints using the Web Context Policy Manager.
    </Description>

    <Navigation>
      <Link to='/web-context-policy-manager'>
        <Finish label='Go to Web Context Policy Manager' />
      </Link>
    </Navigation>
  </div>
)
