import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { BrowserRouter, Routes, Route } from "react-router";
import './style.css'
import TimeTable from './TimeTable.tsx'
import ReserveEdit from './ReserveEdit.tsx'
import SignIn from "./Signin.tsx";
import SignUp from "./SignUp";
import Home from './Home';
import DateChoose from './DateChoose.tsx';
import {Provider} from 'react-redux';
import store from './redux/store';
import { worker } from './mocks/browser';
import EventHome from "./EventHome";
import MyInfo from "./myInfo";

if (process.env.NODE_ENV === 'development') {
    worker.start()
}

const theme = createTheme({
    palette: {
        primary: {
            main: '#e2c0ab', // Change this to your desired primary color
        },
        info: {
            main: '#e49a78', // Optional: Change the secondary color as well
        },
    },
});

createRoot(document.getElementById('root')!).render(
    <Provider store={store}>
        <BrowserRouter>
            <StrictMode>
                <ThemeProvider theme={theme}>
                <Routes>
                    <Route path="/home" element={<Home/>}></Route>
                    <Route path="/" element={<EventHome/>}></Route>
                    <Route path="/time" element={<TimeTable/>}></Route>
                    <Route path="/login" element={<SignIn/>}></Route>
                    <Route path="/reserve" element={<ReserveEdit/>}></Route>
                    <Route path="/signup" element={<SignUp/>}></Route>
                    <Route path="/my" element={<MyInfo/>}></Route>
                    <Route path="/dateChoose" element={<><DateChoose/></>}></Route>
                </Routes>
                </ThemeProvider>
            </StrictMode>
        </BrowserRouter>
    </Provider>
    ,
)

