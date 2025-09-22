import { FC, useState, useEffect  } from 'react'
import { Calendar, dateFnsLocalizer, Event,momentLocalizer , BigCalendar} from 'react-big-calendar'

import { AppProvider } from '@toolpad/core/AppProvider';
import {styled, useTheme} from '@mui/material/styles';
import * as React from 'react';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';

import type {Member} from 'src/typedef'
import { useSelector, useDispatch } from 'react-redux';
import 'react-big-calendar/lib/addons/dragAndDrop/styles.css'
import 'react-big-calendar/lib/css/react-big-calendar.css'
import {Card} from "@mui/material";
import {useNavigate} from "react-router";
import {Navigate} from 'react-router-dom';
import Stack from "@mui/material/Stack";
import  './App.css'
import Typography from "@mui/material/Typography";
import moment from 'moment'
import   'moment-timezone';
import { setDateStr, setUser} from "./redux/store";
import Footer from './components/Footer';
const defaultTZ = moment.tz.guess();

const TabBarButton = styled(BottomNavigationAction)({
    color: '#e67e22',
    '.Mui-selected, svg':{
        color: '#e67e22',
    }
});
const CustomEvent = ({ event }) => {
    return (
        <span>
            {event.title} <br />
            {event.service}
        </span>
    );
};
// Custom Toolbar Component
const CustomToolbar = (toolbarProps) => {
    const goToToday = () => {
        toolbarProps.onNavigate('TODAY');
    };
    const navigate = useNavigate();
    return (
        <div className="rbc-toolbar">
            <span className="rbc-btn-group">
            <button onClick={() => navigate("/dateChoose")} style={{marginRight:'10px'}}>달력</button>
                <button onClick={() => toolbarProps.onNavigate('PREV')}> &lt; 전날</button>
                <button onClick={goToToday}>오늘</button>
                <button onClick={() => toolbarProps.onNavigate('NEXT')}>다음 날 &gt;</button>
            </span>
            <span className="rbc-toolbar-label">{toolbarProps.label}</span>
            <span className="rbc-btn-group">

          </span>
        </div>
    );
};


const TimeTableContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(1),
    overflow: 'scroll',
    [theme.breakpoints.up('sm')]: {
        padding: theme.spacing(1),
    },
    '& .rbc-event-label': {
        display: 'none', // Hides the start and end time
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

    const selectedDate: string  = useSelector( state => state.date.dateStr);
    const [events, setEvents] = useState();
    const [openPopup, setOpenPopup] = useState<boolean>();
    const loginUser: Member = useSelector( state => state.user.loginUser);
    const [popupMessage, setPopupMessage] = useState<string>();
    const [today, setToday] = useState<Date>( new Date(selectedDate)  );
    const [isOffday, setIsOffday] = useState(false);

    if( loginUser.id < 0){
       return <Navigate to ="/login?ret=time"/>
    }

    let dispatch = useDispatch();

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
                        console.log("available designer : "+ JSON.stringify(data));
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
                                end:   moment(  e.endDate,"YYYYMMDDTHH:mm").toDate(),
                                editable: e.editable,
                                service: e.services.length > 0 ? e.services[0].name : "",
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

    useEffect(() => {
        refreshReservation()
    }, [isOffday]);

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
                if (ev.start.getTime() < end.getTime() && ev.end.getTime()  > start.getTime() ) {
                    find = true;
                    break;
                }
            }
        }

        if( find ){
            console.log("skip processing. ")
            return;
        }
        if( start > new Date() )
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

        if( evt.end < now && loginUser.rootUser == false) {
            console.log("now"+ now+" "+JSON.stringify(evt));
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
        dispatch( setDateStr( newDate.toISOString() ))
    }

    const customMessages = {
        today: '오늘', // Custom label for 'Today'
        previous: '< 전 날', // Custom label for 'Back'
        next: '다음 날 >', // Custom label for 'Next'
    };

    return (


    <AppProvider theme={theme}>

        <TimeTableContainer direction="column" justifyContent="space-between" >
            <Card variant="elevation" style={ {overflowY:'scroll'}}>
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
                            10,30
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
                    onDragStart={(e)=>{console.log('drag');e.preventDefault(); e.stopPropagation()}}
                    onDragOver={(e)=>{e.preventDefault(); e.stopPropagation()}}
                    onDragEnd={(e)=>{e.preventDefault(); e.stopPropagation()}}
                    longPressThreshold={5}
                    onNavigate={handleDayChange}
                    style={{height: "90vh"}}
                    messages={customMessages}
                    showMultiDayTimes={false}
                    draggableAccessor={event => false}
                    components={{
                        toolbar: CustomToolbar, // Use the custom toolbar,
                        event:CustomEvent
                    }}
                />
            </Card>
            <Footer backUrl={"/dateChoose"}></Footer>
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


