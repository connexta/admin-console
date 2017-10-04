import React from 'react'

import Flexbox from 'flexbox-react'

import { gql, withApollo } from 'react-apollo'

import Title from 'components/Title'
import Description from 'components/Description'
import Info from 'components/Information'
import Message from 'components/Message'
import MapDisplay from 'components/MapDisplay'

import Body from 'components/wizard/Body'
import Navigation, { Back, Finish } from 'components/wizard/Navigation'

const useCaseMapping = {
  Authentication: 'Authentication',
  AttributeStore: 'Attribute Store',
  AuthenticationAndAttributeStore: 'Authentication and Attribute Store'
}

const confirmationInfo = ({
  width: '50%',
  overflow: 'hidden',
  textOverflow: 'ellipsis'
})

const createLdapConfig = (conn, info, settings, mapping) => ({
  mutation: gql`
    mutation CreateLdapConfig(
      $conn: LdapConnection,
      $info: BindUserInfo,
      $settings: LdapDirectorySettings,
      $mapping: [ClaimsMapEntry]
    ) {
      createLdapConfig(config: {
        connection: $conn,
        bindInfo: $info,
        directorySettings: $settings,
        claimsMapping: $mapping
      })
    }
  `,
  variables: { conn, info, settings, mapping }
})

const ConfirmStage = (props) => {
  const {
    client,
    onError,
    onStartSubmit,
    onEndSubmit,
    next,

    configs: {
      ldapUseCase
    } = {},

    prev,
    messages = [],
    configs
  } = props

  const isAttrStore = ldapUseCase === 'AuthenticationAndAttributeStore' || ldapUseCase === 'AttributeStore'

  const conn = {
    hostname: configs.hostname,
    port: configs.port,
    encryption: configs.encryption
  }

  const info = {
    creds: {
      username: configs.bindUser,
      password: configs.bindUserPassword
    },
    bindMethod: configs.bindUserMethod,
    realm: configs.bindRealm
  }

  const settings = {
    userNameAttribute: configs.userNameAttribute,
    baseUserDn: configs.baseUserDn,
    baseGroupDn: configs.baseGroupDn,
    useCase: configs.ldapUseCase
  }

  if (isAttrStore) {
    settings.memberAttributeReferencedInGroup = configs.memberAttributeReferencedInGroup
    settings.groupObjectClass = configs.groupObjectClass
    settings.groupAttributeHoldingMember = configs.groupAttributeHoldingMember
  }

  const mapping = Object.keys(configs.attributeMappings || {}).map((key) => ({ key, value: configs.attributeMappings[key] }))

  return (
    <div>
      <Title>LDAP Settings Confirmation</Title>

      <Description>
        All of the values have been successfully verified. Would you like to
        save the LDAP configuration?
      </Description>

      <Flexbox flexDirection='column'>
        <Flexbox flexDirection='row' justifyContent='space-between'>
          <Flexbox style={confirmationInfo} flexDirection='column'>
            <Info
              label='LDAP Function'
              value={useCaseMapping[configs.ldapUseCase]} />
            <Info
              label='Hostname'
              value={configs.hostname} />
            <Info
              label='Port'
              value={configs.port} />
            <Info
              label='Encryption Method'
              value={configs.encryption} />
            <Info
              label='Base User DN'
              value={configs.baseUserDn} />
            <Info
              label='User Name Attribute'
              value={configs.userNameAttribute} />
          </Flexbox>
          <Flexbox style={confirmationInfo} flexDirection='column'>
            <Info
              label='Base Group DN'
              value={configs.baseGroupDn} />
            <Info
              label='Bind User'
              value={configs.bindUser} />
            <Info
              label='Bind User Password'
              value='*****' />
            <Info
              label='Bind User Method'
              value={configs.bindUserMethod} />
            <Info
              visible={isAttrStore}
              label='LDAP Group Object Class'
              value={configs.groupObjectClass} />
            <Info
              visible={isAttrStore}
              label='Group Attribute Holding Member References'
              value={configs.groupAttributeHoldingMember} />
            <Info
              visible={isAttrStore}
              label='Member Attribute Referenced in Groups'
              value={configs.memberAttributeReferencedInGroup} />
          </Flexbox>
        </Flexbox>
        <MapDisplay visible={configs.ldapUseCase !== 'Authentication'}
          label='Attribute Mappings'
          mapping={configs.attributeMappings || {}} />
      </Flexbox>

      <Body>
        <Navigation>
          <Back onClick={prev} />
          <Finish
            onClick={() => {
              onStartSubmit()
              client.mutate(createLdapConfig(conn, info, settings, mapping))
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

export default withApollo(ConfirmStage)
