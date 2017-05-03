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
    }
    periscope = fn
  }

  return sub
}
