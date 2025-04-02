import * as React from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {setUser} from './redux/store';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CssBaseline from '@mui/material/CssBaseline';
import FormControlLabel from '@mui/material/FormControlLabel';
import Divider from '@mui/material/Divider';
import FormLabel from '@mui/material/FormLabel';
import FormControl from '@mui/material/FormControl';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import MuiCard from '@mui/material/Card';
import { styled } from '@mui/material/styles';
import { AppProvider } from '@toolpad/core/AppProvider';
import { useTheme } from '@mui/material/styles';
import { MuiTelInput } from 'mui-tel-input'

import {useNavigate} from "react-router";
import { useSearchParams} from "react-router-dom";
import type {Member} from './typedef';
import {useEffect, useState} from "react";

import {Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@mui/material";

const Card = styled(MuiCard)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(4),
    gap: theme.spacing(2),
    margin: 'auto',
    [theme.breakpoints.up('sm')]: {
        maxWidth: '450px',
    },
    boxShadow:
        'hsla(220, 30%, 5%, 0.05) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.05) 0px 15px 35px -5px',
    ...theme.applyStyles('dark', {
        boxShadow:
            'hsla(220, 30%, 5%, 0.5) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.08) 0px 15px 35px -5px',
    }),
}));

const SignInContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(2),
    [theme.breakpoints.up('sm')]: {
        padding: theme.spacing(4),
    },
    '&::before': {
        content: '""',
        display: 'block',
        position: 'absolute',
        zIndex: -1,
        inset: 0,
        backgroundImage:
            'radial-gradient(ellipse at 50% 50%, hsl(210, 100%, 97%), hsl(0, 0%, 100%))',
        backgroundRepeat: 'no-repeat',
        ...theme.applyStyles('dark', {
            backgroundImage:
                'radial-gradient(at 50% 50%, hsla(210, 100%, 16%, 0.5), hsl(220, 30%, 5%))',
        }),
    },
}));

interface LoginResponse {
    msg: string,
    code: number,
    data?: Member
};

