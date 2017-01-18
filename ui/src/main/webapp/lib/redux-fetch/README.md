# redux-fetch

A simple redux async action that wrapps the fetch api.

# usage

```javascript
// actions.js
import { post } from 'redux-fetch'

const myAsyncAction = () => (dispatch) => {
  const body = JSON.stringify({ key: 'value' })
  const res = await dispatch(post('/my-url', { id, body }))
  // use the response
}
```
