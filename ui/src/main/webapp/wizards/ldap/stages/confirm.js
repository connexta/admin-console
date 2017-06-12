import React from 'react'

import Flexbox from 'flexbox-react'

import { gql, withApollo } from 'react-apollo'

import Stage from 'components/Stage'
import Title from 'components/Title'
import Description from 'components/Description'
import Info from 'components/Information'
import Message from 'components/Message'
import MapDisplay from 'components/MapDisplay'

import Body from 'components/wizard/Body'
import Navigation, { Back, Finish } from 'components/wizard/Navigation'

import { confirmationInfo } from './styles.less'

const useCaseMapping = {
  authentication: 'Authentication source',
  attributeStore: 'Attribute store',
  authenticationAndAttributeStore: 'Authentication and attribute store'
}

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

    disabled,
    prev,
    submitting,
    messages = [],
    configs
  } = props

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
    groupObjectClass: configs.groupObjectClass,
    groupAttributeHoldingMember: configs.groupAttributeHoldingMember,
    memberAttributeReferencedInGroup: configs.memberAttributeReferencedInGroup,
    useCase: configs.ldapUseCase
  }

  const mapping = Object.keys(configs.attributeMappings).map((key) => ({ key, value: configs.attributeMappings[key] }))

  return (
    <Stage submitting={submitting}>
      <Title>LDAP Settings Confirmation</Title>

      <Description>
        All of the values have been successfully verified. Would you like to
        save the LDAP configuration?
      </Description>

      <Flexbox flexDirection='column'>
        <Flexbox flexDirection='row' justifyContent='space-between'>
          <Flexbox className={confirmationInfo} flexDirection='column'>
            <Info label='LDAP Function' value={useCaseMapping[configs.ldapUseCase]} />
            <Info label='Hostname' value={configs.hostname} />
            <Info label='Port' value={configs.port} />
            <Info label='Encryption Method' value={configs.encryption} />
            <Info label='Base User DN' value={configs.baseUserDn} />
            <Info label='User Name Attribute' value={configs.userNameAttribute} />
          </Flexbox>
          <Flexbox className={confirmationInfo} flexDirection='column'>
            <Info label='Base Group DN' value={configs.baseGroupDn} />
            <Info label='Bind User' value={configs.bindUser} />
            <Info label='Bind User Password' value='*****' />
            <Info label='Bind User Method' value={configs.bindUserMethod} />
            <Info label='LDAP Group Object Class' value={configs.groupObjectClass} />
            <Info label='Group Attribute Holding Member References'
              value={configs.groupAttributeHoldingMember} />
            <Info label='Member Attribute Referenced in Groups'
              value={configs.memberAttributeReferencedInGroup} />
          </Flexbox>
        </Flexbox>
        <MapDisplay visible={configs.ldapUseCase !== 'authentication'}
          label='Attribute Mappings'
          mapping={configs.attributeMappings} />
      </Flexbox>

      <Body>
        <Navigation>
          <Back
            onClick={prev}
            disabled={disabled} />
          <Finish
            onClick={() => {
              onStartSubmit()
              client.mutate(createLdapConfig(conn, info, settings, mapping))
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

export default withApollo(ConfirmStage)
