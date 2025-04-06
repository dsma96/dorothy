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

import type {Member} from 'src/typedef'
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
const defaultTZ = moment.tz.guess();

const TabBarButton = styled(BottomNavigationAction)({
    color: '#e67e22',
    '.Mui-selected, svg':{
        color: '#e67e22',
    }
});


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
    let now  = new Date();
    const startDate = new Date(2025,3,5);
    const selectedDate: Date  = useSelector( state => state.date);
    const [events, setEvents] = useState();
    const [openPopup, setOpenPopup] = useState<boolean>();
    const loginUser: Member = useSelector( state => state.user.loginUser);
    const [popupMessage, setPopupMessage] = useState<string>();
    const [today, setToday] = useState<Date>( selectedDate.date  );
    const [isOffday, setIsOffday] = useState<boolean>(false);
    if( loginUser.id < 0){
       return <Navigate to ="/login?ret=time"/>
    }

    function refreshAvailableDesigner(dateStr){
        const url = `/api/user/${dateStr}/designers`;

        fetch(url, {
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
                        let newEvents = [];

                        if( data.payload && data.payload.length == 0){
                            console.log("off day / 휴무일");
                            const tableEvent = {
                                id: -1,
                                title: 'Off day',
                                start: moment( dateStr+'T09:30',`YYYYMMDDTHH:mm`).toDate(),
                                end: moment( dateStr+'T19:00',`YYYYMMDDTHH:mm`).toDate(),
                                editable: false
                            }
                            newEvents.push(tableEvent);
                            setEvents(newEvents);
                            setIsOffday(true);
                        }
                        else{
                            console.log("on day "+ JSON.stringify(data.payload));
                            setIsOffday(false);
                            refreshReservation()
                        }
                    }
                }
            )
            .catch(error => console.error("Error:", error));
    }

    function refreshReservation(){
        if( isOffday ){
            return;
        }
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
                    console.log("response Result Data : "+ JSON.stringify(data));
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
//        refreshReservation();
        refreshAvailableDesigner(moment(today).format("YYYYMMDD"));
    },[today]);

    const [value, setValue] = React.useState(0);
    const theme = useTheme();

    const handleSelectSlot = ( {start , end, slots} ) => {
        let now = new Date();

        if( isOffday ){
            return;
        }

        var find = false;
        if( slots?.length > 2) return;

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
        if( start > new Date() && start > startDate )
            navigate("/reserve?start="+moment(start).format("YYYYMMDDTHH:mm"))
        else{
            // setPopupMessage("Please choose a date in the future")
            // setOpenPopup(true);
            console.log("invalid time")
        }
    }

    const handleSelectEvent = (evt )=>{

        const now = new Date();
        if( isOffday ){
            return;
        }

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
                    date = {today}
                    min={

                        new Date(
                            today.getFullYear(),
                            today.getMonth(),
                            today.getDate(),
                            10,0
                        )
                    }
                    max={
                        new Date(
                            today.getFullYear(),
                            today.getMonth(),
                            today.getDate(),
                            19,0
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

                    <TabBarButton label="Back" color='primary' icon={<ArrowBackIosIcon /> } onClick={() => navigate(-1)} />
                    <TabBarButton label="Main" color='primary' icon={<HomeIcon  />} onClick={() => navigate('/')} />
                    <TabBarButton label="My Info" color='primary' icon={<AccountBoxIcon />} onClick={() => navigate('/my')}/>
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


