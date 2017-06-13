const friendlyMessage = {
  UNKNOWN_ENDPOINT: 'No supported sources were found at this location.',
  DUPLICATE_SOURCE_NAME: 'A source with that name already exists. Please choose a different name.',
  MISSING_REQUIRED_FIELD: 'There are missing required fields.',
  EMPTY_FIELD: 'There are empty required fields.',
  MISSING_KEY_VALUE: `There was a problem with the request to the server: MISSING_KEY_VALUE`,
  INVALID_PORT_RANGE: 'Invalid port number.',
  INVALID_HOSTNAME: 'Invalid hostname',
  INVALID_CONTEXT_PATH: 'Invalid context path.',
  FAILED_PERSIST: 'An error occurred while trying to save the configuration',
  UNSUPPORTED_ENUM: `There was a problem with the request to the server: UNSUPPORTED_ENUM`,
  CANNOT_CONNECT: 'Could not connect to the specified server.',
  FAILED_UPDATE_ERROR: 'The server failed to update the configuration',
  FAILED_DELETE_ERROR: 'The server failed to delete the configuration.',
  INVALID_URL_ERROR: 'Invalid URL.',
  NO_EXISTING_CONFIG: 'The configuration does not exist on the server.',
  INVALID_URI_ERROR: 'Invalid URI.',
  UNAUTHORIZED: 'Unauthorized. A valid username & password may be required to connect.'
}

export const genericMessage = (code) => `There was a problem with the request to the server: ${code}`

export const getFriendlyMessage = (code) => friendlyMessage[code] || genericMessage(code)
