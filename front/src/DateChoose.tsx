import * as React from 'react';
import {FC, useEffect, useMemo, useState} from 'react';
import {Calendar, DateLocalizer, momentLocalizer, Views} from 'react-big-calendar'
import {format} from 'date-fns/format'
import HomeIcon from '@mui/icons-material/Home';
import ArrowBackIosIcon from '@mui/icons-material/ArrowBackIos';
import AccountBoxIcon from '@mui/icons-material/AccountBox';
import {styled, useTheme} from '@mui/material/styles';
import {AppProvider} from '@toolpad/core/AppProvider';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import 'moment-timezone';
import {setDate} from './redux/store';
import {useDispatch, useSelector} from 'react-redux';
import 'react-big-calendar/lib/addons/dragAndDrop/styles.css'
import 'react-big-calendar/lib/css/react-big-calendar.css'
import {Card} from "@mui/material";
import {useNavigate} from "react-router";
import Stack from "@mui/material/Stack";
import './App.css'
import moment from 'moment'
import CssBaseline from "@mui/material/CssBaseline";
import PropTypes from 'prop-types'
import {Navigate} from "react-router-dom";
import type {Member, OffDay} from 'src/typedef'
import Footer from "./components/Footer";


// Custom Toolbar Component
const CustomToolbar = (toolbarProps) => {
    const goToToday = () => {
        toolbarProps.onNavigate('TODAY');
    };
    const navigate = useNavigate();
    return (
        <div className="rbc-toolbar">
            <span className="rbc-btn-group">
                <button onClick={() => toolbarProps.onNavigate('PREV')}> &lt; 이전 달</button>
                <button onClick={goToToday}>이번 달</button>
                <button onClick={() => toolbarProps.onNavigate('NEXT')}>다음 달 &gt;</button>
            </span>
            <span className="rbc-toolbar-label">{toolbarProps.label}</span>
            <span className="rbc-btn-group">

          </span>
        </div>
    );
};

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

const customMessages = {
    today: '이번 달', // Custom label for 'Today'
    previous: '< 전 달', // Custom label for 'Back'
    next: '다음 달 >', // Custom label for 'Next'
};

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
                toolbar: CustomToolbar
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
                                const today = new Date();
                                let startDate  = new Date( date.getFullYear(), date.getMonth(), 1);
                                if( startDate.getTime() < today.getTime()){
                                    startDate = new Date( today.getFullYear(), today.getMonth(), today.getDate());
                                }

                                let lastOfMonth = new Date( date.getFullYear(), date.getMonth() + 1, 0);
                                let offDays: OffDay[] = data.payload;

                                for( ;startDate.getTime() < lastOfMonth.getTime(); startDate.setDate(startDate.getDate() + 1)) {
                                    let isOffday = false;

                                    for (const offday of offDays) {
                                        const dateStr = offday.offDay;
                                        if (dateStr == moment(startDate).format("YYYYMMDD")) {
                                            isOffday = true;
                                            break;
                                        }
                                    }

                                    if (!isOffday) {
                                        let tableEvent = {
                                            start: new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate(), 0, 0, 0),
                                            end: new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate(), 23, 59, 59),
                                            allDay: true,
                                            title: "예약",
                                            desc: "예약가능",
                                            id: -1,
                                        }
                                        newEvents.push(tableEvent);

                                    }

                                    setEvents(
                                        [...newEvents]
                                    );
                                }
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

        if( start.getTime() < new Date().getTime()){ // can't select past date
            return;
        }

        const startDate = format(start, 'yyyy-MM-dd HH:mm:ss');
        const endDate = format(end, 'yyyy-MM-dd HH:mm:ss');
        const message = `Selected from ${startDate} to ${endDate}`;

        for( const event of events){
            if( event.start.getTime() >= start.getTime() && event.end.getTime() <= end.getTime()){
                setDate( start );
                dispatch( setDate( start))
                navigate('/time');

            }
        }
    }

    const handleSelectEvent = (evt )=> {
        setDate( evt.start );
        dispatch( setDate( evt.start))
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
                        style={{height: "75vh"}}
                        views={{ month: true, week: false, day: false }}
                        onSelectSlot={handleSelectSlot}
                        onSelectEvent={handleSelectEvent}
                        onNavigate={(date:Date) => {
                            setToday(date);
                            dispatch( setDate( date))
                        }}

                        selectable={true}
                        date={today}
                        longPressThreshold={5}
                        events={events}
                        messages={customMessages}
                    />
                </Card>
                <Footer backUrl={"/"}></Footer>
            </DateChooseContainer>
        </AppProvider>
    )
}

const localizer = momentLocalizer(moment);

DateChoose.propTypes = {
    localizer: PropTypes.instanceOf(DateLocalizer),
}

export default DateChoose;


