

import { SET_USER } from "./actions";
import {combineReducers} from "redux";


const initialState = {
    loginUser : {}
}


// Use the initialState as a default value
const userReducer = (state = initialState, action) => {
    // The reducer normally looks at the action type field to decide what happens
    switch (action.type) {
        // Do something here based on the different types of actions
        case SET_USER:
            return {
                ...state,
                loginuser: action.payload
            }
        default:
            // If this reducer doesn't recognize the action type, or doesn't
            // care about this specific action, return the existing state unchanged
            return state
    }
}


export const  rootReducer = combineReducers({
    userReducer: userReducer
});