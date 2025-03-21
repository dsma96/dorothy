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
import Divider from "@mui/material/Divider";


const MultilineTextField = styled(TextField)({

    '& .MuiInputBase-input::placeholder': {
        // Style the placeholder to appear multiline
        whiteSpace: 'pre-line', // Allows line breaks in the placeholder
    },
});


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

    const [openCancelDialog, setOpenCancelDialog] = useState(false);
    const cancelDialogTitle = "Cancel reservation";
    const cancelDialogMessage = "Would you like to cancel your reservation?";


    const  [reservation, setReservation] = React.useState< Reservation> ( {
        reservationId:-1,
        userName: loginUser.name,
        startDate:'',
        createDate:moment( new Date()).format("YYYYMMDDTHH:mm"),
        status:'CREATED',
        services:[],
        editable:true,
        memo:'',
        phone: loginUser.phone,
        requireSilence: false
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
                            console.log(JSON.stringify(data.payload));
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

    const closeCancelDialog=()=>{
        setOpenCancelDialog(false);
    }

    const cancelReservation = ()=>{
        fetch(`/api/reserve/cancel/${reservation.reservationId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        setDialogMessage("Your reservation has been successfully canceled.");
                        setOpenDialog(true);
                    }
                }
            )
            .catch(error => {
                    setDialogMessage(error.msg);
                    setOpenDialog(true);
                }
            );

    }


    const saveReserve = (event) => {
        event.preventDefault();

        let serviceIds: number[] = [];
        serviceIds.push(1);

        const reqDto = {
            startTime: reservation.startDate,
            designer: 1,
            memo: reservation.memo,
            serviceIds:serviceIds,
            requireSilence: reservation.requireSilence
        };
        let url = "/api/reserve/reservation";
        if( reservation.reservationId > 0 )
            url = url +"/"+reservation.reservationId;

        fetch(url, {
            method: reservation.reservationId > 0 ? "PUT" : "POST",
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
            .catch(error => {
                navigate(-1);
            });

    };

    const navigate = useNavigate();

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        const data = new FormData(event.currentTarget);
    };
    const handleMemoInput = e=>{
        setReservation({
            ...reservation,
            memo: e.target.value
        })
    }

    const handleCheckbox = (e)=>{
        console.log("handleCheckBox:"+reservation.requireSilence)
        reservation.requireSilence = !reservation.requireSilence;
        setReservation({
            ...reservation,
        });
    }



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


    const theme = useTheme();

    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <SignUpContainer direction="column" justifyContent="space-between">
                <Card variant="outlined" style={{overflowY:'scroll'}}>
                    <img src={'./dorothy.png'} alt={'Dorothy Hairshop'}/>
                    <Typography
                        component="h2"
                        variant="h4"
                        sx={{width: '100%', fontSize: 'clamp(1.5rem, 8vw, 2.0rem)'}}
                    >
                        {reservation.reservationId > 0 ? 'Edit Reservation' : 'New Reservation'}
                    </Typography>
                    <Divider/>
                    <Typography
                        component="h4"
                        variant="h4"
                        sx={{width: '100%', fontSize: 'clamp(1.5rem, 8vw, 2.0rem)'}}
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
                            {reservation.userName} { formatPhoneNumber( reservation.phone)}

                        </Typography>
                        <Typography
                            component="h4"
                            variant="h4"
                            sx={{width: '100%', fontSize: 'clamp(1rem, 10vw, 1.0rem)'}}
                        >
                            Created at { formatDate( reservation.createDate)}

                        </Typography>


                        <MultilineTextField
                            placeholder={"자르고 싶은 스타일을 적어 주세요\n디자이너에게 전달됩니다\n\n예)앞머리는 눈썹 아래로 덮히게 해주시고 구렛나루는 남기지말고 짧게 잘라주세요"}
                            multiline
                            rows={5}
                            value={reservation.memo}
                            onChange={handleMemoInput}
                        />

                        <FormControlLabel
                              control={
                                <Checkbox
                                    id='check_requireSilence'
                                    checked={reservation.requireSilence}
                                    disabled={!reservation.editable}
                                    onChange={handleCheckbox}
                              />}
                              label='조용히 시술받고 싶어요'
                        />
                        <Divider/>
                        <Typography
                            component="h4"
                            variant="h4"
                            sx={{width: '100%', fontSize: 'clamp(0.9rem, 9vw, 0.9rem)', lineHeight:1}}
                        >
                            390 Steeles Avenue West
                        </Typography>
                        <Typography
                            component="h4"
                            variant="h4"
                            sx={{width: '100%', fontSize: 'clamp(1rem, 9vw, 0.9rem)', lineHeight:1}}
                        >
                            K-Hair Studio, Designer Jay
                        </Typography>

                        <CardActions >
                        <Button
                            type="submit"
                            size="large"
                            variant="contained"
                            onClick={saveReserve}
                            disabled={!reservation.editable}
                        >
                            {reservation.reservationId > 0 ? 'Update' :  'Reservation'}
                        </Button>
                        <Button
                            size="large"
                            variant="contained"
                            onClick={()=>setOpenCancelDialog(true)}
                            color="info"
                            disabled={!reservation.editable || reservation.reservationId == -1}
                        >
                            Cancel
                        </Button>
                        <Button
                            size="large"
                            variant="contained"
                            onClick={() => navigate(-1)}
                            color="info"
                        >
                            Close
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

                <Dialog
                    open={openCancelDialog}

                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                >
                    <DialogTitle id="alert-dialog-title">
                        {cancelDialogTitle}
                    </DialogTitle>
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            {cancelDialogMessage}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={cancelReservation} autoFocus>
                            Yes
                        </Button>
                        <Button onClick={closeCancelDialog} autoFocus>
                            No
                        </Button>
                    </DialogActions>
                </Dialog>
            </SignUpContainer>
        </AppProvider>
    );
}