export default function SignIn() {
    const [phoneError, setPhoneError] = React.useState(false);
    const [phoneErrorMessage, setPhoneErrorMessage] = React.useState('');
    const [passwordError, setPasswordError] = React.useState(false);
    const [passwordErrorMessage, setPasswordErrorMessage] = React.useState('');
    const [phone, setPhone] = React.useState("");
    const [password,setPassword] = React.useState("");
    let [searchParams] = useSearchParams();
    const loginUser: Member = useSelector( state => state.user.loginUser);
    const [openDialog, setOpenDialog] = useState(false);
    const dialogTitle = "Error";
    const [ dialogMessage, setDialogMessage] = useState("");
    let retUrl= "/";

    if( searchParams.get("ret"))
        retUrl += searchParams.get("ret")

    const getCookie = function(name) {
        var cookies = document.cookie.split(';');
        for(var i=0 ; i < cookies.length ; ++i) {
            var pair = cookies[i].trim().split('=');
            if(pair[0] == name)
                return pair[1];
        }
        return null;
    };

    useEffect(() => {
        if( loginUser.id < 0 ) {
            console.log("try to relogin with cookie")

            fetch("/api/login/relogin", {
                method: "GET"

            })
                .then(response => response.json())
                .then(
                    data => {
                        if (data.code == 200) {
                            dispatch(setUser(data.payload));
                        }
                    }
                )
                .catch( error => {
                        console.error("can't login with cookie:", error)
                        console.log("error!!!")
                    }
                );
        }else {
            navigate(retUrl);
        }
    },[loginUser]);

    const navigate = useNavigate();
    let dispatch = useDispatch();

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        console.log("handleSubmit");
        if (phoneError || passwordError) {
            event.preventDefault();
            return;
        }

        const data = {
            phone: phone,
            password: password
        }

        fetch("/api/login/login", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then(response =>response.json())
        .then(
                data => {
                    console.log("responseData : "+ JSON.stringify(data));
                    if( data.code == 200 ){
                      dispatch( setUser( data.payload )) ;
                      navigate(retUrl)
                    }else{
                        setDialogMessage( data.msg || 'Invalid UserName or Password' );
                        setOpenDialog(true);
                    }
                }
            )
            .catch(error => console.error("Error:", error));
        event.preventDefault();
    };

    const handleChange = (value:string) => {
        if( value.length > 15)
            return;
        let phone = value.replaceAll(/(\+1)|[^0-9]+/g,"");

        setPhone(phone);
    };

    const handlePasswordChange = (event) => {
        if( event.target.value.length > 20)
            return;

        setPassword(event.target.value);
    };

    const validateInputs = () => {
        const phoneNo = phone;

        let isValid = true;

        if (!phoneNo || phoneNo.length != 10) {
            setPhoneError(true);
            setPhoneErrorMessage('Please enter a valid phone number');
            isValid = false;
        } else {
            setPhoneError(false);
            setPhoneErrorMessage('');
        }

        if (!password || password.length < 5) {
            setPasswordError(true);
            setPasswordErrorMessage('Password must be at least 5 characters long.');
            isValid = false;
        } else {
            setPasswordError(false);
            setPasswordErrorMessage('');
        }

        return isValid;
    }
    const theme = useTheme();
    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <SignInContainer direction="column" justifyContent="space-between">
                <Card variant="outlined" style={{overflowY:'scroll'}}>
                    <img src={'./dorothy.png'} alt={'Dorothy Hairshop'}/>

                    <Typography component="h4" variant="h4" >
                        Sign in
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        noValidate
                        sx={{
                            display: 'flex',
                            flexDirection: 'column',
                            width: '100%',
                            gap: 2,
                        }}
                    >
                        <FormControl>
                            <FormLabel htmlFor="email">Phone Number</FormLabel>
                            <MuiTelInput
                                error={phoneError}
                                helperText={phoneErrorMessage}
                                defaultCountry="CA"
                                onlyCountries={['CA']}
                                disableDropdown={true}
                                forceCallingCode
                                id="phone"
                                value={phone}
                                onChange={handleChange}
                                autoFocus
                                required
                                fullWidth
                                autoComplete="current-telno"
                                variant="outlined"
                                color={phoneError ? 'error' : 'primary'}
                                size="small"
                            />
                        </FormControl>
                        <FormControl>
                            <FormLabel htmlFor="password">Password</FormLabel>
                            <TextField
                                error={passwordError}
                                helperText={passwordErrorMessage}
                                name="password"
                                placeholder="••••••"
                                defaultValue={password}
                                type="password"
                                id="password"
                                autoComplete="current-password"
                                autoFocus
                                required
                                fullWidth
                                variant="outlined"
                                color={passwordError ? 'error' : 'primary'}
                                onChange = { handlePasswordChange}
                                size="small"
                            />
                        </FormControl>


                        <Button
                            type="submit"

                            variant="contained"
                            onClick={validateInputs}
                            size="large"
                            disabled={phoneError || passwordError || phone.length < 10 || password.length < 6}
                        >
                            Sign in
                        </Button>
                        {/*<Link*/}
                        {/*    component="button"*/}
                        {/*    type="button"*/}
                        {/*    variant="body2"*/}
                        {/*    sx={{alignSelf: 'center'}}*/}
                        {/*>*/}
                        {/*    Forgot your password?*/}
                        {/*</Link>*/}
                    </Box>
                    <Divider>Don't you Have a Account?</Divider>
                    <Box sx={{display: 'flex', flexDirection: 'column', gap: 2}}>

                        <Button
                            size="medium"
                            color="info"
                            variant="contained"
                            onClick={()=>navigate("/signup")}
                        >
                            Sign Up / 회원가입
                        </Button>


                    </Box>

                </Card>
                <Dialog
                    open={openDialog}
                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                >
                <DialogTitle id="alert-dialog-title">
                    {dialogTitle}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        {dialogMessage}
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={()=>setOpenDialog(false)} autoFocus>
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>

        </SignInContainer>
        </AppProvider>
    );
}