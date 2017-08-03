import React from 'react'

import Title from 'components/Title'
import Description from 'components/Description'
import Navigation, { Begin } from 'components/wizard/Navigation'

export default ({ next }) => (
  <div>
    <Title>
      Welcome to the Source Configuration Wizard
    </Title>
    <Description>
      This wizard will guide you through discovering and configuring
      various sources that are used to query metadata from catalogs.
      To begin, make sure you have the hostname and port of the source you plan to configure.
    </Description>
    <Navigation>
      <Begin
        name='Sources'
        onClick={() => next('discoveryStage')}
      />
    </Navigation>
  </div>
)
