var webpack = require('webpack')
var merge = require('webpack-merge')
var path = require('path')
var fs = require('fs')
var glob = require('glob')
var HtmlWebpackPlugin = require('html-webpack-plugin')
var ExtractTextPlugin = require('extract-text-webpack-plugin')
var StaticSiteGeneratorPlugin = require('static-site-generator-webpack-plugin')

var config = {
  output: {
    publicPath: '/',
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'target', 'webapp')
  },
  devtool: 'source-map',
  entry: ['babel-polyfill'],
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: [
          'babel-loader'
        ]
      },
      {
        test: /\.(css)$/,
        exclude: /node_modules/,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              sourceMap: true,
              modules: true,
              importLoaders: 1,
              localIdentName: '[name]__[local]___[hash:base64:5]'
            }
          }
        ]
      },
      {
        test: /\.css$/,
        include: /node_modules/,
        use: [
          'style-loader',
          'css-loader'
        ]
      }
    ]
  },
  resolve: {
    modules: [
      path.resolve('./src/main/webapp/lib'),
      'node_modules'
    ]
  },
  plugins: [
    new webpack.ProvidePlugin({
      Promise: 'es6-promise'
    })
  ]
}

if (process.env.NODE_ENV === 'production') {
  var html = fs.readFileSync('./src/main/resources/index.html', { encoding: 'utf8' })
  var global = {
    window: {
      SERVER_RENDER: true,
    }
  }
  config = merge.smart(config, {
    entry: ['./src/main/webapp'],
    output: {
      libraryTarget: 'umd'
    },
    module: {
      rules: [
        {
          test: /\.(css)$/,
          exclude: /node_modules/,
          loader: ExtractTextPlugin.extract({
            fallback: 'style-loader',
            use: [
              {
                loader: 'css-loader',
                options: {
                  sourceMap: true,
                  modules: true,
                  importLoaders: 1,
                  localIdentName: '[name]__[local]___[hash:base64:5]'
                }
              }
            ]
          })
        },
        {
          test: /\.css$/,
          include: /node_modules/,
          loader: ExtractTextPlugin.extract({
            fallback: 'style-loader',
            use: [
              'css-loader'
            ]
          })
        }
      ]
    },
    plugins: [
      new webpack.DefinePlugin({
        'process.env.NODE_ENV': '"production"'
      }),
      new webpack.optimize.UglifyJsPlugin({
        sourceMap: true,
        output: {
          comments: false
        },
        compress: {
          drop_console: true,
          warnings: false
        }
      }),
      new StaticSiteGeneratorPlugin('main', ['/'], { html: html }, global),
      new ExtractTextPlugin({ filename: "[name].css" })
    ]
  })
} else if (process.env.NODE_ENV === 'ci') {
  config = merge.smart(config, {
    devtool: 'source-map',
    node: {
      __filename: true
    },
    output: {
      publicPath: '',
      filename: 'bundle.js',
      path: path.resolve(__dirname, 'target', 'ci')
    },
    entry: glob.sync('./src/main/webapp/**/*spec.js')
      .concat('./src/main/webapp/app.js')
      .map(function (spec) { return path.resolve(spec) }),
    plugins: [
      new webpack.DefinePlugin({
        'process.env.NODE_ENV': '"ci"'
      }),
      new HtmlWebpackPlugin()
    ],
    module: {
      rules: [
        {
          test: /spec\.js$/,
          use: [
            'mocha-loader',
            path.resolve(__dirname, 'spec-loader.js'),
            'babel-loader'
          ],
          exclude: /(node_modules|target)/
        }
      ]
    },
    externals: {
      'react/addons': true,
      'react/lib/ExecutionEnvironment': true,
      'react/lib/ReactContext': true
    }
  })
} else if (process.env.NODE_ENV === 'test') {
  config = merge.smart(config, {
    devtool: 'source-map',
    node: {
      __filename: true
    },
    output: {
      publicPath: '',
      filename: 'bundle.js',
      path: path.resolve(__dirname, 'target', 'ci'),
      devtoolModuleFilenameTemplate: '~[resource-path]?[loaders]',
      devtoolFallbackModuleFilenameTemplate: '~[resource-path]?[loaders]'
    },
    entry: [
      'stack-source-map/register'
    ].concat(
      glob.sync('./src/main/webapp/**/*spec.js')
          .map(function (spec) { return path.resolve(spec) })
    ),
    devServer: {
      noInfo: true,
      contentBase: 'src/main/resources/',
      inline: true,
      compress: true,
      hot: true,
      host: '0.0.0.0',
      port: 8181
    },
    plugins: [
      new HtmlWebpackPlugin(),
      new webpack.HotModuleReplacementPlugin()
    ],
    module: {
      rules: [
        {
          test: /spec\.js$/,
          use: [
            'mocha-loader',
            path.resolve(__dirname, 'spec-loader.js'),
            'babel-loader'
          ],
          exclude: /(node_modules|target)/
        }
      ]
    },
    externals: {
      'react/addons': true,
      'react/lib/ExecutionEnvironment': true,
      'react/lib/ReactContext': true
    }
  })
} else {
  config = merge.smart(config, {
    entry: [
      'react-hot-loader/patch',
      'stack-source-map/register',
      './src/main/webapp'
    ],
    output: {
      devtoolModuleFilenameTemplate: '~[resource-path]?[loaders]',
      devtoolFallbackModuleFilenameTemplate: '~[resource-path]?[loaders]'
    },
    devServer: {
      noInfo: true,
      contentBase: 'src/main/resources/',
      inline: true,
      compress: true,
      hot: true,
      historyApiFallback: true,
      host: '0.0.0.0',
      proxy: {
        '/admin': {
          target: 'https://localhost:8993',
          secure: false
        }
      }
    },
    plugins: [
      new webpack.HotModuleReplacementPlugin(),
      new webpack.NamedModulesPlugin(),
      new webpack.NoEmitOnErrorsPlugin()
    ]
  })
}

module.exports = config
