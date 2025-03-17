import { configureStore, createSlice} from "@reduxjs/toolkit";
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
            }
        }
});

export let {setUser} = user.actions;

export default configureStore({
    reducer: {
        user: user.reducer
    }
});