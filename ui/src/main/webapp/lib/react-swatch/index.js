import React from 'react'
import { Card, CardHeader, CardText } from 'material-ui/Card'
import { ChromePicker } from 'react-color'
import { getReadableTextColor } from './color-utils'
import Collapse from 'material-ui/svg-icons/navigation/expand-less'
import Expand from 'material-ui/svg-icons/navigation/expand-more'

export default ({ background, onChange, label }) => {
  const foreground = getReadableTextColor(background)

  return (
    <Card style={{ backgroundColor: background }}>
      <CardHeader
        openIcon={
          <Collapse color={foreground} />
        }
        closeIcon={
          <Expand color={foreground} />
        }
        showExpandableButton
        title={
          <span style={{ fontSize: '18px', color: foreground, fontWeight: 'bold', textTransform: 'uppercase', whiteSpace: 'nowrap', cursor: 'pointer' }}>
            {label}
          </span>
        }
        actAsExpander
      />
      <CardText expandable>
        <ChromePicker
          disableAlpha
          color={background}
          onChangeComplete={(color) => onChange(color.hex)} />
      </CardText>
    </Card>
  )
}
