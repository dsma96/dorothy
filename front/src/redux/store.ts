import { configureStore, createSlice} from "@reduxjs/toolkit";


let user = createSlice({
        name:'user',
        initialState: {
            name:'anonymous',
            phone: '',
            email:''
        },
        reducers:{
            setUser(state, action){
                state.name = action.payload.name;
                state.phone = action.payload.phone;
                state.email = action.payload.email;
            }
        }
    });
export let {setUser} = user.actions;

export default configureStore({
    reducer: {
        user: user.reducer
    }
});