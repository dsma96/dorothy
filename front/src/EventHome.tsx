import {styled, useTheme} from "@mui/material/styles";
import MuiCard from "@mui/material/Card";
import Stack from "@mui/material/Stack";
import {AppProvider} from "@toolpad/core/AppProvider";
import CssBaseline from "@mui/material/CssBaseline";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import {useNavigate} from "react-router";
import * as React from "react";
import Button from "@mui/material/Button";
import Divider from "@mui/material/Divider";
import Link from "@mui/material/Link";
import {CardMedia} from "@mui/material";


const Card = styled(MuiCard)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(0),
    gap: theme.spacing(0),
    margin: 'auto',
    [theme.breakpoints.up('sm')]: {
        maxWidth: '460px',
    },
    boxShadow:
        'hsla(220, 30%, 5%, 0.05) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.05) 0px 15px 35px -5px',
    ...theme.applyStyles('dark', {
        boxShadow:
            'hsla(220, 30%, 5%, 0.5) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.08) 0px 15px 35px -5px',
    }),
}));

const HomeContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(0),
    [theme.breakpoints.up('sm')]: {
        padding: theme.spacing(0),
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


export default function EventHome() {
    const theme = useTheme();
    const navigate = useNavigate();

    const gotoTimetable  = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        navigate("/time");
    }

    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <HomeContainer direction="column" justifyContent="space-between">
                <Card variant="outlined" style={{overflowY:'scroll'}}>
                    <CardMedia component={"img"}
                    image={"./dorothy_home.jpeg"}
                   useMap="#image-map"
                    />
                    <map name="image-map">
                        <area shape="circle" coords="541,635,270,907" href="/dateChoose"/>
                    </map>
                </Card>
            </HomeContainer>
        </AppProvider>
    );
}