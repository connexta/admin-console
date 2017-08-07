import React from 'react'
import muiThemeable from 'material-ui/styles/muiThemeable'

const styles = ({
  informationDiv: {
    textAlign: 'left',
    fontSize: '16px',
    lineHeight: '24px',
    width: '100%',
    display: 'inline-block',
    position: 'relative',
    height: '200ms'
  },
  informationLabel: {
    position: 'absolute',
    lineHeight: '22px',
    top: '30px',
    transform: 'scale(0.75) translate(0px, -28px)',
    transformOrigin: 'left top 0px'
  },
  informationP: {
    position: 'relative',
    height: '100%',
    margin: '28px 0px 7px',
    whiteSpace: 'nowrap'
  },
  informationListItemP: {
    position: 'relative',
    height: '100%',
    margin: '0px',
    whiteSpace: 'nowrap'
  }
})

const Information = ({ id, label, value, muiTheme }) => (
  <div style={styles.informationDiv}>
    <label style={{ color: muiTheme.palette.textColor, ...styles.informationLabel }}>{label}</label>
    { value instanceof Array
      ? (
        <div style={styles.informationP}>
          {value.map((text, i) => (
            <p key={i} style={{ color: muiTheme.palette.textColor, ...styles.informationListItemP }}>
              {text}
            </p>))}
        </div>
        )
      : (<p id={id} style={{ color: muiTheme.palette.textColor, ...styles.informationP }}>{value}</p>)
    }
  </div>
)

export default muiThemeable()(Information)
