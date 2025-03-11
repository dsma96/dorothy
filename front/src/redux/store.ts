import { configureStore, createSlice} from "@reduxjs/toolkit";
import {  Event} from 'react-big-calendar'
import type {Member} from 'type'

let user = createSlice({
        name:'user',
        initialState: {
            loginUser:{
                name:'',
                phone: '',
                email:'',
                isRootUser:false
            },
        },
        reducers:{
            setUser(state, action){
                state.loginUser.name = action.payload.name;
                state.loginUser.phone = action.payload.phone;
                state.loginUser.email = action.payload.email;
                state.loginUser.rootUser = (true === action.payload.rootUser);
            }
        }
});

export let {setUser} = user.actions;

export default configureStore({
    reducer: {
        user: user.reducer
    }
});