import * as React from "react";
import {useNavigate} from "react-router";
import Footer from "./components/Footer";
import {Member, Reservation, Stamp} from './typedef';
import {useEffect, useState} from "react";
import {styled, useTheme} from "@mui/material/styles";
import MuiCard from "@mui/material/Card";
import Stack from "@mui/material/Stack";
import CssBaseline from "@mui/material/CssBaseline";
import {AppProvider} from "@toolpad/core/AppProvider";
import Box from "@mui/material/Box";
import {Container} from "@mui/material";
import Button from "@mui/material/Button";
import moment from 'moment'
import Typography from "@mui/material/Typography";
import {useSelector} from "react-redux";
import Divider from "@mui/material/Divider";
import Header from "./components/Header";
interface StampItemProps{
    stamped: boolean; // back button url
    date?: string;
}

const StampItem = ( {stamped, date}:StampItemProps)=>{
    const WIDTH = 72;
    const HEIGHT = 72;
    return(
        <Container sx={{
            display:'flex',
            flexDirection:'column',
            flexBasis:WIDTH,
            justifyContent:'space-evenly'
        }}>
        <Box component='img'
             src={stamped ? '/stamp.png' : 'empty_stamp.png'}
             sx={{
                 height:WIDTH,
                 width:HEIGHT,
             }}
        />
            <Box>{  date }</Box>
        </Container>
    )
}


const Card = styled(MuiCard)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(1),
    gap: theme.spacing(1),
    margin: 'auto',
    flexGrow:1,
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

const StampContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(1),
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



export default function Stamps() {
    const theme = useTheme();
    const loginUser: Member = useSelector(state => state.user.loginUser);
    const [stamps, setStamps] = useState <Stamp[]>([]);

    useEffect(()=>{
        fetch(`/api/coupon/${loginUser.id}/stamps`)
            .then((response) => response.json())
            .then((data) => {
                setStamps( data.payload );
            })
            .catch((error) => {
                console.error('Error fetching services:', error);
            });
    },[])

    const stampComp  = ( )=>{
        let sc: JSX.Element[]   = [];

        stamps.forEach((stamp,index) =>{
            if( stamp.stampCount > 0 ){
                sc.push(<StampItem key={index} stamped={stamp.stampCount > 0} date={stamp.serviceDate}/>);
            }
        });

        for( let i = sc.length ; i < 10; i++){
            sc.push(<StampItem key={i} stamped={false}/>);
        }

        return (
            <Container sx={{display:'flex', flexDirection: 'column', justifyContent:'space-evenly', flexGrow:1}}>
                <Container sx={{display:'flex', flexDirection: 'row'}}>
                    {
                        sc.slice(0,3)
                    }
                </Container>
                <Container sx={{display:'flex', flexDirection: 'row' , justifyContent:'space-evenly'}}>
                    {
                        sc.slice(3,5)
                    }
                </Container>
                <Container sx={{display:'flex', flexDirection: 'row',justifyContent:'space-evenly'}}>
                    {
                        sc.slice(5,8)
                    }
                </Container>
                <Container sx={{display:'flex', flexDirection: 'row',justifyContent:'space-evenly'}}>
                    {
                        sc.slice(8)
                    }
                </Container>
            </Container>
        )
    }

    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme/>
            <StampContainer direction="column" justifyContent="space-between">
                <Header/>
                <Typography component="h5" variant="h5"> My Stamp</Typography>
                <Card variant="outlined">
                {stampComp()}
                    <Card
                        variant="outlined"
                        sx={{
                            textAlign: 'left', // Align text to the left
                            padding: 2, // Optional: Add padding for better spacing
                        }}
                    >
                    * 10개 적립시, 남자커트 1회 무료입니다.<br/>
                    * 익일 적립됩니다<br/>
                    * 가족 합산적용되지 않고, 1일 1건만 적립됩니다.<br/>

                    </Card>
                </Card>
                <Footer backUrl="BACK" showMyStamp={false}></Footer>
            </StampContainer>
        </AppProvider>
    );
}

