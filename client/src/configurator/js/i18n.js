/* eslint-disable global-require,import/no-extraneous-dependencies,no-empty */
let i18nStrings = {};
try {
    i18nStrings = require('i18nStrings');
} catch (e) {
}
const AJS = require('AJS');

class I18n {
    constructor(data = {}) {
        this.data = data;
    }

    getText(key) {
        return this.data[key] || key;
    }

    getTextWithTwoParams(key, param1, param2) {
        return AJS.format(this.data[key], param1, param2) || key;
    }
}

export default new I18n(i18nStrings);
