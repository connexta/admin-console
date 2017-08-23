import React from 'react'

const globalStyles = `
  body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
    font-size: 18px;
    margin: 0;
    color: #333333;
    background: #ffffff;
  }
  a {
    text-decoration: none;
  }
  input::-ms-clear {
    display:none;
  }
`

const styles = {
  loading: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    top: 0
  },
  inner: {
    position: 'relative',
    height: '100%'
  },
  header: {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%,-100%)',
    fontSize: 80,
    fontWeight: 'bold',
    textAlign: 'center'
  }
}

export default ({ assets, children }) => {
  const bundles = assets.filter((asset) => asset.match(/bundle.*js$/))
    .concat('/admin/iframe-resizer/2.6.2/js/iframeResizer.contentWindow.min.js')
  const css = assets.filter((asset) => asset.match(/.*css$/))

  return (
    <html>
      <head>
        <title>Admin Console</title>
        <meta charset='UTF-8' />
        <meta http-equiv='x-ua-compatible' content='IE=Edge' />
        <meta name='viewport' content='width=device-width, initial-scale=1' />
        <style>{globalStyles}</style>
        {css.map((name) =>
          <link rel='stylesheet' type='text/css' href={name} />)}
      </head>
      <body>
        <div id='root'>
          {children === undefined
            ? <div style={styles.loading}>
              <div style={styles.inner}>
                <h1 style={styles.header}>Loading...</h1>
              </div>
            </div> : children}
        </div>
        {bundles.map((name, i) =>
          <script key={i} type='text/javascript' src={name} />)}
      </body>
    </html>
  )
}
