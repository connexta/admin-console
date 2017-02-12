# admin-ui

The ui part of the admin-console.

## getting started

Before you start working on the ui, make sure all the dependencies are
installed. Simply do:

    yarn install

To start a development server, do:

    yarn start

This will load the ui in your browser and any updates to the source code
will automatically be pushed up to the browser.

## interesting places

- `./src/main/webapp`: where all of the JavaScript source code is.
- `./src/main/webapp/index.js`: the entry point into the `admin-ui`.
- `./src/main/resources/index.html`: the main html page for the app.
- `./target/webapp`: the build target for the `admin-ui`.
- `./target/webapp/bundle.js`: the compiled JavaScript file that gets
  loaded into the browser.

To find more interesting places, I suggest you inspect the
`webpack.config.js`.

## dev tools

To toggle the [redux devtools](https://github.com/gaearon/redux-devtools)
in the browser, do:

    ctrl+h

If you want the application state to persist across page reloads, add the
`debug_session` query parameter to the url. Example:

    http://localhost:8080?debug_session=my-session-name/#/

## tests

A test file can be located anywhere in `./src/main/webapp/`, as long as it
ends with `spec.js`. It is beneficial to co-locate tests near code they
exercise to suggest a close relationship, as well as making the `import`
statements much simpler.

To develop/debug tests in a browser, do:

    yarn run start:test

To run all tests as part of a CI build in a headless browser, do:

    yarn test

## helpful links

Some useful information to help team members get acquainted with the
technologies used in this project. Please add to it as you find more
learning resources.

- [Getting Started with Redux](https://egghead.io/courses/getting-started-with-redux)
- [Building React Applications with Idiomatic Redux](https://egghead.io/courses/building-react-applications-with-idiomatic-redux)

It is highly recommended that you familiarize yourself with the technology
stack before you start hacking on the admin-ui.

