import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'

import { BrowserRouter, Routes, Route } from "react-router";
import './style.css'
import TimeTable from './TimeTable.tsx'
import ReserveEdit from './ReserveEdit.tsx'
import { StyledEngineProvider } from '@mui/material/styles';
import Demo from './Demo';
import SignIn from "./Signin.tsx";
import SignUp from "./SignUp";
import Home from './Home';
import {Provider} from 'react-redux';
import store from './redux/store';
import { worker } from './mocks/browser';

if (process.env.NODE_ENV === 'development') {
    worker.start()
}

createRoot(document.getElementById('root')!).render(
    <Provider store={store}>
        <BrowserRouter>
            <StrictMode>
                <StyledEngineProvider injectFirst>
                    <Routes>
                        <Route path="/" element={<Home/>}></Route>
                        <Route path="/time" element={<TimeTable />}></Route>
                        <Route path="/login" element={<SignIn/>}></Route>
                        <Route path="/reserve" element={<ReserveEdit/>}></Route>
                        <Route path="/demo" element={<Demo/>}></Route>
                        <Route path="/signUp" element={<SignUp/>}></Route>
                    </Routes>
                </StyledEngineProvider>
            </StrictMode>
        </BrowserRouter>
    </Provider>
    ,
)

