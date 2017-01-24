var webpack = require('webpack')
var validate = require('webpack-validator')
var merge = require('webpack-merge')
var path = require('path')
var fs = require('fs')
var glob = require('glob')
var HtmlWebpackPlugin = require('html-webpack-plugin')
var ExtractTextPlugin = require("extract-text-webpack-plugin")
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
    loaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel'
      },
      {
        test: /\.less$/,
        loader: ExtractTextPlugin.extract('style-loader', 'css?sourceMap&modules&importLoaders=1&localIdentName=[name]__[local]___[hash:base64:5]!less?sourceMap')
      },
      {
        test: /\.json$/,
        loader: 'json'
      }
    ]
  },
  resolve: {
    root: [
      path.resolve('./src/main/webapp/lib'),
      path.resolve('./node_modules')
    ]
  },
  plugins: [
    new webpack.ProvidePlugin({
      Promise: 'es6-promise'
    }),
    new ExtractTextPlugin("[name].css")
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
    plugins: [
      new webpack.DefinePlugin({
        'process.env.NODE_ENV': '"production"'
      }),
      new webpack.optimize.UglifyJsPlugin({
        output: {
          comments: false
        },
        compress: {
          drop_console: true,
          warnings: false
        }
      }),
      new StaticSiteGeneratorPlugin('main', ['/'], { html: html }, global)
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
        .map(function (spec) { return path.resolve(spec) }),
    plugins: [new HtmlWebpackPlugin()],
    module: {
      loaders: [
        {
          test: /spec\.js$/,
          loaders: [
            'mocha',
            path.resolve(__dirname, 'spec-loader.js')
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
    output: {
      publicPath: '',
      filename: 'bundle.js',
      path: path.resolve(__dirname, 'target', 'ci')
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
      loaders: [
        {
          test: /spec\.js$/,
          loader: 'mocha',
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
      './src/main/webapp'
    ],
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
      new webpack.HotModuleReplacementPlugin()
    ]
  })
}

module.exports = validate(config)
