import React from 'react'

import Flexbox from 'flexbox-react'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'
import Info from 'components/Information'
import Spinner from 'components/Spinner'
import Message from 'components/Message'

import { confirmationInfo } from './styles.less'

const useCaseMapping = {
  authentication: 'Authentication source',
  attributeStore: 'Attribute store',
  authenticationAndAttributeStore: 'Authentication and attribute store'
}

export default ({ disabled, prev, persist, submitting, messages = [], configs }) => (
  <Stage>
    <Spinner submitting={submitting}>
      <Title>LDAP Settings Confirmation</Title>

      <Description>
        All of the values have been successfully verified. Would you like to
        save the LDAP configuration?
      </Description>

      <Flexbox flexDirection='column'>
        <Flexbox flexDirection='row' justifyContent='space-between'>
          <Flexbox className={confirmationInfo} flexDirection='column'>
            <Info label='LDAP Function' value={useCaseMapping[configs.ldapUseCase]} />
            <Info label='Hostname' value={configs.hostName} />
            <Info label='Port' value={configs.port} />
            <Info label='Encryption Method' value={configs.encryptionMethod} />
            <Info label='Base User DN' value={configs.baseUserDn} />
            <Info label='User Name Attribute' value={configs.userNameAttribute} />
          </Flexbox>
          <Flexbox className={confirmationInfo} flexDirection='column'>
            <Info label='Base Group DN' value={configs.baseGroupDn} />
            <Info label='Bind User DN' value={configs.bindUserDn} />
            <Info label='Bind User Password' value='*****' />
            <Info label='Bind User Method' value={configs.bindUserMethod} />
            <Info label='LDAP Group Object Class' value={configs.groupObjectClass} />
            <Info label='Group Attribute Holding Member References'
              value={configs.groupAttributeHoldingMember} />
            <Info label='Member Attribute Referenced in Groups'
              value={configs.memberAttributeReferencedInGroup} />
          </Flexbox>
        </Flexbox>
        { configs.ldapUseCase !== 'authentication'
          ? (
            <div>
              <Info label='Attribute Mappings'
                value={Object.keys(configs.attributeMappings)
                  .map((key) => key + ' = ' + configs.attributeMappings[key])} />
            </div>
          ) : null }
      </Flexbox>

      <ActionGroup>
        <Action
          secondary
          label='back'
          onClick={prev}
          disabled={disabled} />
        <Action
          primary
          label='save'
          onClick={persist}
          disabled={disabled}
          persistId='create'
          nextStageId='final-stage' />
      </ActionGroup>

      {messages.map((msg, i) => <Message key={i} {...msg} />)}
    </Spinner>
  </Stage>
)
