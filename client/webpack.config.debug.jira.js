const path = require('path');
const webpack = require('webpack');
const BundleTracker = require('webpack-bundle-tracker');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
    devtool: 'cheap-eval-source-map',
    entry: {
        'configurator/react/app': [
            './src/configurator/js/index.jsx'
        ],
    },
    output: {
        path: path.join(__dirname, '../src/main/resources/ru/mail/jira/plugins/projectconfigurator/'),
        filename: '[name].bundle.js',
    },
    module: {
        rules: [
            {
                test: /(\.scss|\.css)$/,
                include: [
                    path.join(__dirname, 'src/configurator/js'),
                    path.join(__dirname, 'node_modules'),

                ],
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: 'css-loader?modules&importLoaders=1&localIdentName=[path]___[name]__[local]___[hash:base64:5]',
                }),
            },
            {
                test: /\.less$/,
                include: [
                    path.join(__dirname, 'node_modules'),
                ],
                loaders: [
                    'style-loader',
                    'css-loader',
                ],
            },
            {
                test: /\.(png|woff2?|eot|ttf)/,
                loaders: [
                    'url-loader?limit=100000&mimetype=image/png',
                ],
            },
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                loaders: [
                    'babel-loader',
                ],
            }
        ]
    },
    resolve: {
        modules: ['node_modules', path.join(__dirname, 'src'), path.join(__dirname, 'src/configurator')],
        extensions: ['.js', '.jsx']
    },
    plugins: [
        new webpack.optimize.OccurrenceOrderPlugin(),
        new webpack.DefinePlugin({
            'process.env': {
                NODE_ENV: JSON.stringify('development'),
            },
        }),
        new BundleTracker({ filename: './webpack-stats.json' }),
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NamedModulesPlugin(),
        new ExtractTextPlugin({
            filename: '[name].bundle.css',
            allChunks: true,
        }),
    ],
    externals: {
        i18nStrings: 'require("project-configurator/i18n")',
        AJS: 'AJS',
        'AJS.$': 'require("jquery")',
    },
    target: 'web',
    context: __dirname,
};
