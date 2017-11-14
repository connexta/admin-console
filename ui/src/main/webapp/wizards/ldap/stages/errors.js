export const groupErrors = (keys = [], errors = []) =>
  errors.reduce((o, err) => {
    const attr = err.path[err.path.length - 1]
    if (keys.includes(attr)) {
      o[attr] = err.message
    } else {
      o.messages.push(err)
    }
    return o
  }, { messages: [] })
