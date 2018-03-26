import React from 'react';
import { Route } from 'react-router';
import ProjectConfigurator from 'js/components/category/Categories';

export default (
    <Route path="/">
        <Route path="jira/projectConfigurator" component={ ProjectConfigurator } />
    </Route>
);
