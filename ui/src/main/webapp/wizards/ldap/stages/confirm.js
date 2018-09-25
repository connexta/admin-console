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

const createLdapConfig = (hosts, ldapLoadBalancing, info, settings, mapping) => ({
  mutation: gql`
    mutation CreateLdapConfig(
      $hosts: [LdapConnection],
      $ldapLoadBalancing: LdapLoadBalancing,
      $info: BindUserInfo,
      $settings: LdapDirectorySettings,
      $mapping: [ClaimsMapEntry]
    ) {
      createLdapConfig(config: {
        connections: $hosts,
        ldapLoadBalancing: $ldapLoadBalancing,
        bindInfo: $info,
        directorySettings: $settings,
        claimsMapping: $mapping
      })
    }
  `,
  variables: { hosts, ldapLoadBalancing, info, settings, mapping }
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
    errors: messages = [],
    configs
  } = props

  const isAttrStore = ldapUseCase === 'AuthenticationAndAttributeStore' || ldapUseCase === 'AttributeStore'

  const info = {
    creds: {
      username: configs.bindUser,
      password: configs.bindUserPassword
    },
    bindMethod: configs.bindUserMethod,
    realm: configs.bindRealm
  }

  const settings = {
    loginUserAttribute: configs.loginUserAttribute,
    memberAttributeReferencedInGroup: configs.memberAttributeReferencedInGroup,
    baseUserDn: configs.baseUserDn,
    baseGroupDn: configs.baseGroupDn,
    useCase: configs.ldapUseCase
  }

  if (isAttrStore) {
    settings.groupObjectClass = configs.groupObjectClass
    settings.groupAttributeHoldingMember = configs.groupAttributeHoldingMember
  }

  const mapping = Object.keys(configs.attributeMappings || {}).map((key) => ({ key, value: configs.attributeMappings[key] }))

  const hosts = Object.keys(configs.connectionInfo || {}).map((key, i) => ({
    hostname: configs.connectionInfo[key][0],
    port: configs.connectionInfo[key][1],
    encryption: configs.encryption
  }))

  const ldapLoadBalancing = configs.loadbalancing

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
              label='Host Connections'
              value={hosts.map(host => { return (host.hostname + ':' + host.port) })} />
            <Info
              label='Encryption Method'
              value={configs.encryption} />
            <Info
              label='Load Balancing Algorithm'
              value={configs.loadbalancing} />
            <Info
              label='Base User DN'
              value={configs.baseUserDn} />
            <Info
              label='User Login Attribute'
              value={configs.loginUserAttribute} />
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
              client.mutate(createLdapConfig(hosts, ldapLoadBalancing, info, settings, mapping))
                .then(() => {
                  onEndSubmit()
                  next('final-stage')
                })
                .catch((err) => {
                  onEndSubmit()
                  onError(err)
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
