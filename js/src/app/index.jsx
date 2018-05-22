/* eslint-disable no-undef */
import React from 'react';
import ReactDOM from 'react-dom';
import { browserHistory, Router, Route } from 'react-router';
import { Provider } from 'react-redux';
import { syncHistoryWithStore } from 'react-router-redux';

import LayerManager from '@atlaskit/layer-manager';

import ProjectConfigurator from './ProjectConfigurator';

import { store } from '../service/store';
import './projectconfigurator.less';


const history = syncHistoryWithStore(browserHistory, store);

$(() => {
    ReactDOM.render(
        <Provider store={ store }>
            <LayerManager>
                <Router history={ history }>
                    <Route path="/">
                        <Route path="projectConfigurator" component={ ProjectConfigurator } />
                    </Route>
                </Router>
            </LayerManager>
        </Provider>,
        document.querySelector('.project-configurator-container')
    );
});
