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
            dateStr: new Date().toISOString(),
        },
        reducers:{
            setDateStr(state, action){
                state.dateStr = action.payload;
            },
        }
});

let availableServices: any = createSlice(
    {
        name: 'availableServices',
        initialState: {
            services:[] as HairService[]
        },
        reducers:{
            setAvailableServices(state, action){
                state.services = [...action.payload];
            },
        }
    }
);

let options = createSlice({
    name:'options',
    initialState: {
            services:[] as HairService[],
            attachments: [] as Object[],
            requireSilence: false,
            memo:''
    },
    reducers:{
        setAttachments(state, action){
          state.attachments = [...action.payload];
        },
        setRequireSilence(state, action){
            state.requireSilence = action.payload;
        },
        setMemo(state, action){
            state.memo = action.payload;
        },
        setSelectedServices(state, action){
            state.services = [...action.payload];
        },
    }
});

export let {setUser} = user.actions;
export let {setDateStr} = date.actions;
export let {setAvailableServices} = availableServices.actions;
export let {setAttachments, setRequireSilence, setMemo, setSelectedServices} = options.actions;

export default configureStore({
    reducer: {
        user: user.reducer,
        date: date.reducer,
        availableServices: availableServices.reducer,
        options: options.reducer,
    },
    devTools: process.env.NODE_ENV !== 'production'
});