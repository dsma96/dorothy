import {styled, useTheme} from "@mui/material/styles";
import MuiCard from "@mui/material/Card";
import Stack from "@mui/material/Stack";
import {AppProvider} from "@toolpad/core/AppProvider";
import CssBaseline from "@mui/material/CssBaseline";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import {MuiTelInput} from "mui-tel-input";
import TextField from "@mui/material/TextField";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Button from "@mui/material/Button";
import Link from "@mui/material/Link";
import Divider from "@mui/material/Divider";
import * as React from "react";

const Card = styled(MuiCard)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(4),
    gap: theme.spacing(2),
    margin: 'auto',
    [theme.breakpoints.up('sm')]: {
        maxWidth: '450px',
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

export default function Home() {
    const theme = useTheme();
    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <HomeContainer direction="column" justifyContent="space-between">
                <Card variant="outlined">
                    <img src={'./dorothy.png'} alt={'Dorothy Hairshop'}/>

                    <Typography
                        component="h1"
                        variant="h4"
                        sx={{width: '100%', fontSize: 'clamp(2rem, 10vw, 2.15rem)'}}
                    >
                        Dorothy 쌤
                    </Typography>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                        >
                            예약하기
                        </Button>
                    <Divider></Divider>
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                    >
                        찾아오는 길
                    </Button>
                    <Divider></Divider>
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                    >
                        서비스 소개
                    </Button>
                    
                    <Box sx={{display: 'flex', flexDirection: 'column', gap: 2}}>

                        <Typography sx={{textAlign: 'center'}}>
                            Don&apos;t have an account?{' '}
                            <Link
                                href="/material-ui/getting-started/templates/sign-in/"
                                variant="body2"
                                sx={{alignSelf: 'center'}}
                            >
                                Sign up
                            </Link>
                        </Typography>
                    </Box>
                </Card>
            </HomeContainer>
        </AppProvider>
    );
}