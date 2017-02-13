import React from 'react'
import { connect } from 'react-redux'
import Mount from 'react-mount'

import { fetchSystemUsageProperties } from './actions'
import { getSystemUsageProperties } from './reducer'

let Banners = ({ settings, fetchSystemUsageProperties, children }) => {
  if (settings && (settings.header || settings.footer)) {
    let bannerStyle = {
      position: 'absolute',
      height: '20px',
      width: '100%',
      textAlign: 'center',
      color: settings.textColor,
      backgroundColor: settings.style
    }

    return (
      <div>
        <div style={{...bannerStyle, top: 0}}>
          {settings.header}
        </div>
        <div style={{
          position: 'absolute',
          top: '20px',
          bottom: '20px',
          width: '100%',
          overflow: 'scroll'
        }}>
          { children }
        </div>
        <div style={{...bannerStyle, bottom: 0}}>
          {settings.footer}
        </div>
      </div>
    )
  } else {
    return (
      <div>
        <Mount
          on={fetchSystemUsageProperties}
          url='/admin/jolokia/exec/org.codice.ddf.ui.admin.api.ConfigurationAdmin:service=ui,version=2.3.0/getService/(service.pid=org.codice.admin.ui.configuration)' />
        {children}
      </div>
    )
  }
}

export default connect((state) => ({
  settings: getSystemUsageProperties(state)
}), {
  fetchSystemUsageProperties: fetchSystemUsageProperties
})(Banners)
