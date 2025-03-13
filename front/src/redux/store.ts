import { configureStore, createSlice} from "@reduxjs/toolkit";
let user = createSlice({
        name:'user',
        initialState: {
            loginUser:{
                name:'',
                phone: '',
                email:'',
                rootUser:false
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