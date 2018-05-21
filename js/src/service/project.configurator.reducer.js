import { routerReducer as routing } from 'react-router-redux';
import {combineReducers} from 'redux';


export const projectConfiguratorReducer = combineReducers({
    routing,
    screenSchemeReducer: screenSchemeReducer
});

const SELECT_SCREEN_SCHEME = 'SELECT_SCREEN_SCHEME';
const ADD_SCREEN_SCHEME = 'ADD_SCREEN_SCHEME';
const UPDATE_SCREEN_SCHEMES = 'UPDATE_SCREEN_SCHEMES';

export const ScreenSchemeActionCreators = {
    selectScreenScheme: (selectedScreenScheme) => {
        return {
            type: SELECT_SCREEN_SCHEME,
            selectedScreenScheme
        };
    },
    addScreenScheme: (issueType, selectedScreenScheme) => {
        return {
            type: ADD_SCREEN_SCHEME,
            issueType,
            selectedScreenScheme
        };
    },
    updateScreenSchemes: (selectedScreenSchemes) => {
        return {
            type: UPDATE_SCREEN_SCHEMES,
            selectedScreenSchemes
        };
    }
};

function screenSchemeReducer(state = {selectedScreenScheme: null, selectedScreenSchemes: {}}, action) {
    switch (action.type) {
        case SELECT_SCREEN_SCHEME:
            return {
                ...state,
                selectedScreenScheme: action.selectedScreenScheme
            };
        case ADD_SCREEN_SCHEME:
            const selectedScreenSchemes = state.selectedScreenSchemes;
            selectedScreenSchemes[action.issueType.id] = action.selectedScreenScheme;

            return {
                ...state,
                selectedScreenSchemes: selectedScreenSchemes,
                selectedScreenScheme: null
            };
        case UPDATE_SCREEN_SCHEMES:
            return {
                ...state,
                selectedScreenSchemes: action.selectedScreenSchemes,
            };
        default:
            return state;
    }
}
