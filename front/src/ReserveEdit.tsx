import * as React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import MuiCard from '@mui/material/Card';
import {styled, useTheme} from '@mui/material/styles';
import {
    CardActions,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    FormGroup
} from "@mui/material";
import FormControlLabel from "@mui/material/FormControlLabel";

import { AppProvider } from '@toolpad/core/AppProvider';
import type {Member} from 'type'
import { useSelector, useDispatch } from 'react-redux';
import {useNavigate} from "react-router";
import { useSearchParams } from "react-router-dom";
import moment from 'moment'
import TextField from "@mui/material/TextField";
import {HairService, Reservation} from "type";
import {useEffect, useState} from "react";
import {setUser} from "./redux/store";

const Card = styled(MuiCard)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(4),
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

export default function ReserveEdit(props: { disableCustomTheme?: boolean }) {
    const loginUser: Member = useSelector(state => state.user.loginUser);
    let [searchParams] = useSearchParams();
    const [openDialog, setOpenDialog] = useState(false);
    const [dialogTitle, setDialogTitle] = useState("Dorothy");
    const [ dialogMessage, setDialogMessage] = useState("");

    const  [reservation, setReservation] = React.useState< Reservation> ( {
        reservationId:-1,
        userName:'',
        startDate:'',
        createDate:'',
        status:'CREATED',
        services:[],
        editable:true,
        memo:''
    });

    useEffect(() => {
        if (searchParams.get("start")) {
            let startDateString = searchParams.get("start");
            setReservation(
                {
                    ...reservation,
                    startDate: startDateString
                }
            )
        }
        else if( searchParams.get("regId")){
            let regId = searchParams.get("regId");
            fetch(`/api/reserve/${regId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            })
                .then(response =>response.json())
                .then(
                    data => {
                        if( data.code == 200 ){
                            // dispatch( setUser( data.payload )) ;
                            // navigate(retUrl)
                            setReservation( data.payload );
                        }
                    }
                )
                .catch(error => console.error("Error:", error));
        }
    },[]);

    const handleCloseDialog=()=>{
        setOpenDialog(false);
        navigate(-1);
    }
    const saveReserve = (event) => {
        event.preventDefault();

        const reqDto = {
            startTime: reservation.startDate,
            designer: 1,
            memo: reservation.memo,
            serviceIds:[1]
        };

        fetch("/api/reserve/reservation", {
            method: "POST",
            body: JSON.stringify(reqDto),
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response =>response.json())
            .then(
                data => {
                    console.log("responseData : "+ JSON.stringify(data));
                    if( data.code == 200 ){
                        console.log("Registration Success!!");
                        setDialogTitle("Complete!");
                        setOpenDialog(true);
                        setDialogMessage( 'See you at ' + formatDate( data.payload.startDate));
                    }
                }
            )
            .catch(error => console.error("Error:", error));

    };

    const navigate = useNavigate();

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        const data = new FormData(event.currentTarget);
    };

    const formatPhoneNumber = (phoneNumberString: string) => {
        var cleaned = ('' + phoneNumberString).replace(/\D/g, '');
        var match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match) {
            return '(' + match[1] + ') ' + match[2] + '-' + match[3];
        }
        return null;
    }

    const formatDate = (dateStr: string) =>{
        return moment( dateStr,"YYYYMMDDTHH:mm").format('YYYY/MM/DD ddd HH:mm')
    }

    const handleMemoInput = e=>{
        setReservation({
            ...reservation,
            memo: e.target.value
        })
    }

    const theme = useTheme();
    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <SignUpContainer direction="column" justifyContent="space-between">
                <Card variant="outlined">
                    <img src={'./dorothy.png'} alt={'Dorothy Hairshop'}/>
                    <Typography
                        component="h1"
                        variant="h4"
                        sx={{width: '100%', fontSize: 'clamp(2rem, 10vw, 2.15rem)'}}
                    >
                        {reservation.reservationId > 0 ? 'Edit Reservation' : 'New Reservation'}
                    </Typography>
                    <Typography
                        component="h4"
                        variant="h4"
                        sx={{width: '100%', fontSize: 'clamp(1rem, 10vw, 1.15rem)'}}
                    >
                        {  formatDate( reservation.startDate )}

                    </Typography>

                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        sx={{display: 'flex', flexDirection: 'column', gap: 2}}
                    >
                        <Typography
                            component="h4"
                            variant="h4"
                            sx={{width: '100%', fontSize: 'clamp(1rem, 10vw, 1.15rem)'}}
                        >
                            {loginUser.name} { formatPhoneNumber( loginUser.phone)}
                        </Typography>
                        {/*<FormGroup>*/}
                        {/*    <FormControlLabel disabled control={<Checkbox defaultChecked />} label="남자헤어컷" />*/}
                        {/*    <FormControlLabel  control={<Checkbox />} label="샴푸+드라이" />*/}
                        {/*    <FormControlLabel  control={<Checkbox />} label="다운펌" />*/}
                        {/*    <FormControlLabel  control={<Checkbox />} label="세치염색" />*/}
                        {/*</FormGroup>*/}
                        <TextField
                            placeholder="Please leave your extra requirements"
                            multiline
                            rows={2}
                            maxRows={4}
                            value={reservation.memo}
                            onChange={handleMemoInput}
                        />
                        <CardActions >
                        <Button
                            type="submit"
                            size="large"
                            variant="contained"
                            onClick={saveReserve}
                            disabled={!reservation.editable}
                        >
                            Reserve
                        </Button>
                        <Button
                            size="large"
                            variant="contained"
                            onClick={() => navigate(-1)}
                            color="info"
                        >
                            Cancel
                        </Button>
                        </CardActions>
                    </Box>
                </Card>
                <Dialog
                    open={openDialog}
                    onClose={handleCloseDialog}
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