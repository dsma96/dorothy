import { FC, useState, useCallback, useEffect, useMemo  } from 'react'
import { Calendar, dateFnsLocalizer, Event,momentLocalizer , BigCalendar, Views, DateLocalizer} from 'react-big-calendar'
import {format} from 'date-fns/format'
import HomeIcon from '@mui/icons-material/Home';
import ArrowBackIosIcon from '@mui/icons-material/ArrowBackIos';
import AccountBoxIcon from '@mui/icons-material/AccountBox';
import {styled, useTheme} from '@mui/material/styles';
import { AppProvider } from '@toolpad/core/AppProvider';
import * as React from 'react';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import   'moment-timezone';
import {setDate} from './redux/store';
import { useSelector, useDispatch } from 'react-redux';
import 'react-big-calendar/lib/addons/dragAndDrop/styles.css'
import 'react-big-calendar/lib/css/react-big-calendar.css'
import {Card, Snackbar} from "@mui/material";
import {useNavigate} from "react-router";
import Stack from "@mui/material/Stack";
import  './App.css'
import moment from 'moment'
import CssBaseline from "@mui/material/CssBaseline";
import PropTypes from 'prop-types'
import * as dates from "./utils/dates";
import {Navigate} from "react-router-dom";
import type {Member, OffDay} from 'src/typedef'
const TabBarButton = styled(BottomNavigationAction)({
    color: '#e67e22',
    '.Mui-selected, svg':{
        color: '#e67e22',
    }
});
const ColoredDateCellWrapper = ({ children }) =>
    React.cloneElement(React.Children.only(children), {
        style: {
            backgroundColor: 'lightblue',
        },
    })

const DateChooseContainer = styled(Stack)(({ theme }) => ({
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
const DateChoose: FC = () => {
    let dispatch = useDispatch();

    const selectedDate: Date  = useSelector( state => state.date);
    const loginUser: Member = useSelector( state => state.user.loginUser);

    const theme = useTheme();
    const navigate = useNavigate();
    const [today, setToday] = useState<Date>(new Date());
    const [events, setEvents] = useState<[]>([]);


    if( loginUser.id < 0){
        return <Navigate to ="/login?ret=dateChoose"/>
    }


    const { components, defaultDate,  views } = useMemo(
        () => ({
            components: {
                timeSlotWrapper: ColoredDateCellWrapper,
            },
            defaultDate:today,
            views: Object.keys(Views).map((k) => Views[k]),
        }),
        []
    )

    const updateOffDay = ((date: Date) => {

        if( events.length == 0 ||  events[0].start.getMonth() != date.getMonth()) {
            const url = `/api/user/offday/${moment(date).format('YYYY/MM')}`;
            fetch(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            })
                .then(response => response.json())
                .then(
                    data => {
                        if (data.code == 200) {
                            // dispatch( setUser( data.payload )) ;
                            // navigate(retUrl)
                            let newEvents = [];

                            if (data.payload && data.payload.length > 0) {

                                let offDays: OffDay[] = data.payload;

                                for (const offday of offDays) {
                                    const dateStr = offday.offDay;
                                    const tableEvent = {
                                        title: 'Off day',
                                        start: moment(dateStr + '00:00', `YYYYMMDDHH:mm`).toDate(),
                                        end: moment(dateStr + '23:59', `YYYYMMDDHH:mm`).toDate(),
                                        editable: false,
                                        allDay: true
                                    }
                                    newEvents.push(tableEvent);
                                }
                                setEvents(
                                    [...newEvents]
                                );
                            } else {
                                console.log("on day " + JSON.stringify(data.payload));
                            }
                        }
                    }
                )
                .catch(error => console.error("Error:", error));
        }
    })

    useEffect(() => {
        updateOffDay(today);

    },[today]);


    const handleSelectSlot = ( {start , end} ) => {
        const startDate = format(start, 'yyyy-MM-dd HH:mm:ss');
        const endDate = format(end, 'yyyy-MM-dd HH:mm:ss');
        const message = `Selected from ${startDate} to ${endDate}`;

        for( const event of events){
            if( event.start.getTime() >= start.getTime() && event.end.getTime() <= end.getTime()){
                console.log("This date is off day");
                return;
            }
        }

        setDate( start );
        dispatch( setDate( start))
        navigate('/time');
    }

    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <DateChooseContainer direction="column" justifyContent="space-between" >
                <Card variant="elevation" style={ {overflowY:'scroll'}}>
                    <Calendar
                        components={components}
                        defaultDate={today}
                        startAccessor="start"
                        endAccessor="end"
                        localizer={localizer}
                        style={{height: 800}}
                        views={{ month: true, week: false, day: false }}
                        onSelectSlot={handleSelectSlot}
                        onNavigate={(date:Date) => {
                            setToday(date);
                            dispatch( setDate( date))
                        }}

                        selectable={true}
                        date={today}
                        longPressThreshold={5}
                        events={events}
                    />
                </Card>
                <BottomNavigation
                    showLabels={true}
                    className='stickToBottom'
                >

                    <TabBarButton label="Back" color='primary' icon={<ArrowBackIosIcon /> } onClick={() => navigate('/')} />
                    <TabBarButton label="Main" color='primary' icon={<HomeIcon  />} onClick={() => navigate('/')} />
                    <TabBarButton label="My Info" color='primary' icon={<AccountBoxIcon />} onClick={() => navigate('/my')}/>

                </BottomNavigation>
            </DateChooseContainer>
        </AppProvider>
    )
}

const localizer = momentLocalizer(moment);

DateChoose.propTypes = {
    localizer: PropTypes.instanceOf(DateLocalizer),
}

export default DateChoose;


