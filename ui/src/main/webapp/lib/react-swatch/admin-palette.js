import React, { Component } from 'react'

import { Card, CardHeader } from 'material-ui/Card'
import SelectField from 'material-ui/SelectField'
import MenuItem from 'material-ui/MenuItem'

import Swatch from './'
import { getReadableTextColor } from './color-utils'

export default class extends Component {
  constructor (props) {
    super(props)
    this.state = ({ presetSelection: null })
  }

  changePreset (value) {
    this.setState({ presetSelection: value })
    this.props.setThemePreset(value)
  }

  render () {
    const {
      theme,
      updateColor
    } = this.props
    const {
      presetSelection
    } = this.state

    const styles = {
      card: {
        backgroundColor: theme.palette.backdropColor,
        padding: '5px',
        marginBottom: '10px'
      },
      cardHeader: {
        margin: '0px',
        fontSize: '18px',
        whiteSpace: 'nowrap',
        color: getReadableTextColor(theme.palette.backdropColor)
      }
    }

    return (
      <div style={{padding: '0px 5px'}}>
        <Card style={styles.card}>
          <CardHeader title={<p style={styles.cardHeader}>Labels & Buttons</p>} />
          <Swatch label='Primary' background={theme.palette.primary1Color} onChange={updateColor(['palette', 'primary1Color'])} />
          <Swatch label='Secondary' background={theme.palette.accent1Color} onChange={updateColor(['palette', 'accent1Color'])} />
          <Swatch label='Disabled' background={theme.palette.disabledColor} onChange={updateColor(['palette', 'disabledColor'])} />
          <Swatch label='Label Text' background={theme.palette.alternateTextColor} onChange={updateColor(['palette', 'alternateTextColor'])} />
        </Card>
        <Card style={styles.card}>
          <CardHeader title={<p style={styles.cardHeader}>Background & Text</p>} />
          <Swatch label='Text' background={theme.palette.textColor} onChange={updateColor(['palette', 'textColor'])} />
          <Swatch label='Canvas' background={theme.palette.canvasColor} onChange={updateColor(['palette', 'canvasColor'])} />
          <Swatch label='Backdrop' background={theme.palette.backdropColor} onChange={updateColor(['palette', 'backdropColor'])} />
        </Card>
        <Card style={styles.card}>
          <CardHeader title={<p style={styles.cardHeader}>Alerts & Messages</p>} />
          <Swatch label='Error' background={theme.palette.errorColor} onChange={updateColor(['palette', 'errorColor'])} />
          <Swatch label='Warning' background={theme.palette.warningColor} onChange={updateColor(['palette', 'warningColor'])} />
          <Swatch label='Success' background={theme.palette.successColor} onChange={updateColor(['palette', 'successColor'])} />
        </Card>
        <Card style={styles.card}>
          <CardHeader title={<p style={styles.cardHeader}>Tables</p>} />
          <Swatch label='Table Highlight' background={theme.palette.accent2Color} onChange={updateColor(['palette', 'accent2Color'])} />
          <Swatch label='Table Header' background={theme.palette.accent3Color} onChange={updateColor(['palette', 'accent3Color'])} />
          <Swatch label='Table Selected' background={theme.tableRow.selectedColor} onChange={updateColor(['tableRow', 'selectedColor'])} />
        </Card>
        <SelectField
          floatingLabelText='Presets'
          value={presetSelection}
          onChange={(e, i, value) => this.changePreset(value)}>
          <MenuItem value='Admin' primaryText='Admin' />
          <MenuItem value='Material' primaryText='Material' />
          <MenuItem value='Dark' primaryText='Dark' />
          <MenuItem value='Parrett' primaryText='Parrett' />
          <MenuItem value='Solarized Dark' primaryText='Solarized Dark' />
        </SelectField>
      </div>
    )
  }
}
