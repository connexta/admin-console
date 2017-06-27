import { createWizard } from 'admin-wizard'

import IntroductionStage from './stages/introduction-stage'
import UseCaseStage from './stages/use-case-stage'
import LdapTypeSelection from './stages/ldap-type-selection'
import ConfigureEmbeddedLdap from './stages/configure-embedded-ldap'
import NetworkSettings from './stages/network-settings'
import BindSettings from './stages/bind-settings'
import DirectorySettings from './stages/directory-settings'
import LdapAttributeMappingStage from './stages/attribute-mapping'
import Confirm from './stages/confirm'
import FinalStage from './stages/final-stage'

export const stages = {
  'introduction-stage': IntroductionStage,
  'use-case-stage': UseCaseStage,
  'ldap-type-selection': LdapTypeSelection,
  'configure-embedded-ldap': ConfigureEmbeddedLdap,
  'network-settings': NetworkSettings,
  'bind-settings': BindSettings,
  'directory-settings': DirectorySettings,
  'attribute-mapping': LdapAttributeMappingStage,
  'confirm': Confirm,
  'final-stage': FinalStage
}

const configIds = [
  'baseGroupDn',
  'baseUserDn',
  'bindKdcAddress',
  'bindRealm',
  'bindUser',
  'bindUserMethod',
  'bindUserPassword',
  'encryption',
  'groupAttributeHoldingMember',
  'groupObjectClass',
  'hostname',
  'ldapType',
  'ldapUseCase',
  'memberAttributeReferencedInGroup',
  'port',
  'query',
  'queryBase',
  'subjectClaims',
  'userAttributes',
  'userNameAttribute'
]

export default createWizard('ldap', stages, configIds)
