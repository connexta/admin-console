# react-confirmable

Easily wrap react components to add an in-place confirmation dialog when they are clicked.

### description

Wrap any component declaration with `confirmable()` and the returned component will spawn an in-place
confirmation dialog that the user must confirm before its `onClick` action is called. If the user
denies, then the confirmation dialog will close and the original component will be displayed.


### properties
1. `onClick`: this method will be called when the user confirms the dialog
2. `confirmableMessage`: this message will be displayed on the confirmation dialog
3. `confirmableStyle`: styles to be applied to the confirmation dialog's outer div

Any additional properties will be passed to the wrapped component.

### usage
```javascript
import confirmable from 'react-confirmable'

const ConfirmableButton = confirmable(ButtonComponent)

(
  <ConfirmableButton
    onClick={() => {console.log('User confirmed!')}}
    confirmableMessage="Are you sure?"
    confirmableStyle={{ margin: '5px' }}
  />
)

```