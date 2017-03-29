import color from 'color'

export const getReadableTextColor = (backgroundColor) => {
  return color(backgroundColor).light() ? 'rgba(0, 0, 0, 0.5)' : 'rgba(255, 255, 255, 0.5)'
}

export const getReadableTextColorOpaque = (backgroundColor) => {
  return color(backgroundColor).light() ? '#000000' : '#FFFFFF'
}
