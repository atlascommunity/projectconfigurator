import {applyMiddleware, createStore} from "redux";
import thunk from "redux-thunk";
import {routerMiddleware} from "react-router-redux";
import {browserHistory} from "react-router";
import rootReducer from '../../commons/js/reducers';

export default createStore(
    rootReducer,
    applyMiddleware(routerMiddleware(browserHistory), thunk)
);