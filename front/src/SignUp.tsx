import * as React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CssBaseline from '@mui/material/CssBaseline';
import FormLabel from '@mui/material/FormLabel';
import FormControl from '@mui/material/FormControl';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import MuiCard from '@mui/material/Card';
import {styled, useTheme} from '@mui/material/styles';
import { AppProvider } from '@toolpad/core/AppProvider';
import { MuiTelInput } from 'mui-tel-input'
import {useNavigate} from "react-router";
import moment from 'moment'

import {
    CardActions,
    Container,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle
} from "@mui/material";

import {Member} from "./type";
import {setUser} from "./redux/store";
import {useEffect} from "react";
import useState from 'react-usestateref';
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

let EXPIRE_TIME = 300;

export default function SignUp(props: { disableCustomTheme?: boolean }) {

    const [emailError, setEmailError] = React.useState(false);
    const [emailErrorMessage, setEmailErrorMessage] = React.useState<string>('');
    const [telNoError, setTelNoError] = React.useState(false);
    const [telNoErrorMessage, setTelNoErrorMessage] = React.useState<string>('');
    const [passwordError, setPasswordError] = React.useState(false);
    const [passwordErrorMessage, setPasswordErrorMessage] = React.useState('');
    const [confirmPasswordError, setConfirmPasswordError] = React.useState(false);
    const [confirmPasswordErrorMessage, setConfirmPasswordErrorMessage] = React.useState<string>('');
    const [openDialog, setOpenDialog] = useState(false);
    const [dialogTitle, setDialogTitle] = useState<string>("Welcome");
    const [ dialogMessage, setDialogMessage] = useState<string>("");
    const [ createError, setCreateError] = useState(false);

    const [nameError, setNameError] = React.useState(false);
    const [nameErrorMessage, setNameErrorMessage] = React.useState<string>('');
    const [telNo, setTelNo] = React.useState<string>("");
    const [verified, setVerified] = React.useState(false);
    const [verifiStatus, setVerifiStatus, verifiStatusRef] = useState<string>('READY');
    const [verifiTimeout, setVerifiTimeout, verifiTimeoutRef] = useState(EXPIRE_TIME);
    const [verifiCode, setVerifiCode] = useState<string>('');
    const [verifyErrorMsg, setVerifyErrorMsg] = useState<string>('')

    const generateVerifiMessage : string = ()=>{
        if( verifiStatus == 'INPUT'){
            if( verifiTimeout > 0)
                return '인증 유효시간 : '+ verifiTimeout;
            else
                return '인증 유효시간이 만료되었습니다.';
        }
    }


    const navigate = useNavigate();

    useEffect(() => {
        if (process.env.NODE_ENV === 'development') {
            setTelNo("6474060362")
            EXPIRE_TIME = 10;
            setVerifiTimeout(EXPIRE_TIME);
        }

    },[]);


    const validateInputs = () => {
        if( !verified )
            return false;
        const email = document.getElementById('email') as HTMLInputElement;
        const password = document.getElementById('password') as HTMLInputElement;
        const confirmPassword = document.getElementById('confirmPassword') as HTMLInputElement;
        const name = document.getElementById('name') as HTMLInputElement;

        let isValid = true;

        if (!telNo || telNo.length != 10){
            setTelNoError(true);
            setTelNoErrorMessage('Please enter a valid phone number');
        }else {
            setTelNoError(false);
            setTelNoErrorMessage('')
        }

        if (email.value && !/\S+@\S+\.\S+/.test(email.value)) {
            setEmailError(true);
            setEmailErrorMessage('Please enter a valid email address.');
            isValid = false;
        } else {
            setEmailError(false);
            setEmailErrorMessage('');
        }

        if (!password.value || password.value.length < 6) {
            setPasswordError(true);
            setPasswordErrorMessage('Password must be at least 6 characters long.');
            isValid = false;
        } else {
            setPasswordError(false);
            setPasswordErrorMessage('');
        }


        if ( password.value != confirmPassword.value) {
            setConfirmPasswordError(true);
            setConfirmPasswordErrorMessage('confirm password should be same as password');
            isValid = false;
        } else {
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

        return isValid;
    };

    const handleChange = (value:string) => {
        if( value.length > 15)
            return;
        let phone = value.replaceAll(/(\+1)|[^0-9]+/g,"");

        setTelNo(phone);
    };

    const handleCloseDialog=()=>{
        setOpenDialog(false);

        if( !createError  )
            navigate(-1);
    }


    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {

        event.preventDefault();

        if (nameError || emailError || passwordError || confirmPasswordError || !verified) {
            return;
        }

        const currentEvent = new FormData(event.currentTarget);
        const data = {} as Member;
        data.phone = telNo;
        // @ts-ignore
        data.name = currentEvent.get('name');
        // @ts-ignore
        data.email = currentEvent.get('email') ? currentEvent.get('email') : null;
        // @ts-ignore
        data.password= currentEvent.get('password');


        fetch("/api/user/signup", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        setDialogTitle('Nice to meet you!');
                        setDialogMessage(`Welcome ${data.payload.name}. you can login now`);
                        setCreateError(false);
                        setOpenDialog(true);
                    }else{
                        setDialogTitle('Error');
                        setDialogMessage(`Can't Create. ${data.msg}`);
                        setCreateError(true);
                        setOpenDialog(true);
                    }
                }
            )
            .catch(
                error => {
                    console.error("Error:", error);
                    setDialogTitle('Error');
                    setDialogMessage(`Can't Create. %{data.msg}`);
                    setCreateError(true);
                }

            );
    };

    const verifiCountDownTimer = ()=>{
        if(verifiStatusRef.current == 'INPUT' &&  verifiTimeoutRef.current > 0 ){
            let i = verifiTimeoutRef.current - 1;
            setVerifiTimeout(i);
            setTimeout(()=>verifiCountDownTimer(),1000);
        }

    }

    const handleVerifyClick = ()=>{
        let data = {
            phoneNo: telNo
        }
        setVerifiCode('');
        fetch("/api/verify/request", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        setVerifiStatus ( 'INPUT');
                        let now = new Date();
                        let expiredDate = moment( data.payload.expireDate ,'YYYYMMDDTHH:mm:ss').toDate();
                        let timoutSec = Math.floor( (expiredDate.getTime() - now.getTime() )/ 1000);
                        setVerifiTimeout( timoutSec );
                        setTimeout(()=>verifiCountDownTimer(),1000);
                    }else{
                        console.error("Error:", data.msg);
                        setDialogTitle('Error');
                        setDialogMessage(`Can't Verify. %{data.msg}`);
                        setCreateError(true);
                    }
                }
            )
            .catch(
                error => {
                    console.error("Error:", error);
                    setDialogTitle('Error');
                    setDialogMessage(`Can't Verify. %{data.msg}`);
                    setCreateError(true);
                }
            );

    }

    const handleCancelVerification = ()=>{
        setVerifiTimeout(EXPIRE_TIME);
        setVerifiStatus('READY');
    }

    const handleVerifyStartClick=()=>{

        let data = {
            phoneNo: telNo,
            verifyCode: verifiCode
        }

        fetch("/api/verify/match", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        if(data.payload.state == 'VERIFIED'){
                            setVerifiStatus('VERIFIED');
                            setVerified(true);
                        }else {
                            setDialogTitle('Error');
                            setDialogMessage('code mismatch. please try again');
                            setOpenDialog(true);
                            setCreateError(true);
                        }
                    }else{
                    }
                }
            )
            .catch(
                error => {
                    console.error("Error:", error);
                    setDialogTitle('Error');
                    setDialogMessage(`Can't Verify. %{data.msg}`);
                    setCreateError(true);
                }

            );
    }

    const theme = useTheme();
    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <SignUpContainer direction="column" justifyContent="space-between">
                <Card variant="outlined" style={{overflowY:'scroll'}}>
                    <img src={'./dorothy.png'} alt={'Dorothy Hairshop'}/>
                    <Typography
                        component="h1"
                        variant="h4"
                        sx={{width: '100%', fontSize: 'clamp(1.6rem, 8vw, 1.8rem)'}}
                    >
                        회원 가입
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        sx={{display: 'flex', flexDirection: 'column', gap: 2}}
                    >
                        <FormControl>
                            <FormLabel htmlFor="name">Name</FormLabel>
                            <TextField
                                autoComplete="name"
                                name="name"
                                required
                                fullWidth
                                id="name"
                                placeholder="Jay M"
                                error={nameError}
                                helperText={nameErrorMessage}
                                color={nameError ? 'error' : 'primary'}
                            />
                        </FormControl>
                        <FormControl>
                            <FormLabel htmlFor="telNo">전화번호 </FormLabel>
                            <MuiTelInput
                                error={telNoError}
                                helperText={telNoErrorMessage}
                                defaultCountry="CA"
                                onlyCountries={['CA']}
                                disableDropdown={true}
                                forceCallingCode
                                id="telNo"
                                value={telNo}
                                onChange={handleChange}
                                autoFocus
                                required
                                fullWidth
                                autoComplete="current-telno"
                                placeholder="(416)123-4567"
                                variant="outlined"
                                color={telNoError ? 'error' : 'primary'}
                            />
                        </FormControl>
                        {
                            verifiStatus == 'INPUT'  &&
                            // <Typography
                            //     component="h1"
                            //     sx={{width: '100%'}}
                            // >
                            //     {generateVerifiMessage()}
                            // </Typography>

                            <FormLabel htmlFor="verifyInput">{generateVerifiMessage()}</FormLabel>
                        }
                            {
                                verifiStatus == 'READY' &&
                                <Button
                                    size="large"
                                    fullWidth
                                    variant="contained"
                                    onClick={handleVerifyClick}
                                    color="info"
                                    disabled={telNo.length != 10 }
                                >
                                    인증코드 요청
                                </Button>
                            }
                            {
                                verifiStatus == 'INPUT' &&

                                <Stack spacing={1} direction="row">
                                    <TextField id="verifyInput"
                                        type="number"
                                        disabled={ verifiTimeout == 0}
                                        value={verifiCode}
                                        onChange={e => setVerifiCode(e.target.value)}
                                    />
                                    <Button
                                        size="small"
                                        color="info"
                                        variant="contained"
                                        disabled={verifiTimeout == 0 || verifiCode.length != 4}
                                        onClick={handleVerifyStartClick}
                                    >
                                        인증
                                    </Button>
                                    <Button
                                        size="small"
                                        color="primary"
                                        variant="contained"
                                        onClick={handleCancelVerification}
                                    >
                                        취소
                                    </Button>
                                </Stack>
                            }
                            {
                                verified &&
                                <Typography
                                    component="h1"
                                    sx={{width: '100%'}}
                                >
                                    전화번호 인증완료
                                </Typography>
                            }

                        <FormControl>
                            <FormLabel htmlFor="email">Email (optional)</FormLabel>
                            <TextField
                                error={emailError}
                                helperText={emailErrorMessage}

                                fullWidth
                                name="email"
                                placeholder="email address"
                                type="email"
                                id="email"
                                variant="outlined"
                                color={emailError ? 'error' : 'primary'}
                            />

                        </FormControl>
                        <FormControl>
                            <FormLabel htmlFor="password">Password</FormLabel>
                            <TextField
                                required
                                fullWidth
                                name="password"
                                placeholder="password"
                                type="password"
                                id="password"
                                autoComplete="new-password"
                                variant="outlined"
                                error={passwordError}
                                helperText={passwordErrorMessage}
                                color={passwordError ? 'error' : 'primary'}
                            />
                        </FormControl>
                        <FormControl>
                            <FormLabel htmlFor="password">Password Confirm</FormLabel>
                            <TextField
                                required
                                fullWidth
                                name="confirmPassword"
                                placeholder="password confirm"
                                type="password"
                                id="confirmPassword"
                                autoComplete="new-password"
                                variant="outlined"
                                error={confirmPasswordError}
                                helperText={confirmPasswordErrorMessage}
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
                            disabled={!verified}
                        >
                            회원 가입
                        </Button>
                        <Button
                            size="large"
                            fullWidth
                            variant="contained"
                            onClick={() => navigate('/')}
                            color="info"
                        >
                            취소
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