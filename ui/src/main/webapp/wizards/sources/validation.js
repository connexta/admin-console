import isURL from 'validator/lib/isURL'
import isFQDN from 'validator/lib/isFQDN'
import isIP from 'validator/lib/isIP'

export const isEmpty = (string) => {
  return !string
}

export const isBlank = (string) => {
  return !string || !string.trim()
}

export const discoveryStageDisableNext = ({ configs, discoveryType }) => {
  // checks that username & password are either both filled out or both empty (because it's optional)
  if (userNameError(configs) || passwordError(configs)) {
    return true
  }

  // hostname & port discovery checks
  if (discoveryType === 'hostnamePort') {
    if (isBlank(configs.sourceHostName) || hostnameError(configs) || portError(configs)) {
      return true
    }
  }

  // url discovery checks
  if (discoveryType === 'url') {
    if (isBlank(configs.endpointUrl) || urlError(configs)) {
      return true
    }
  }

  return false
}

export const hostnameError = ({ sourceHostName }) =>
  (!isBlank(sourceHostName) && !isFQDN(sourceHostName, { require_tld: false }) && !isIP(sourceHostName))
    ? 'Not a valid hostname or IP.' : undefined

export const urlError = ({ endpointUrl }) =>
  (!isBlank(endpointUrl) && !isURL(endpointUrl, { require_tld: false }))
    ? 'Not a valid url.' : undefined

export const portError = ({ sourcePort }) =>
  (sourcePort === undefined || sourcePort < 0 || sourcePort > 65535)
    ? 'Port is not in valid range.' : undefined

export const userNameError = ({ sourceUserName, sourceUserPassword }) =>
  (isBlank(sourceUserName) && !isEmpty(sourceUserPassword))
    ? 'Password with no username.' : undefined

export const passwordError = ({ sourceUserName, sourceUserPassword }) =>
  (!isBlank(sourceUserName) && isEmpty(sourceUserPassword))
    ? 'Username with no password.' : undefined
