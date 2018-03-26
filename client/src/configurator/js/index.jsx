/* eslint-disable no-undef */
import $ from 'AJS.$';
import React from 'react';
import ReactDOM from 'react-dom';
import { browserHistory, Router } from 'react-router';
import { Provider } from 'react-redux';
import { syncHistoryWithStore } from 'react-router-redux';
import { IntdevuiThemeProvider } from 'intdev-ui';
import getIntdevuiTheme from 'intdev-ui/lib/styles/getIntdevuiTheme';
import store from 'js/store';
import routes from '../../commons/js/routes';

const history = syncHistoryWithStore(browserHistory, store);

const render = () => {
    $(() => {
        ReactDOM.render(
            <Provider store={ store }>
                <IntdevuiThemeProvider intdevuiTheme={ getIntdevuiTheme({ zIndex: { layer: 3000 } }) }>
                    <Router history={ history }>
                        { routes }
                    </Router>
                </IntdevuiThemeProvider>
            </Provider>,
            document.querySelector('.project-configurator-container')
        );
    });
};

if (window.Raven && window.Raven.isSetup()) {
    window.Raven.wrap({
        tags: {
            plugin: 'projectconfigurator',
            module: 'configurator',
        },
    }, render)();
} else {
    render();
}
