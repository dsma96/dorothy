import {styled, useTheme} from "@mui/material/styles";
import MuiCard from "@mui/material/Card";
import Stack from "@mui/material/Stack";
import {HairService, Member, Reservation, UploadFile} from "./typedef";
import {useDispatch, useSelector} from "react-redux";
import moment from "moment";
import * as React from "react";
import Divider from "@mui/material/Divider";
import {AppProvider} from "@toolpad/core/AppProvider";
import CssBaseline from "@mui/material/CssBaseline";
import Header from "./components/Header";
import {CardActions, CardHeader, FormGroup, IconButton} from "@mui/material";
import Button from "@mui/material/Button";
import Footer from "./components/Footer";
import {useNavigate} from "react-router";
import DatePicker from "react-datepicker";
import './style.css'
import "react-datepicker/dist/react-datepicker.css";
import { registerLocale } from "react-datepicker";
import ko from "date-fns/locale/ko";
import {setDate, setDateStr} from "./redux/store";
import {useEffect, useState} from "react";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import TextField from "@mui/material/TextField"; // Import Korean locale
import {useDropzone} from 'react-dropzone';
import Box from "@mui/material/Box";
import CloseIcon from "@mui/icons-material/Close";
import ReservationHistoryTable from "./components/ReservationHistoryTable";


const thumbsContainer = {
    display: 'flex',
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginTop: 16
};

const thumb = {
    display: 'inline-flex',
    borderRadius: 2,
    border: '1px solid #eaeaea',
    marginBottom: 8,
    marginRight: 8,
    width: 100,
    height: 100,
    padding: 4,
    boxSizing: 'border-box'
};

const thumbInner = {
    display: 'flex',
    minWidth: 0,
    overflow: 'hidden'
};

const img = {
    display: 'block',
    width: 'auto',
    height: '100%'
};

const baseStyle = {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    padding: '0px',
    borderWidth: 0,
    borderRadius: 8,
    borderStyle: 'solid',
    backgroundColor: '#e2c0ab',
    outline: 'none',
    transition: 'border .24s ease-in-out'
};


const Card = styled(MuiCard)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    alignItems: 'flex-start',
    padding: theme.spacing(1),
    gap: theme.spacing(1),
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


const TimeSelectContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(2),
    justifyContent: 'flex-start',
    [theme.breakpoints.up('sm')]: {
        padding: theme.spacing(1),
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


export default function TimeSelect() {
    const theme = useTheme();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const selectedDateStr: string  = useSelector( state => state.date).dateStr;
    const loginUser: Member = useSelector(state => state.user.loginUser);
    const selectedService : HairService[] = useSelector(state => state.selectedServices.services);
    const [availableTimes, setAvailableTimes] = useState <Date[]>([]);
    const [selectedTime, setSelectedTime] = useState<Date | null>(null);

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
        requireSilence: false,
        userId: -1,
        tip:0
    });

    const DESIGNER_ID='1';

    if( loginUser == null || selectedService.length === 0 || selectedDateStr == null ){
       navigate('/dateChoose');
    }

    registerLocale("ko", ko);


    useEffect(() => {
        let startDateString = moment(selectedDateStr).format("YYYYMMDD");
        // need to load available service
        if( availableTimes.length ==0 ){
            fetch(`/api/reserve/availableSlots/${DESIGNER_ID}?date=${startDateString}&serviceIds=${selectedService.map(s => s.serviceId).join(',')}`)
                .then((response) => response.json())
                .then((data) => {
                    if( data.code == 200) {
                        const times = data.payload.map((time: string) => new Date(time));
                        setAvailableTimes(times);
                        console.log(JSON.stringify(times));
                    }
                })
                .catch((error) => {
                    console.error('Error fetching services:', error);
                });
        }

    },[selectedDateStr, selectedService]);


    function dateChanged(date: Date | null){
        if(date != null) {
            dispatch(setDateStr(date.toISOString()));
            setAvailableTimes([])
        }
        return;
    }


    const timeSlots = (
        <>
            <Stack spacing={2}>
                {availableTimes.reduce((rows: JSX.Element[][], date: Date, index: number) => {
                    const button = (
                        <Button
                            key={date.getTime()}
                            variant={selectedTime?.getTime() === date.getTime() ? "contained" : "outlined"}
                            onClick={() => setSelectedTime(date)}
                            style={{ minWidth: '80px', margin: '5px' }}
                        >
                            {moment(date).format('HH:mm')}
                        </Button>
                    );

                    if (index % 3 === 0) {
                        rows.push([button]); // Start a new row
                    } else {
                        rows[rows.length - 1].push(button); // Add to the current row
                    }

                    return rows;
                }, []).map((row, rowIndex) => (
                    <Stack key={rowIndex} direction="row" spacing={2} justifyContent="left">
                        {row}
                    </Stack>
                ))}
            </Stack>

        </>
    );




    return(
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <TimeSelectContainer direction="column" justifyContent="space-between">
                <Header
                />
                <DatePicker
                    showIcon
                    locale={ko}
                    selected={selectedDateStr}
                    onChange={dateChanged}
                    dateFormat="MM월 dd일"
                    minDate={new Date()}
                    maxDate={moment().add(14, 'days').toDate()}
                />
                <Card variant="outlined"    sx={{
                    marginTop: '20px', // Adjust this value to increase or decrease the gap
                }}
                >

                    <Divider/>

                    <FormGroup>
                        {timeSlots}
                        { availableTimes.length == 0 &&
                            <Typography
                            component="h4"
                            variant="h4"
                            sx={{width: '100%', fontSize: 'clamp(1.0rem, 7vw, 1.2rem)'}}
                            >
                            예약 가능한 시간이 없습니다.<br/>
                            다른 날짜를 선택해주세요
                            </Typography>
                        }
                    </FormGroup>
                <Divider/>
                    <CardActions
                        sx={{
                            position: 'fixed',
                            bottom: '60px', // Adjust this value to place it just above the Footer
                            left: 0,
                            right: 0,
                            display: 'flex',
                            justifyContent: 'center',
                            backgroundColor: '#fff', // Optional: Add a background color to match the design
                            zIndex: 2, // Ensure it stays above other content but below the Footer
                        }}
                    >
                        <Button

                            size="large"
                            variant="contained"
                            id={"reserveButton"}
                        >
                            예약
                        </Button>
                    </CardActions>
                </Card>

                <Footer backUrl="/dateChoose" showMyInfo={false}
                        style={{
                            position: 'fixed',
                            bottom: 0,
                            left: 0,
                            right: 0,
                            backgroundColor: '#fff',
                            zIndex: 1,
                        }}
                />

            </TimeSelectContainer>
        </AppProvider>
    );
}