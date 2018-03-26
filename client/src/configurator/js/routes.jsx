if (process.env.NODE_ENV === 'production') {
    module.exports = require('./routes.prod').default;
} else {
    module.exports = require('./routes.dev').default;
}