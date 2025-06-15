import { configureStore, createSlice} from "@reduxjs/toolkit";
import {HairService} from  "../types/Types";

let user = createSlice({
        name:'user',
        initialState: {
            loginUser:{
                name:'',
                phone: '',
                email:'',
                rootUser:false,
                id:-1
            },
        },
        reducers:{
            setUser(state, action){
                state.loginUser.name = action.payload.name;
                state.loginUser.phone = action.payload.phone;
                state.loginUser.email = action.payload.email;
                state.loginUser.rootUser = (true === action.payload.rootUser);
                state.loginUser.id=action.payload.id;
            },
        }
});

let date = createSlice({
        name:'date',
        initialState: {
            date: new Date(),
        },
        reducers:{
            setDate(state, action){
                state.date = action.payload;
            },
        }
});

let availableServices: any = createSlice(
    {
        name: 'availableServices',
        initialState: {
            services:[]
        },
        reducers:{
            setAvailableServices(state, action){
                state.services = action.payload;
            },
        }
    }
);

export let {setUser} = user.actions;
export let {setDate} = date.actions;
export let {setAvailableServices} = availableServices.actions;

export default configureStore({
    reducer: {
        user: user.reducer,
        date: date.reducer,
        service: availableServices.reducer
    }
});