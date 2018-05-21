import {applyMiddleware, createStore} from 'redux';
import thunk from 'redux-thunk';
import {routerMiddleware} from 'react-router-redux';
import {browserHistory} from 'react-router';

import {projectConfiguratorReducer} from './project.configurator.reducer';
import {ProjectConfiguratorService} from './project.configurator.service';


export const projectConfiguratorService = ProjectConfiguratorService;

export const store = createStore (
    projectConfiguratorReducer,
    applyMiddleware(routerMiddleware(browserHistory), thunk)
);
