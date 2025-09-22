import {
    CardActions, CardHeader, FormGroup, IconButton,
} from "@mui/material";
import { useLayoutEffect } from "react";
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
import {useEffect, useRef, useState} from "react";
import {Navigate, useSearchParams} from "react-router-dom";
import {useSelector} from "react-redux";
import Divider from "@mui/material/Divider";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Button from "@mui/material/Button";
import { useDispatch } from "react-redux";
import {setSelectedServices, setMakingReservation} from './redux/store';
import {useNavigate} from "react-router";
import TextField from "@mui/material/TextField";
import {useDropzone} from 'react-dropzone';
import Box from "@mui/material/Box";
import CloseIcon from "@mui/icons-material/Close";


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


const ServiceSelectContainer = styled(Stack)(({ theme }) => ({
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


export default function ServiceSelect() {
    const theme = useTheme();
    const loginUser: Member = useSelector(state => state.user.loginUser);
    const selectedDate: Date  = useSelector( state => state.date).date;
    const savedReservation: Reservation = useSelector( state => state.makingReservation);

    const [availableServices, setAvailableServices] = useState<HairService[]>([]);
    const [services, setServices] = useState <HairService[]>([]);
    const [reservation, setReservation] = useState<Reservation>( {
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
        userId: loginUser.id
    } );
    const [localFiles, setLocalFiles] = useState([]);
    const cardRef = useRef<HTMLDivElement>(null); // Add a ref for the Card
    const footerRef = useRef<HTMLDivElement>(null);
    const headerRef = useRef<HTMLDivElement>(null);
    const [cardMaxHeight, setCardMaxHeight] = useState<number>(0);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const formatDate = (dateStr: string) =>{
        return moment( dateStr,"YYYYMMDDTHH:mm").format('YYYY/MM/DD ddd HH:mm')
    }

    useEffect(()=> {
        if( loginUser.id < 0 ){
            console.log("login required before selecting services")
            navigate("/login?ret=timeSelect");
        }
        else{
            reservation.phone = loginUser.phone;
            reservation.userName = loginUser.name;
            reservation.userId = loginUser.id;
            setReservation({...reservation});
        }
    },[loginUser]);

    useEffect(() => {
            let startDateString = moment(selectedDate).format("YYYYMMDD");
            // need to load available service
            fetch(`/api/reserve/services/${startDateString}`)
                .then((response) => response.json())
                .then((serviceData) => {
                    setAvailableServices(serviceData.payload)
                })
                .catch((error) => {
                    console.error('Error fetching services:', error);
                });

    },[selectedDate]);


    useLayoutEffect(() => {
        const updateCardHeight = () => {
            const footerHeight = footerRef.current?.offsetHeight || 120;
            const headerHeight = headerRef.current?.offsetHeight || 70;
            const availableHeight = window.innerHeight - footerHeight - headerHeight;
            setCardMaxHeight(availableHeight);
        };

        updateCardHeight();
        window.addEventListener("resize", updateCardHeight);

        return () => {
            window.removeEventListener("resize", updateCardHeight);
        };
    }, [availableServices]);

    useEffect(() => {
        if (cardRef.current) {
            cardRef.current.scrollTop = cardRef.current.scrollHeight; // Scroll to bottom
        }
    }, [availableServices]); // Trigger scrolling when availableServices changes

    function saveSelectedService(){
        event.preventDefault();

        const selectedServices = availableServices.filter(service => service.selected);
        reservation.services = selectedServices;

        const formData = new FormData();
        localFiles.forEach(file => {
            formData.append('files', file);
        });

        dispatch( setSelectedServices( selectedServices )) ;
        dispatch( setMakingReservation( reservation));
        navigate("/timeSelect");
    }

    const handleMemoInput = e=>{
        setReservation({
            ...reservation,
            memo: e.target.value
        })
    }

    const handleRequireSilenceChange = (e)=>{
        reservation.requireSilence = !reservation.requireSilence;
        setReservation({
            ...reservation,
        });
    }

    const {getRootProps, getInputProps} = useDropzone({
        accept: {
            'image/*': []
        },
        onDrop: acceptedFiles => {
            setLocalFiles(acceptedFiles.map(file => Object.assign(file, {
                preview: URL.createObjectURL(file)
            })));
        },
        maxFiles:2
    });



    const localThumbs = localFiles.map(file => (
        <Box style={{position: 'relative'}} key={file.name}>
            <div style={thumb} key={file.name}>
                <div style={thumbInner}>
                    <img
                        src={file.preview}
                        style={img}
                        onLoad={() => {
//                                URL.revokeObjectURL(file.preview)
                        }}
                    />
                </div>
            </div>
            <IconButton
                size="small"
                sx={{
                    position: 'absolute',
                    top: 0,
                    right: 0,
                    zIndex: 100,
                    backgroundColor: 'rgba(255, 255, 255, 0.4)',
                    borderRadius:2
                }}
                onClick={() => {
                    setLocalFiles(localFiles.filter((item) => item.name !== file.name));
                    URL.revokeObjectURL(file.preview)
                }}
            >
                <CloseIcon/>
            </IconButton>

        </Box>
    ));


    const serviceCheckBoxes = (
        <>
            {availableServices.filter(service => service.idx < 1000).length > 0 && (
                <Typography variant="h6" gutterBottom style={{textAlign: 'left'}}>
                    💇‍♂️ Men’s Services
                </Typography>
            )}
            {availableServices
                .filter(service => service.idx < 1000)
                .map((service: HairService) => (
                    <FormControlLabel
                        key={service.serviceId}
                        control={
                            <Checkbox
                                id={`check_service_${service.serviceId}`}
                                checked={service.selected}

                                onChange={e => {
                                    service.selected = e.target.checked;
                                    setServices([...services]);
                                }}
                                style={{
                                    marginTop: 3,
                                    marginBottom: 0,
                                    paddingTop: 0,
                                    paddingBottom: 0,
                                    gap: 0,
                                    lineHeight: 0
                                }}
                            />
                        }
                        label={
                            <span>
                            {service.name.split('\\n').map((line, index, arr) => (
                                <React.Fragment key={index}>
                                    {line}
                                    {index < arr.length - 1 && <br/>}
                                </React.Fragment>
                            ))}
                                {' '} ${service.price}
                                {service.description.length > 0 && <br/>}
                                {service.description.length > 0 ? '(' + service.description + ')' : ''}
                            </span>
                        }
                        style={{textAlign: 'left'}}
                    />
                ))}
            <Divider/>

            {availableServices.filter(service => service.idx >= 1000).length > 0 && (
                <Typography variant="h6" gutterBottom style={{textAlign: 'left'}}>
                    💇‍♀️ Women’s Services
                </Typography>
            )}
            {availableServices
                .filter(service => service.idx >= 1000)
                .map((service: HairService) => (
                    <FormControlLabel
                        key={service.serviceId}
                        control={
                            <Checkbox
                                id={`check_service_${service.serviceId}`}
                                checked={service.selected}
                                onChange={e => {
                                    service.selected = e.target.checked;
                                    setServices([...services]);
                                }}
                                style={{
                                    marginTop: 3,
                                    marginBottom: 0,
                                    paddingTop: 0,
                                    paddingBottom: 0,
                                    gap: 0,
                                    lineHeight: 0
                                }}
                            />
                        }
                        label={
                            <span>
                            {service.name.split('\\n').map((line, index, arr) => (
                                <React.Fragment key={index}>
                                    {line}
                                    {index < arr.length - 1 && <br/>}
                                </React.Fragment>
                            ))}
                                {' '} ${service.price}
                                {service.description.length > 0 && <br/>}
                                {service.description.length > 0 ? '(' + service.description + ')' : ''}
                            </span>
                        }
                        style={{textAlign: 'left'}}
                    />
                ))}
            <Divider/>
            <Typography variant="subtitle1" gutterBottom style={{textAlign: 'left'}}>
                All services include a complimentary haircut.
            </Typography>

            <TextField
                placeholder={"원하시는 스타일을 적어주시면 디자이너에게 전달됩니다"}
                multiline
                rows={3}
                value={reservation.memo}
                onChange={handleMemoInput}
            />

            <FormControlLabel
                control={
                    <Checkbox
                        id='check_requireSilence'
                        checked={reservation.requireSilence}
                        disabled={!reservation.editable}
                        onChange={handleRequireSilenceChange}
                    />}
                label='조용히 시술받고 싶어요'
            />

            <section className="container">
                <div {...getRootProps({className: 'dropzone', style: baseStyle})}>
                    <input {...getInputProps()} />
                    {!loginUser.rootUser &&
                        <p style={{'fontSize': 'small'}}>원하시는 스타일 사진첨부 가능합니다 (최대 2장)</p>
                    }
                </div>
                <aside style={thumbsContainer}>
                    { localThumbs}
                </aside>
            </section>
        </>
    );


    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme/>
            <ServiceSelectContainer direction="column" justifyContent="space-between">
                <Header
                    ref={headerRef}
                />
                <Card variant="outlined"
                      ref={cardRef}
                      style={{overflowY: 'scroll', maxHeight: `${cardMaxHeight}px`}}
                >
                    <Box
                        component="form"
                        sx={{display: 'flex', flexDirection: 'column', gap: 1}}
                    >

                    <CardHeader
                        title={moment(selectedDate).format("YYYY/MM/DD")}
                    />

                    <FormGroup>
                        {serviceCheckBoxes}
                    </FormGroup>
                    <Divider/>
                    <CardActions  sx={{
                        display: 'flex',
                        justifyContent: 'center',
                    }}>
                        <Button
                            type="submit"
                            size="large"
                            variant="contained"
                            onClick={ () => saveSelectedService() }
                        >
                            시간선택

                        </Button>
                    </CardActions>
                    </Box>
                </Card>
                <Footer backUrl="BACK" showMyInfo={true}
                        ref={footerRef}
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