# react-visible

Easily wrap react components to make them visible/invisible.

## usage

```javascript
import visible from 'react-visible'

const MyComponent = visible(({ className }) => (
  <div className={className}></div>
))

// <MyComponent />                  - default visible
// <MyComponent visible />          - explicitly visible
// <MyComponent visible={false} />  - explicitly not visible

```