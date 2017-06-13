export const changeStage = (stageId) => ({ type: 'SOURCES/CHANGE_STAGE', stage: stageId })
export const startSubmitting = () => ({ type: 'SOURCES/START_SUBMITTING' })
export const endSubmitting = () => ({ type: 'SOURCES/END_SUBMITTING' })
export const setDiscoveryType = (value) => ({ type: 'SOURCES/SET_DISCOVERY_TYPE', value })

export const setDiscoveredEndpoints = (endpointConfigs) => ({ type: 'SOURCES/SET_DISCOVERED_ENDPOINTS', endpointConfigs })
export const setChosenEndpoint = (endpointKey) => ({ type: 'SOURCES/SET_CHOSEN_ENDPOINT', endpointKey })

export const setErrors = (stageId, errorList) => ({ type: 'SOURCES/SET_ERRORS', stageId, errorList })
export const clearErrors = () => ({ type: 'SOURCES/CLEAR_ERRORS' })
