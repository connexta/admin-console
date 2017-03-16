export default () => {
  var periscope = null

  const sub = (arg) => {
    if (periscope === null) {
      return arg
    } else {
      return periscope(arg)
    }
  }

  sub.init = (fn) => {
    if (typeof fn !== 'function') {
      throw Error('Submarine must be initialized with a function.')
    } else if (periscope !== null) {
      throw Error('Cannot initialize a submarine after it has already been initialized.')
    }
    periscope = fn
  }

  return sub
}
