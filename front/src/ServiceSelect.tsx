import {
    CardActions, CardHeader, FormGroup,
} from "@mui/material";

import { AppProvider } from '@toolpad/core/AppProvider';
import { HairService, Reservation, UploadFile, Member } from "./typedef";
import Header from "./components/Header";
import Footer from "./components/Footer";
import {styled, useTheme} from "@mui/material/styles";
import MuiCard from "@mui/material/Card";
import Stack from "@mui/material/Stack";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import moment from "moment";
import {useEffect, useState} from "react";
import {useSearchParams} from "react-router-dom";
import {useSelector} from "react-redux";
import Divider from "@mui/material/Divider";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";


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


const ServiceSelectContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(2),
    justifyContent: 'flex-start',
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


export default function ServiceSelect() {
    const theme = useTheme();
    const [availableServices, setAvailableServices] = useState<HairService[]>([]);
    const selectedDate: Date  = useSelector( state => state.date).date;
    const [services, setServices] = useState <HairService[]>([]);
//moment(today).format("YYYYMMDD")

    const formatDate = (dateStr: string) =>{
        return moment( dateStr,"YYYYMMDDTHH:mm").format('YYYY/MM/DD ddd HH:mm')
    }

    useEffect(() => {
            let startDateString = moment(selectedDate).format("YYYYMMDD");
            // need to load available service
            fetch(`/api/reserve/services/${ startDateString}`)
                .then((response) => response.json())
                .then((serviceData) => {
                    setAvailableServices(serviceData.payload)
                })
                .catch((error) => {
                    console.error('Error fetching services:', error);
                });

    },[selectedDate]);




    const serviceCheckBoxes =  availableServices.map( (service: HairService, index) => {
        return (
            <FormControlLabel
                key={service.serviceId}
                control={
                    <Checkbox
                        id={`check_service_${service.serviceId}`}
                        checked={ service.selected }
                        onChange={e=>{
                            service.selected = e.target.checked;
                            let newServices = [
                                ...services
                            ];
                            setServices(newServices);
                        }
                        }
                        style={{marginTop: 3, marginBottom:0, paddingTop:0,paddingBottom:0, gap:0, lineHeight:0}}
                    />}
                label={service.name+" $"+service.price}
            />
        )
    });
    return(
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <ServiceSelectContainer direction="column" justifyContent="space-between">
                <Header/>
                <Card variant="outlined" style={{overflowY:'scroll'}}>
                    <CardHeader
                        title={moment(selectedDate).format("YYYY/MM/DD")}
                    />


                    <FormGroup>
                        {serviceCheckBoxes}
                    </FormGroup>
                </Card>
                <Footer backUrl="BACK" showMyInfo={false}
                        style={{
                            position: 'fixed',
                            bottom: 0,
                            left: 0,
                            right: 0,
                            backgroundColor: '#fff',
                            zIndex: 1,
                        }}
                />
            </ServiceSelectContainer>
        </AppProvider>
    );
}