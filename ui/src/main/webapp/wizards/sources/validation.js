export const isEmpty = (string) => {
  return !string
}

export const isBlank = (string) => {
  return !string || !string.trim()
}

export const nextShouldBeDisabled = ({ configs, discoveryType }) => {
  // checks that username & password are either both filled out or both empty (because it's optional)
  if (isBlank(configs.sourceUserName) !== isEmpty(configs.sourceUserPassword)) {
    return true
  }

  // hostname & port discovery checks
  if (discoveryType === 'hostnamePort') {
    if (isBlank(configs.sourceHostName) || configs.sourcePort < 0 || configs.sourcePort > 65535) {
      return true
    }
  }

  // url discovery checks
  if (discoveryType === 'url') {
    if (isBlank(configs.endpointUrl)) {
      return true
    }
  }

  return false
}

export const portError = ({ sourcePort }) => ((sourcePort === undefined || sourcePort < 0 || sourcePort > 65535) ? 'Port is not in valid range.' : undefined)
export const userNameError = ({ sourceUserName, sourceUserPassword }) => ((isBlank(sourceUserName) && !isEmpty(sourceUserPassword)) ? 'Password with no username.' : undefined)
export const passwordError = ({ sourceUserName, sourceUserPassword }) => ((!isBlank(sourceUserName) && isEmpty(sourceUserPassword)) ? 'Username with no password.' : undefined)

