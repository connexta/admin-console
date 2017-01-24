import React from 'react'

import { Link } from 'react-router'

import Stage from 'components/Stage'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'
import Title from 'components/Title'
import Description from 'components/Description'
import LargeStatusIndicator from 'components/LargeStatusIndicator'

export default () => (
  <Stage>
    <Title>LDAP configurations have been successfully saved!</Title>

    <LargeStatusIndicator success />

    <Description>
      Now that your LDAP is configured, the final step is to use it to secure REST
      endpoints using the Web Context Policy Manager.
    </Description>

    <ActionGroup>
      <Link to='/web-context-policy-manager'>
        <Action primary label='Go to Web Context Policy Manager' />
      </Link>
    </ActionGroup>
  </Stage>
)
