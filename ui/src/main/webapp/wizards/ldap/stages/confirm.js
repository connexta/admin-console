import React from 'react'

import Flexbox from 'flexbox-react'

import Divider from 'material-ui/Divider'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Action from 'components/Action'
import ActionGroup from 'components/ActionGroup'
import Info from 'components/Information'

const useCaseMapping = ({
  'authentication': 'Authentication source',
  'attributeStore': 'Attribute store',
  'authenticationAndAttributeStore': 'Authentication and attribute store'
})

export default ({ disabled, prev, persist, configs }) => (
  <Stage>
    <Title>LDAP Confirm</Title>

    <Description>
      Below are your final LDAP configuration settings.
    </Description>

    <Flexbox flexDirection='column'>
      <Flexbox flexDirection='row' justifyContent='space-between'>
        <Flexbox flexDirection='column'>
          <Info label='LDAP Function' value={useCaseMapping[configs.ldapUseCase]} />
          <Info label='Hostname' value={configs.hostName} />
          <Info label='Port' value={configs.port} />
          <Info label='Encryption Method' value={configs.encryptionMethod} />
          <Info label='Base User DN' value={configs.baseUserDn} />
        </Flexbox>
        <Flexbox flexDirection='column'>
          <Info label='User Name Attribute' value={configs.userNameAttribute} />
          <Info label='Base Group DN' value={configs.baseGroupDn} />
          <Info label='Bind User DN' value={configs.bindUserDn} />
          <Info label='Bind User Password' value='*****' />
          <Info label='Bind User Method' value={configs.bindUserMethod} />
        </Flexbox>
      </Flexbox>
      <Divider />
      { configs.ldapUseCase !== 'authentication'
        ? (
          <div>
            <Info label='LDAP Group Object Class' value={configs.groupObjectClass} />
            <Info label='Membership Attribute' value={configs.membershipAttribute} />
            <Info label='Attribute Mappings'
              value={Object.keys(configs.attributeMappings).map(
                  (key) => {
                    return key + ' = ' + configs.attributeMappings[key]
                  })
                }
          />
            <Divider />
          </div>
        )
        : null
      }
    </Flexbox>

    {/* // todo: below are most of the config settings - decide which of these to display here

    */}

    <Description>
      All of the values have been successfully verified. Would you like to
      save the LDAP configuration?
    </Description>

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
  </Stage>
)
