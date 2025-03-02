import { FC, useState } from 'react'
import { Calendar, dateFnsLocalizer, Event } from 'react-big-calendar'
import {format} from 'date-fns/format'
import parse from 'date-fns/parse'
import startOfWeek from 'date-fns/startOfWeek'
import getDay from 'date-fns/getDay'
import enUS from 'date-fns/locale/en-US'
import { AppProvider } from '@toolpad/core/AppProvider';
import { useTheme } from '@mui/material/styles';
import * as React from 'react';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import SavingsIcon from '@mui/icons-material/Savings';
import FavoriteIcon from '@mui/icons-material/Favorite';
import ArrowBackIosIcon from '@mui/icons-material/ArrowBackIos';

import 'react-big-calendar/lib/addons/dragAndDrop/styles.css'
import 'react-big-calendar/lib/css/react-big-calendar.css'
import {Paper} from "@mui/material";


const TimeTable: FC = () => {
    const today = new Date();
    const start =  new Date(
        today.getFullYear(),
        today.getMonth(),
        today.getDate(),
        11,30
    );
    const end = new Date(
        today.getFullYear(),
        today.getMonth(),
        today.getDate(),
        12
    );
    const [events] = useState<Event[]>([
        {
            title: 'Lemmy 남자커트',
            start,
            end,
        },
    ]);
    const [value, setValue] = React.useState(0);
    const theme = useTheme();

    fetch("/posts")
        .then((response) => response.json())
        .then((data) => console.log(data));


    return (
    <AppProvider theme={theme}>
            <Paper sx={{   flex: "1 0 auto" }}>
                <Calendar
                    defaultView='day'
                    events={events}
                    localizer={localizer}
                    resizable
                    style={{ width:'90%', overflow: 'scroll'}}
                    step={30}
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
                    views={[ 'day']}
                    popup={true}
                />
                <BottomNavigation
                    showLabels
                    value={value}
                    onChange={(event, newValue) => {
                        setValue(newValue);
                    }}
                    className='stickToBottom'
                >
                    <BottomNavigationAction label="Points" icon={<SavingsIcon />} />
                    <BottomNavigationAction label="Favorites" icon={<FavoriteIcon />} />
                    <BottomNavigationAction label="Back" icon={<ArrowBackIosIcon />} />
                </BottomNavigation>
            </Paper>
    </AppProvider>
    )
}

const locales = {
    'en-US': enUS,
}



// The types here are `object`. Strongly consider making them better as removing `locales` caused a fatal error
const localizer = dateFnsLocalizer({
    format,
    parse,
    startOfWeek,
    getDay,
    locales,
})


export default TimeTable