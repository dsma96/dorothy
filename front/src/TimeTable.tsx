import { FC, useState, useCallback, useEffect  } from 'react'
import { Calendar, dateFnsLocalizer, Event,momentLocalizer , BigCalendar} from 'react-big-calendar'
import {format} from 'date-fns/format'
import parse from 'date-fns/parse'
import startOfWeek from 'date-fns/startOfWeek'
import getDay from 'date-fns/getDay'
import enUS from 'date-fns/locale/en-US'
import SavingsIcon from '@mui/icons-material/Savings';
import HomeIcon from '@mui/icons-material/Home';
import ArrowBackIosIcon from '@mui/icons-material/ArrowBackIos';
import AccountBoxIcon from '@mui/icons-material/AccountBox';

import { AppProvider } from '@toolpad/core/AppProvider';
import {styled, useTheme} from '@mui/material/styles';
import * as React from 'react';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import   'moment-timezone';

import type {Member} from 'type'
import { useSelector, useDispatch } from 'react-redux';
import 'react-big-calendar/lib/addons/dragAndDrop/styles.css'
import 'react-big-calendar/lib/css/react-big-calendar.css'
import {Card, Snackbar} from "@mui/material";
import {useNavigate} from "react-router";
import {Navigate} from 'react-router-dom';
import Stack from "@mui/material/Stack";
import  './App.css'
import Typography from "@mui/material/Typography";
import moment from 'moment'
import {setUser} from "./redux/store";
const defaultTZ = moment.tz.guess()

const TimeTableContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(1),
    overflow: 'scroll',
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
        overflow:'scroll'
    },
}));

const TimeTable: FC = () => {
    const navigate = useNavigate();

    const [events, setEvents] = useState();
    const [openPopup, setOpenPopup] = useState<boolean>();
    const loginUser: Member = useSelector( state => state.user.loginUser);
    const [popupMessage, setPopupMessage] = useState<string>();
    const [today, setToday] = useState<Date>(new Date());

    if( loginUser.name == ''){
       return <Navigate to ="/login?ret=time"/>
    }

    function refreshReservation(){
        const url = `/api/reserve/reservations?startDate=${moment(today).format("YYYYMMDD")}T00:00&endDate=${moment(today).format("YYYYMMDD")}T23:59`;

        fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response =>response.json())
            .then(
                data => {
                    console.log("responseData : "+ JSON.stringify(data));
                    if( data.code == 200 ){
                        // dispatch( setUser( data.payload )) ;
                        // navigate(retUrl)
                        let newEvents= [];

                        data.payload.forEach( e=> {
                            const tableEvent = {
                                id: e.reservationId,
                                title: e.userName,
                                start: moment( e.startDate,"YYYYMMDDTHH:mm").toDate(),
                                end: moment(e.startDate,"YYYYMMDDTHH:mm").add(30,'m').toDate(),
                                editable: e.editable
                            }
                            newEvents.push( tableEvent );
                        })
                        setEvents(newEvents)
                    }
                }
            )
            .catch(error => console.error("Error:", error));
    }

    useEffect(() => {
        refreshReservation();
    },[today]);

    const [value, setValue] = React.useState(0);
    const theme = useTheme();

    const handleSelectSlot = ( {start , end} ) => {
        var find = false;

        if( events != null && events.length > 0 ) {
            for (const ev of events) {
                if (ev.start.getTime() == start.getTime()) {
                    find = true;
                    break;
                }
            }
        }

        if( find ){
            console.log("skip processing. ")
            return;
        }
        if( start > new Date())
            navigate("/reserve?start="+moment(start).format("YYYYMMDDTHH:mm"))
        else{
            // setPopupMessage("Please choose a date in the future")
            // setOpenPopup(true);
            console.log("invalid time")
        }
    }

    const handleSelectEvent = (evt )=>{
        const now = new Date();
        console.log("select event!");
        if( evt.start < now && loginUser.rootUser == false) {
            console.log(JSON.stringify(evt));
            return;
        }
        if( (evt.editable !== true ) && loginUser.rootUser !== true) {
            console.log(JSON.stringify(evt));
            return;
        }else{
            console.log(JSON.stringify(evt));
        }
        navigate("/reserve?regId="+evt.id);

    }

    const handleClosePopup = e=>{
        setOpenPopup(false);
    }

    const handleDayChange = ( newDate: Date ) =>{
        setToday(newDate);
    }

    return (
    <AppProvider theme={theme}>

        <TimeTableContainer direction="column" justifyContent="space-between" >
            <Card variant="elevation" style={ {overflowY:'scroll'}}>
                <Typography
                    component="h3"
                    variant="h4"
                    sx={{width: '100%', fontSize: 'clamp(1rem, 6vw, 2rem)'}}
                >
                    Welcome {loginUser.name}!
                </Typography>

                <Calendar
                    defaultView='day'
                    events={events}
                    localizer={localizer}
                    selectable
                    step={TIME_UNIT}
                    timeslots={1}

                    min={
                        new Date(
                            today.getFullYear(),
                            today.getMonth(),
                            today.getDate(),
                            9,30
                        )
                    }
                    max={
                        new Date(
                            today.getFullYear(),
                            today.getMonth(),
                            today.getDate(),
                            18,30
                        )
                    }
                    views={['day']}

                    onSelectSlot={handleSelectSlot}
                    onSelectEvent={handleSelectEvent}
                    onDoubleClickEvent={handleSelectEvent}
                    longPressThreshold={5}
                    onNavigate={handleDayChange}

                />
            </Card>
                <BottomNavigation
                    showLabels={true}
                    value={value}
                    onChange={(event, newValue) => {
                        setValue(newValue);
                    }}
                    className='stickToBottom'
                >


                    <BottomNavigationAction label="Back" icon={<ArrowBackIosIcon />} onClick={() => navigate('/')} />
                    <BottomNavigationAction label="Main" icon={<HomeIcon  />} onClick={() => navigate('/')} />
                    <BottomNavigationAction label="My Info" icon={<AccountBoxIcon />} />
                    <Snackbar
                        open={openPopup}
                        onClick={()=>setOpenPopup(false)}

                        message={popupMessage}
                        sx={{
                            '&.MuiSnackbar-root': { top: '50px' },
                        }}
                    />
                </BottomNavigation>

        </TimeTableContainer>

    </AppProvider>
    )
}

const locales = {
    'en-US':'enUS',
}



// The types here are `object`. Strongly consider making them better as removing `locales` caused a fatal error
// const localizer = dateFnsLocalizer({
//     format,
//     parse,
//     startOfWeek,
//     getDay,
//     locales,
//
// })
const localizer = momentLocalizer(moment)


export default TimeTable

export const TIME_UNIT : number= 30;


