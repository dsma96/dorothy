import { FC, useState, useCallback } from 'react'
import { Calendar, dateFnsLocalizer, Event,momentLocalizer , BigCalendar} from 'react-big-calendar'
import {format} from 'date-fns/format'
import parse from 'date-fns/parse'
import startOfWeek from 'date-fns/startOfWeek'
import getDay from 'date-fns/getDay'
import enUS from 'date-fns/locale/en-US'
import { AppProvider } from '@toolpad/core/AppProvider';
import {styled, useTheme} from '@mui/material/styles';
import * as React from 'react';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import SavingsIcon from '@mui/icons-material/Savings';
import FavoriteIcon from '@mui/icons-material/Favorite';
import ArrowBackIosIcon from '@mui/icons-material/ArrowBackIos';

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

    const today = new Date();

    const [events, setEvents] = useState<Event[]>();
    const [openPopup, setOpenPopup] = useState<boolean>();
    const loginUser: Member = useSelector( state => state.user);
    const [popupMessage, setPopupMessage] = useState<string>();

    if( loginUser.name == 'anonymous'){
       return <Navigate to ="/login?ret=time"/>
    }

    const [value, setValue] = React.useState(0);
    const theme = useTheme();

    const handleSelectSlot = useCallback(({ start , end  }) => {
        if( start > new Date())
            navigate("/reserve?start="+moment(start).format('YYYYMMDD HH:mm'))
        else{
            // setPopupMessage("Please choose a date in the future")
            // setOpenPopup(true);
            console.log("invalid time")
        }
    }, [setEvents])

    const handleSelectEvent = (e)=>{
        console.log('selectEvent'+JSON.stringify(e));
    }

    const handleClosePopup = e=>{
        setOpenPopup(false);
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
                    popup={true}
                    onSelectSlot={handleSelectSlot}
                    onSelectEvent={handleSelectEvent}
                    onDoubleClickEvent={handleSelectEvent}
                    longPressThreshold={5}
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

                    <BottomNavigationAction label="Points" icon={<SavingsIcon />} />
                    <BottomNavigationAction label="Favorites" icon={<FavoriteIcon />} />
                    <BottomNavigationAction label="Back" icon={<ArrowBackIosIcon />} onClick={() => navigate('/')} />
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


