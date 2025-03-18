import * as React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CssBaseline from '@mui/material/CssBaseline';
import FormLabel from '@mui/material/FormLabel';
import FormControl from '@mui/material/FormControl';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import MuiCard from '@mui/material/Card';
import {styled, useTheme} from '@mui/material/styles';
import { AppProvider } from '@toolpad/core/AppProvider';
import { MuiTelInput } from 'mui-tel-input'
import {useNavigate} from "react-router";
import {CardActions, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@mui/material";
import {Member} from "./type";
import {setUser} from "./redux/store";
import {useState} from "react";
import {useSelector,useDispatch} from "react-redux";
import {Navigate} from "react-router-dom";
const Card = styled(MuiCard)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(1),
    gap: theme.spacing(2),
    margin: 'auto',
    boxShadow:
        'hsla(220, 30%, 5%, 0.05) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.05) 0px 15px 35px -5px',
    [theme.breakpoints.up('sm')]: {
        width: '450px',
    },
    ...theme.applyStyles('dark', {
        boxShadow:
            'hsla(220, 30%, 5%, 0.5) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.08) 0px 15px 35px -5px',
    }),
}));

const SignUpContainer = styled(Stack)(({ theme }) => ({
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

export default function MyInfo(props: { disableCustomTheme?: boolean }) {
    const [emailError, setEmailError] = React.useState(false);
    const [emailErrorMessage, setEmailErrorMessage] = React.useState('');

    const [currentPasswordError, setCurrentPasswordError] = React.useState(false);
    const [currentPasswordErrorMessage, setCurrentPasswordErrorMessage] = React.useState('');

    const [passwordError, setPasswordError] = React.useState(false);
    const [passwordErrorMessage, setPasswordErrorMessage] = React.useState('');
    const [confirmPasswordError, setConfirmPasswordError] = React.useState(false);
    const [confirmPasswordErrorMessage, setConfirmPasswordErrorMessage] = React.useState('');

    const [openDialog, setOpenDialog] = useState(false);
    const [dialogTitle, setDialogTitle] = useState("Welcome");
    const [ dialogMessage, setDialogMessage] = useState("");
    const [ updateError, setUpdateError] = useState(false);

    const [ nameError, setNameError] = React.useState(false);
    const [ nameErrorMessage, setNameErrorMessage] = React.useState('');
    const [ validInput, setValidInput] = React.useState(false);
    const [ changed, setChanged ] = React.useState(false);

    const navigate = useNavigate();
    let dispatch = useDispatch();
    const loginUser: Member = useSelector( state => state.user.loginUser);


    if( loginUser.id < 0){
        return <Navigate to ="/login?ret=my"/>
    }


    const validateInputs = () => {

        const email = document.getElementById('email') as HTMLInputElement;
        const currentPassword = document.getElementById('currentPassword') as HTMLInputElement;
        const password = document.getElementById('password') as HTMLInputElement;
        const confirmPassword = document.getElementById('confirmPassword') as HTMLInputElement;
        const name = document.getElementById('name') as HTMLInputElement;

        let isValid = true;

        if (email.value && !/\S+@\S+\.\S+/.test(email.value)) {
            setEmailError(true);
            setEmailErrorMessage('Please enter a valid email address.');
            isValid = false;
        } else {
            setEmailError(false);
            setEmailErrorMessage('');
        }

        if( !currentPassword.value || currentPassword.value.length < 6 ){
            setCurrentPasswordError(true);
            setCurrentPasswordErrorMessage("Please input your current password")
            isValid = false;
        }else{
            setCurrentPasswordError(false);
            setCurrentPasswordErrorMessage('');
        }

        if( password.value.length > 0 || confirmPassword.value.length > 0 ) {
            if ( !password.value || password.value.length < 5) {
                setPasswordError(true);
                setPasswordErrorMessage('Password must be at least 6 characters long.');
                isValid = false;
            } else {
                setPasswordError(false);
                setPasswordErrorMessage('');
            }

            if ( password.value != confirmPassword.value ) {
                setConfirmPasswordError(true);
                setConfirmPasswordErrorMessage('confirm password should be same as password');
                isValid = false;
            } else {
                setConfirmPasswordError(false);
                setConfirmPasswordErrorMessage('');
            }
        }else{
            setPasswordError(false);
            setPasswordErrorMessage('');
            setConfirmPasswordError(false);
            setConfirmPasswordErrorMessage('');

        }

        if (!name.value || name.value.length < 2) {
            setNameError(true);
            setNameErrorMessage(!name.value ? 'Name is required.' : 'name is too short');
            isValid = false;
        } else {
            setNameError(false);
            setNameErrorMessage('');
        }


        setValidInput(isValid);

        return isValid;
    };


    const handleCloseDialog=()=>{
        setOpenDialog(false);
        if( !updateError )
            navigate(-1);
    }


    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (nameError || emailError || passwordError || confirmPasswordError) {
            return;
        }

        const currentEvent = new FormData(event.currentTarget);
        const data = {} as Member;
        data.name = currentEvent.get('name');
        data.email = currentEvent.get('email') ? currentEvent.get('email') : null;
        data.newPassword= currentEvent.get('password');
        data.password = currentEvent.get('currentPassword');

        fetch(`/api/user/${loginUser.id}`, {
            method: "PUT",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        dispatch(setUser(data.payload));
                        setDialogTitle('Success');
                        setDialogMessage(`Your changes were updated!`);
                        setUpdateError(false);
                        setOpenDialog(true);
                    }
                }
            )
            .catch(
                error => {
                    console.error("Error:", error);
                    setDialogTitle('Error');
                    setDialogMessage(`Can't Create. %{data.msg}`);
                    setUpdateError(true);
                    setOpenDialog(true);
                }

            );

    };

    const theme = useTheme();
    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <SignUpContainer direction="column" justifyContent="space-between">
                <Card variant="outlined" style={{overflowY:'scroll'}}>
                    <Typography
                        component="h3"
                        variant="h3"
                        sx={{width: '100%', fontSize: 'clamp(1.5rem, 7vw, 1.7rem)'}}
                    >
                        My Info
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        sx={{display: 'flex', flexDirection: 'column', gap: 2}}
                    >
                        <FormControl>
                            <FormLabel htmlFor="name">Name</FormLabel>
                            <TextField
                                required
                                autoComplete="name"
                                name="name"
                                fullWidth
                                id="name"
                                defaultValue={loginUser.name}
                                error={nameError}
                                helperText={nameErrorMessage}
                                onChange={ e=> {setChanged(true); validateInputs()}}
                                color={nameError ? 'error' : 'primary'}
                            />
                        </FormControl>

                        <FormControl>
                            <FormLabel htmlFor="telNo">Tel No</FormLabel>
                            <MuiTelInput
                                defaultCountry="CA"
                                onlyCountries={['CA']}
                                disableDropdown={true}
                                forceCallingCode
                                id="telNo"
                                value={loginUser.phone}
                                disabled
                                fullWidth
                                variant="outlined"
                                color={emailError ? 'error' : 'primary'}
                            />
                        </FormControl>


                        <FormControl>
                            <FormLabel htmlFor="email">Email (optional)</FormLabel>
                            <TextField
                                error={emailError}
                                helperText={emailErrorMessage}
                                defaultValue={loginUser.email}
                                fullWidth
                                name="email"
                                onChange={  e=> {setChanged(true); validateInputs()}}
                                type="email"
                                id="email"
                                variant="outlined"
                                color={emailError ? 'error' : 'primary'}
                            />

                        </FormControl>
                        <FormControl>
                            <FormLabel htmlFor="password">Current Password</FormLabel>
                            <TextField
                                required
                                fullWidth
                                name="currentPassword"
                                type="password"
                                placeholder="Current Password"
                                id="currentPassword"
                                autoComplete="new-password"
                                variant="outlined"
                                error={currentPasswordError}
                                helperText={currentPasswordErrorMessage}
                                onChange={ e=>validateInputs()}
                                color={passwordError ? 'error' : 'primary'}
                            />
                        </FormControl>

                        <FormControl>
                            <FormLabel htmlFor="password">New Password</FormLabel>
                            <TextField
                                fullWidth
                                name="password"
                                placeholder="New Password"
                                type="password"
                                id="password"
                                autoComplete="new-password"
                                variant="outlined"
                                error={passwordError}
                                helperText={passwordErrorMessage}
                                onChange={  e=> {setChanged(true); validateInputs()}}
                                color={passwordError ? 'error' : 'primary'}
                            />
                        </FormControl>
                        <FormControl>
                            <FormLabel htmlFor="password">New Password Confirm</FormLabel>
                            <TextField
                                fullWidth
                                name="confirmPassword"
                                placeholder="New Password confirm"
                                type="password"
                                id="confirmPassword"
                                autoComplete="new-password"
                                variant="outlined"
                                error={confirmPasswordError}
                                helperText={confirmPasswordErrorMessage}
                                onChange={  e=> {setChanged(true); validateInputs()}}
                                color={confirmPasswordError ? 'error' : 'primary'}
                            />
                        </FormControl>

                        <CardActions >
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                onClick={validateInputs}
                                size="large"
                                disabled={ !changed || !validInput }
                            >
                                Save
                            </Button>
                            <Button
                                size="large"
                                fullWidth
                                variant="contained"
                                onClick={() => navigate('/')}
                                color="info"
                            >
                                Cancel
                            </Button>
                        </CardActions>
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
                        <Button onClick={handleCloseDialog} autoFocus>
                            Confirm
                        </Button>

                    </DialogActions>
                </Dialog>
            </SignUpContainer>
        </AppProvider>
    );
}