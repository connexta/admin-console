const friendlyMessage = {
  UNKNOWN_ENDPOINT: 'No supported sources were found at this location.',
  DUPLICATE_SOURCE_NAME: 'A source with that name already exists. Please choose a different name.',
  MISSING_REQUIRED_FIELD: 'Missing required field.',
  EMPTY_FIELD: 'Empty required field.',
  MISSING_KEY_VALUE: 'There was a problem with the request to the server: MISSING_KEY_VALUE',
  INVALID_PORT_RANGE: 'Invalid port number.',
  INVALID_HOSTNAME: 'Invalid hostname',
  INVALID_CONTEXT_PATH: 'Invalid context path.',
  FAILED_PERSIST: 'An error occurred while trying to save the configuration',
  UNSUPPORTED_ENUM: 'There was a problem with the request to the server: UNSUPPORTED_ENUM',
  CANNOT_CONNECT: 'Could not connect to the specified server.',
  INVALID_URL: 'Invalid URL.',
  NO_EXISTING_CONFIG: 'The configuration does not exist on the server.',
  INVALID_URI: 'Invalid URI.',
  UNAUTHORIZED: 'Unauthorized. A valid username & password may be required to connect.',
  CANNOT_BIND: 'Cannot authenticate user.',
  INVALID_DN: 'The distinguished name has an invalid format.',
  INVALID_QUERY: 'The provided query is invalid.',
  DN_DOES_NOT_EXIST: 'The distinguished name does not exist.',
  NO_USERS_IN_BASE_USER_DN: 'Could not find any users in the base user dn.',
  NO_GROUPS_IN_BASE_GROUP_DN: 'Could not find any groups in the base group dn.',
  NO_GROUPS_WITH_MEMBERS: 'No groups were found containing any members.',
  NO_REFERENCED_MEMBER: 'Unable to find a user with the member attribute specified for groups.',
  USER_ATTRIBUTE_NOT_FOUND: 'Could not find attribute on any users.'
}

export const genericMessage = (code) => `There was a problem with the request to the server: ${code}`

export const getFriendlyMessage = (code) => friendlyMessage[code] || genericMessage(code)
