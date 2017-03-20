# redux-submarine

Pattern for implementing sub-module selectors without the sub-modules knowing about the
structure of the entire state tree.

## usage

In the sub-module reducer, import redux-submarine, wrap it in a closure, and then use the submarine
in your selectors to get that module's  section of the state tree.

In its root reducer, import the submarine, call init, and pass it a function that takes state and
returns the submodule's section of the state tree.

```javascript
// root-reducer.js
import { combineReducers } from 'redux-immutable'

import subReducer, { submarine as subReducerSubmarine } from './sub-reducer.js'
// NOTE: a submarine can only be initialized once
subReducerSubmarine.init((state) => state.get('subReducer'))

export default combineReducers({ subReducer })
```

```javascript
// sub-reducer.js
import sub from 'redux-submarine'

export const submarine = sub()

export default (state = 0, action) => {
  return state
}

// to get the "subValue" reducer's value
export const stateSelector = (state) => submarine(state)
```