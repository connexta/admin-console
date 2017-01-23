import React from 'react'

import Mount from 'react-mount'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'
import Message from 'components/Message'
import Spinner from 'components/Spinner'

import {
  Hostname,
  Port,
  Select
} from 'admin-wizard/inputs'

const NetworkSettings = (props) => {
  const {
    setDefaults,
    prev,
    test,
    submitting,
    disabled,
    messages = []
  } = props

  return (
    <Stage>
      <Mount
        on={setDefaults}
        port={1636}
        encryptionMethod='LDAPS'
        hostName='localhost' />
      <Spinner submitting={submitting}>
        <Title>LDAP Network Settings</Title>
        <Description>
          Lets start with the network configurations of your LDAP store.
        </Description>

        <Hostname id='hostName' disabled={disabled} />
        <Port id='port' disabled={disabled} options={[389, 636]} />
        <Select id='encryptionMethod'
          label='Encryption Method'
          disabled={disabled}
          options={[ 'None', 'LDAPS', 'StartTLS' ]} />

        <ActionGroup>
          <Action
            secondary
            label='back'
            onClick={prev}
            disabled={disabled} />
          <Action
            primary
            label='next'
            onClick={test}
            disabled={disabled}
            nextStageId='bind-settings'
            testId='connection' />
        </ActionGroup>

        {messages.map((msg, i) => <Message key={i} {...msg} />)}
      </Spinner>
    </Stage>
  )
}

export default NetworkSettings
