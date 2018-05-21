/* eslint-disable global-require,import/no-extraneous-dependencies,no-empty */
let i18nStrings = {};
try {
    i18nStrings = require('i18nStrings');
} catch (e) {
}

class I18n {
    constructor(data = {}) {
        this.data = data;
    }

    getText(key) {
        return this.data[key] || key;
    }
}

export default new I18n(i18nStrings);
