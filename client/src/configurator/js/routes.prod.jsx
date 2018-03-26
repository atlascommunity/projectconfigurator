import React from 'react';
import { Route } from 'react-router';
import ProjectConfigurator from './components/configurator/ProjectConfigurator';

export default (
    <Route path="/">
        <Route path="jira/projectConfigurator" component={ ProjectConfigurator } />
    </Route>
);
