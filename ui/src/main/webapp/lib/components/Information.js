import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'
import visible from 'react-visible'

const styles = {
  div: {
    textAlign: 'left',
    fontSize: '16px',
    lineHeight: '24px',
    width: '100%',
    display: 'inline-block',
    position: 'relative',
    height: '200ms'
  },
  label: {
    position: 'absolute',
    lineHeight: '22px',
    top: '30px',
    transform: 'scale(0.75) translate(0px, -28px)',
    transformOrigin: 'left top 0px'
  },
  p: {
    position: 'relative',
    height: '100%',
    margin: '28px 0px 7px',
    whiteSpace: 'nowrap'
  },
  listItemP: {
    position: 'relative',
    height: '100%',
    margin: '0px',
    whiteSpace: 'nowrap'
  }
}

const Information = ({ id, label, value, muiTheme }) => (
  <div style={styles.div}>
    <label style={{ color: muiTheme.palette.textColor, ...styles.label }}>{label}</label>
    { value instanceof Array
      ? (
        <div style={styles.p}>
          {value.map((text, i) => (
            <p key={i} style={{ color: muiTheme.palette.textColor, ...styles.listItemP }}>
              {text}
            </p>))}
        </div>
        )
      : (<p id={id} style={{ color: muiTheme.palette.textColor, ...styles.p }}>{value}</p>)
    }
  </div>
)

export default visible(muiThemeable()(Information))
