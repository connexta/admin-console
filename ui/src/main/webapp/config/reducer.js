export const flip = () => ({ type: 'config/FLIP' })

import 'whatwg-fetch'

const url = '/admin/jolokia/read/org.codice.ddf.admin.application.service.ApplicationService:service=application-service/AllFeatures'

export const getAllFeature = () => (dispatch) => {
  window.fetch(url, { method: 'GET', credentials: 'same-origin' })
    .then((res) => res.json())
    .then((json) => {
      dispatch({ type: 'JSON', json })
    })
}

export default (state = true, { type, json }) => {
  switch (type) {
    case 'config/FLIP':
      return !state
    case 'JSON':
      return json
    default:
      return state
  }
}
