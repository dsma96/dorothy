import * as React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import MuiCard from '@mui/material/Card';
import {styled, useTheme} from '@mui/material/styles';
import CloseIcon from '@mui/icons-material/Close'
import {useDropzone} from 'react-dropzone';
import Divider from "@mui/material/Divider";
import FormControlLabel from "@mui/material/FormControlLabel";
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from "react-router";
import { useSearchParams } from "react-router-dom";
import moment from 'moment'
import TextField from "@mui/material/TextField";
import { useEffect, useState } from "react";
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ReservationHistoryTable from "./components/ReservationHistoryTable";
import {
    CardActions,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle, FormGroup,
    IconButton
} from "@mui/material";

import { AppProvider } from '@toolpad/core/AppProvider';
import { HairService, Reservation, UploadFile, Member } from "./typedef";
import Header from "./components/Header";


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

const ReserveEditContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(2),
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


export default function ReserveEdit() {
    const loginUser: Member = useSelector(state => state.user.loginUser);
    const [availableServices, setAvailableServices] = useState<HairService[]>([]);

    let [searchParams] = useSearchParams();
    const [openDialog, setOpenDialog] = useState(false);
    const [dialogTitle, setDialogTitle] = useState("Dorothy");
    const [ dialogMessage, setDialogMessage] = useState("");
    const [openCancelDialog, setOpenCancelDialog] = useState(false);
    const [openYesNoDialog, setOpenYesNoDialog] = useState(false);
    const cancelDialogTitle = "Cancel reservation";
    const cancelDialogMessage = "Would you like to cancel your reservation?";
    const [hasError, setHasError] = useState(false);
    const [localFiles, setLocalFiles] = useState([]);
    const [fromServer, setFromServer] = useState(searchParams.get("regId") != null);
    const [openPreviewDialog, setOpenPreviewDialog] = useState(false);
    const [previewFile,setPreviewFile] = useState<UploadFile>();
    const [services, setServices] = useState <HairService[]>([]);
    const [stampCount, setStampCount] = useState<number>(0);

    let buttonClicked = false;

    const [ customerInfo, setCustomerInfo] = useState<Member>({
        phone: '',
        name: '',
        id: -1,
        rootUser: false,
        memo: ''
    });

    let dispatch = useDispatch();

    const {getRootProps, getInputProps} = useDropzone({
        accept: {
            'image/*': []
        },
        onDrop: acceptedFiles => {
            setFromServer( false );
            setLocalFiles(acceptedFiles.map(file => Object.assign(file, {
                preview: URL.createObjectURL(file)
            })));
        },
        maxFiles:2
    });

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
        userId: -1
    });

    const localThumbs = localFiles.map(file => (
        <Box style={{position: 'relative'}} key={file.name}>
            <div style={thumb} key={file.name}>
                <div style={thumbInner}>
                        <img
                            src={file.preview}
                            style={img}
                            // Revoke data uri after image is loaded
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


    const yesNoDialog = ({title, message, yesCallback, noCallback})=>{
        return (
            <Dialog
                open={openYesNoDialog}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {title}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        {message}
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={()=>{yesCallback();setOpenYesNoDialog(false)}} autoFocus>
                        Yes
                    </Button>
                    <Button onClick={()=>{ if(noCallback) noCallback(); setOpenYesNoDialog(false);}} autoFocus>
                        No
                    </Button>
                </DialogActions>
            </Dialog>
        )
    }

    //
    // const serviceCheckBoxes = services == null ? [] : services.map( (service: HairService, index) => {
    //     return (
    //         <FormControlLabel
    //             key={service.serviceId}
    //             control={
    //                 <Checkbox
    //                     id={`check_service_${service.serviceId}`}
    //                     checked={ service.selected }
    //                     disabled={!reservation.editable}
    //                     onChange={e=>{
    //                             service.selected = e.target.checked;
    //                             let newServices = [
    //                                 ...services
    //                             ];
    //                             setServices(newServices);
    //                         }
    //                     }
    //                     style={{marginTop: 3, marginBottom:0, paddingTop:0,paddingBottom:0, gap:0, lineHeight:0}}
    //                 />}
    //             label={service.name+" $"+service.price}
    //         />
    //     )
    // });
    const serviceCheckBoxes = (
        <>
            {services.filter(service => service.idx < 1000).length > 0 && (
                <Typography variant="h6" gutterBottom style={{textAlign:'left'}}>
                    💇‍♂️ Men’s Services
                </Typography>
            )}
            {services
                .filter(service => service.idx < 1000)
                .map((service: HairService) => (
                    <FormControlLabel
                        key={service.serviceId}
                        control={
                            <Checkbox
                                id={`check_service_${service.serviceId}`}
                                checked={service.selected}
                                disabled={!reservation.editable}
                                onChange={e => {
                                    service.selected = e.target.checked;
                                    setServices([...services]);
                                }}
                                style={{marginTop: 3, marginBottom:0, paddingTop:0,paddingBottom:0, gap:0, lineHeight:0}}
                            />
                        }
                        label={
                            <span>
                            {service.name.split('\\n').map((line, index, arr) => (
                                <React.Fragment key={index}>
                                    {line}
                                    {index < arr.length - 1&& <br />}
                                </React.Fragment>
                            ))}
                                {' '} ${service.price}
                                { service.description.length > 0 && <br/> }
                                { service.description.length > 0 ? '('+service.description +')' : ''  }
                            </span>
                        }
                        style={{ textAlign:'left'}}
                    />
                ))}
            <Divider/>

            {services.filter(service => service.idx >= 1000).length > 0 && (
                <Typography variant="h6" gutterBottom style={{textAlign:'left'}}>
                    💇‍♀️ Women’s Services
                </Typography>
            )}
            {services
                .filter(service => service.idx >= 1000)
                .map((service: HairService) => (
                    <FormControlLabel
                        key={service.serviceId}
                        control={
                            <Checkbox
                                id={`check_service_${service.serviceId}`}
                                checked={service.selected}
                                disabled={!reservation.editable}
                                onChange={e => {
                                    service.selected = e.target.checked;
                                    setServices([...services]);
                                }}
                                style={{marginTop: 3, marginBottom:0, paddingTop:0,paddingBottom:0, gap:0, lineHeight:0}}
                            />
                        }
                        label={
                            <span>
                            {service.name.split('\\n').map((line, index, arr) => (
                                <React.Fragment key={index}>
                                    {line}
                                    {index < arr.length - 1 && <br />}
                                </React.Fragment>
                            ))}
                                {' '} ${service.price}
                                { service.description.length > 0 && <br/> }
                                { service.description.length > 0 ? '('+service.description +')' : ''  }
                            </span>
                        }
                        style={{ textAlign:'left'}}
                    />
                ))}
            <Divider/>
            <Typography variant="subtitle1" gutterBottom style={{textAlign:'left'}}>
                All services include a complimentary haircut.
            </Typography>
        </>
    );
    const serverThumbs = reservation.files ? reservation.files.map(file => (
            <Box style={{position: 'relative'}} key={file.id}>
                <div style={thumb} key={file.id}>
                    <div style={thumbInner}>
                        <img
                            src={file.url}
                            style={img}
                            onClick={() => {
                            setPreviewFile(file);
                            setOpenPreviewDialog(true);
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
                    let newR = {
                        ...reservation,
                        files: reservation.files.filter((item) => item.id != file.id)
                    } as Reservation;
                    setReservation(newR);
                }}
            >
                <CloseIcon/>
            </IconButton>

        </Box>
    ))
    : [];


    useEffect(() => {
        if( fromServer ){ // EDIT mode
            if(reservation.reservationId > 0 &&availableServices &&  availableServices.length > 0 && services.length == 0) {
                let newServices: HairService[] = [];
                availableServices.forEach( (service: HairService) => {
                    let newService = { ...service};
                    newService.selected = reservation.services.some( s => s.serviceId == service.serviceId);
                    newServices.push(newService);
                });
                setServices(newServices);
            }
        }else{
            if(availableServices && availableServices.length > 0 && services.length == 0) {
                let newServices: HairService[] = [];
                availableServices.forEach( (service: HairService) => {
                    let newService = { ...service};
                    newService.selected = false;
                    newServices.push(newService);
                });
                setServices( newServices);
            }
        }
    }, [services, reservation]);

    useEffect(()=>{
        if( loginUser.rootUser && reservation.reservationId > 0) { // need to load available service
            fetch('/api/user/'+reservation.userId)
                .then((response) => response.json())
                .then((data) => {
                    setCustomerInfo(data.payload);
                })
                .catch((error) => {
                    console.error('Error fetching services:', error);
                });

            fetch(`/api/coupon/${reservation.userId}/stampAmount`)
                .then((response) => response.json())
                .then((data) => {
                    if( data.code == 200)
                        setStampCount(data.payload);
                })
                .catch((error) => {
                    console.error('Error fetching services:', error);
                });

        }
    },[reservation]);

    useEffect(() => {

        if (searchParams.get("start")) {
            let startDateString = searchParams.get("start");
            const newReservation = {
                ...reservation,
                startDate: startDateString
            } as Reservation;
            console.debug("getService from startDate");
        // need to load available service
            fetch(`/api/reserve/services/${startDateString.substring(0,8)}`)
                .then((response) => response.json())
                .then((serviceData) => {
                    setAvailableServices(serviceData.payload)
                    setReservation( newReservation);
                })
                .catch((error) => {
                    console.error('Error fetching services:', error);
                });

        }
        else if( searchParams.get("regId")){
            let regId = searchParams.get("regId");
            fetch(`/api/reserve/${regId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            })
                .then(response =>response.json())
                .then(
                    data => {
                        if( data.code == 200 ){
                            console.log(JSON.stringify(data.payload));
                            if( data.payload.files == null )
                                data.payload.files = [];
                            let startDate = data.payload.startDate;
                            console.log("getService from regId");
                            fetch(`/api/reserve/services/${startDate.substring(0,8)}`)
                                .then((response) => response.json())
                                .then((serviceData) => {
                                    setAvailableServices(serviceData.payload)
                                    setReservation( data.payload );
                                })
                                .catch((error) => {
                                    console.error('Error fetching services:', error);
                                });

                        }
                    }
                )
                .catch(error => console.error("Error:", error));
        }

        return () => localFiles.forEach(file => URL.revokeObjectURL(file.preview));
    },[]);

    const handleCloseDialog=()=>{
        setOpenDialog(false);
        if( hasError != true )
            navigate(-1);
    }

    const closeCancelDialog=()=>{
        setOpenCancelDialog(false);
    }

    const cancelReservation = ()=>{
        fetch(`/api/reserve/cancel/${reservation.reservationId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        showDialog("Complete!", "Your reservation has been successfully canceled.", true);
                    }
                }
            )
            .catch(error => {
                    setDialogMessage(error.msg);
                    setOpenDialog(true);
                }
            );
    }

    function showDialog(title: string, msg: string, goHome: boolean){
        setDialogTitle(title);
        setDialogMessage( msg);
        setHasError(!goHome);
        setOpenDialog(true);
    }

    function validateInput () : string{
        let serviceSelected = false;
        services.forEach( (service: HairService) => {
            if( service.selected ){
                serviceSelected = true;
            }
        });

        if( serviceSelected == false ){
            return "예약할 서비스를 선택해주세요";
        }
        return "SUCCESS";
    }

    const useCoupon = ()=>{
        fetch(`/api/coupon/${reservation.userId}/coupon?regId=${reservation.reservationId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            }
        })  .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        setStampCount(data.payload);
                        showDialog("Success",'쿠폰이 적용되었습니다',false);
                    }else{
                        showDialog("Error",data.msg,false);
                    }
                }
            )
            .catch(error => {
                setDialogMessage(error.msg);
                showDialog('Error',error.msg, false);
            });
    }

    function adjustReservation (amount : number){
        let op = amount > 0 ? 'extend' : 'shrink';

        fetch(`/api/reserve/${op}/${reservation.reservationId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            }
        })  .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        console.log(JSON.stringify(data.payload));
                        setReservation( data.payload);
                        showDialog("Complete!", "시간 조정 완료", false);
                    }else{
                        showDialog("Error",data.msg,false);
                    }
                }
            )
            .catch(error => {
                setDialogMessage(error.msg);
                showDialog('Error',error.msg, false);
            });
    }

    const saveUserMemo = (event) => {
        event.preventDefault();

        fetch(`/api/user/${customerInfo.id}/memo`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                id: customerInfo.id,
                memo: customerInfo.memo
            })
        })
            .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        showDialog("Complete!", "고객 메모 저장 완료.", false);
                    }
                }
            )
            .catch(error => {
                setDialogMessage(error.msg);
                showDialog('Error',error.msg, false);
            });
    }

    const saveReserve = (event) => {
        event.preventDefault();

        if( buttonClicked == false)
            buttonClicked = true;
        else
            return;

        let msg = validateInput();
        if( msg != "SUCCESS"){
            showDialog('Error',msg, false);
            buttonClicked = false;
            return;
        }
        let serviceIds: number[] = [];
        services.forEach( (service: HairService) => {
            if( service.selected ){
                serviceIds.push( service.serviceId);
            }
        });

        const reqDto = {
            startTime: reservation.startDate,
            designer: 1,
            memo: reservation.memo,
            serviceIds:serviceIds,
            requireSilence: reservation.requireSilence,
            fileIds:[]
        };

        if( fromServer && reservation.files && reservation.files.length > 0){
            reqDto.fileIds = reservation.files.map( file => file.id);
        }

        const formData = new FormData();
        localFiles.forEach(file => {
            formData.append('files', file);
        });

        formData.append('reservation', JSON.stringify(reqDto));

        let url = "/api/reserve/reservation";
        if( reservation.reservationId > 0 )
            url = url +"/"+reservation.reservationId;

        fetch(url, {
            method: reservation.reservationId > 0 ? "PUT" : "POST",
            body: formData,
        })
            .then(response =>response.json())
            .then(
                data => {
                    console.log("responseData : "+ JSON.stringify(data));
                    if( data.code == 200 ){
                        console.log("Registration Success!!");
                        setDialogTitle("Complete!");
                        setOpenDialog(true);
                        setDialogMessage( 'See you at ' + formatDate( data.payload.startDate));
                        setHasError(false);
                    }
                    else{
                        setDialogTitle("Error!");
                        setDialogMessage( data.msg);
                        setOpenDialog(true);
                        setHasError(true);
                    }
                    buttonClicked = false;
                }
            )
            .catch(error => {
                navigate(-1);
            });
    };

    const navigate = useNavigate();

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        const data = new FormData(event.currentTarget);
    };

    const handleMemoInput = e=>{
        setReservation({
            ...reservation,
            memo: e.target.value
        })
    }

    const handleUserMemoInput = e=>{
        setCustomerInfo({
            ...customerInfo,
            memo: e.target.value
        })

    }


    const handleTipInput = e=>{
        setReservation({
            ...reservation,
            tip: e.target.value
        })
    }

    const handleCheckbox = (e)=>{
        reservation.requireSilence = !reservation.requireSilence;
        setReservation({
            ...reservation,
        });
    }

    const formatPhoneNumber = (phoneNumberString: string) => {
        var cleaned = ('' + phoneNumberString).replace(/\D/g, '');
        var match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match) {
            return '(' + match[1] + ') ' + match[2] + '-' + match[3];
        }
        return null;
    }

    const formatDate = (dateStr: string) =>{
        return moment( dateStr,"YYYYMMDDTHH:mm").format('YYYY/MM/DD ddd HH:mm')
    }

    function saveTip(){
        if( reservation.reservationId == -1 )
            return;

        let url = `/api/reserve/reservation/${reservation.reservationId}/tip?tip=${reservation.tip}`;

        fetch(url, {
            method: "PUT"
        })
            .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        setDialogTitle("Complete!");
                        setOpenDialog(true);
                        setDialogMessage( 'Tip Saved!');
                        setHasError(true);
                    }
                    else{
                        setDialogTitle("Error!");
                        setDialogMessage( data.msg);
                        setOpenDialog(true);
                        setHasError(true);
                    }
                    buttonClicked = false;
                }
            )
    }

    function handleNoshow(){
        console.log('noshow')
    }

    const theme = useTheme();

    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <ReserveEditContainer direction="column" justifyContent="space-between">
                <Card variant="outlined" style={{overflowY:'scroll'}}>
                    <Header/>
                    <Typography
                        component="h6"
                        variant="h6"
                    >
                        {  formatDate( reservation.startDate )}
                    </Typography>

                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        sx={{display: 'flex', flexDirection: 'column', gap: 1}}
                    >
                        <Typography>
                            {reservation.userName} {formatPhoneNumber(reservation.phone)}
                        </Typography>

                        <Divider/>
                        <FormGroup>
                            {serviceCheckBoxes}
                        </FormGroup>
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
                                    onChange={handleCheckbox}
                                />}
                            label='조용히 시술받고 싶어요'
                        />

                        <section className="container">
                            <div {...getRootProps({className: 'dropzone', style: baseStyle})}>
                                <input {...getInputProps()} />
                                {!loginUser.rootUser &&
                                <p style={{'fontSize':'small'}} >원하시는 스타일 사진첨부 가능합니다 (최대 2장)</p>
                                }
                            </div>
                            <aside style={thumbsContainer}>
                                { fromServer ? serverThumbs : localThumbs}
                            </aside>
                        </section>

                        <Divider/>
                        <CardActions  sx={{
                            display: 'flex',
                            justifyContent: 'center',
                        }}>
                            <Button
                                type="submit"
                                size="large"
                                variant="contained"
                                onClick={saveReserve}
                                disabled={!reservation.editable || buttonClicked }
                            >
                                {reservation.reservationId > 0 ? '수정' : '예약'}
                            </Button>
                            <Button
                                size="large"
                                variant="contained"
                                onClick={() => setOpenCancelDialog(true)}
                                color="info"
                                disabled={!reservation.editable || reservation.reservationId == -1}
                            >
                                예약취소
                            </Button>
                            <Button
                                size="large"
                                variant="contained"
                                onClick={() => navigate(-1)}
                                color="info"
                            >
                                닫기
                            </Button>
                        </CardActions>
                        <Divider/>
                        {loginUser.rootUser && reservation.reservationId > 0 &&
                            <CardActions  sx={{
                                display: 'flex',
                                justifyContent: 'center',
                            }}>
                                { ((moment(reservation.endDate,'YYYYMMDDTHH:mm').toDate().getTime() - moment(reservation.startDate,'YYYYMMDDTHH:mm').toDate().getTime()) / 1000 / 60 ) +' 분 ' }
                                <Box
                                    sx={{
                                        display: 'flex',
                                        flexDirection: 'column',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        border: '0px solid #ccc',
                                        borderRadius: '0px',
                                        overflow: 'hidden',
                                        width:'40px'
                                    }}
                                >
                                <Button
                                    size="small"
                                    variant="contained"
                                    onClick={()=>adjustReservation(30)}
                                    disabled={!reservation.editable || reservation.reservationId == -1}
                                    id={'btn_increase'}

                                    sx={{
                                        borderRadius: 0,
                                        padding: '0px',
                                        height: '25%',
                                        maxWidth:'2px'
                                    }}
                                >
                                    <ArrowDropUpIcon color={"error"}/>
                                </Button>
                                <Button
                                    size="small"
                                    variant="contained"
                                    onClick={()=>adjustReservation(-30)}
                                    disabled={!reservation.editable || reservation.reservationId == -1}
                                    id={'btn_decrease'}
                                    sx={{
                                        borderRadius: 0,
                                        padding: '0px',
                                        height: '25%',
                                    }}
                                >
                                    <ArrowDropDownIcon />
                                </Button>
                                </Box>
                                <Button
                                    size="large"
                                    variant="contained"
                                    color="error"
                                    disabled={stampCount < 10}
                                    onClick={useCoupon}
                                    style={{
                                        marginLeft:'15px',
                                        marginRight:'15px',
                                        padding: '0px',
                                        height:'100%'
                                    }}
                                >
                                    쿠폰사용
                                </Button>
                                <TextField
                                    type="number"
                                    variant="outlined"
                                    placeholder={"tip"}
                                    value={reservation.tip}
                                    onChange={handleTipInput}
                                    sx={{
                                        width: '80px',
                                        marginLeft:'25px'
                                    }}
                                />
                                <Button
                                    size="large"
                                    color="success"
                                    variant="contained"
                                    onClick={saveTip}

                                >Tip</Button>
                            </CardActions>
                        }
                        {loginUser.rootUser &&  reservation.reservationId > 0 &&
                            <TextField
                                placeholder="memo"
                                multiline
                                rows={4}
                                value={customerInfo.memo}
                                onChange={handleUserMemoInput}
                            />

                        }
                        {loginUser.rootUser && reservation.reservationId > 0 &&
                            <CardActions  sx={{
                                display: 'flex',
                                justifyContent: 'center',
                            }}>
                            <Button
                                size="medium"
                                variant="contained"
                                onClick={saveUserMemo}
                            >
                                메모저장
                            </Button>
                                <Button
                                    size="medium"
                                    variant="contained"
                                    onClick={() => setOpenYesNoDialog(true)}
                                    >
                                No Show
                                </Button>
                            </CardActions>
                        }
                        { loginUser.rootUser && reservation.userId > 0 &&
                            <ReservationHistoryTable
                                userId={reservation.userId.toString()}
                            />
                        }

                    </Box>
                </Card>
                <Dialog
                    open={openDialog}
                    onClose={handleCloseDialog}
                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                >
                    <DialogTitle id="alert-dialog-title">
                        {dialogTitle}
                    </DialogTitle>
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            {dialogMessage}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleCloseDialog} autoFocus>
                            Confirm
                        </Button>
                    </DialogActions>
                </Dialog>
                <Dialog
                    open={openCancelDialog}
                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                >
                    <DialogTitle id="alert-dialog-title">
                        {cancelDialogTitle}
                    </DialogTitle>
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            {cancelDialogMessage}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={cancelReservation} autoFocus>
                            Yes
                        </Button>
                        <Button onClick={closeCancelDialog} autoFocus>
                            No
                        </Button>
                    </DialogActions>
                </Dialog>

                { loginUser.rootUser && yesNoDialog({
                    title:'NoShow',
                    message:'Noshow 처리하시겠습니까?',
                    yesCallback: handleNoshow,
                    noCallback:null
                })}

                { openPreviewDialog ?

                <Dialog
                    open={openPreviewDialog}
                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                >
                    <DialogTitle id="alert-dialog-title">
                        {previewFile.userFileName}
                    </DialogTitle>
                    <DialogContent>
                        <img src={previewFile.url} width={'100%'}></img>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={ ()=>setOpenPreviewDialog(false) } autoFocus>
                            OK
                        </Button>
                    </DialogActions>

                </Dialog>
                    :
                    <></>
                }
            </ReserveEditContainer>
        </AppProvider>
    );
}