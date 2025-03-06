import * as React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import MuiCard from '@mui/material/Card';
import {styled, useTheme} from '@mui/material/styles';
import {CardActions, FormGroup} from "@mui/material";
import FormControlLabel from "@mui/material/FormControlLabel";

import { AppProvider } from '@toolpad/core/AppProvider';
import type {Member} from 'type'
import { useSelector, useDispatch } from 'react-redux';
import {useNavigate} from "react-router";
import { useSearchParams } from "react-router-dom";
import moment from 'moment'
import {TIME_UNIT} from './TimeTable';
import TextField from "@mui/material/TextField";

const Card = styled(MuiCard)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(4),
    gap: theme.spacing(2),
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

const SignUpContainer = styled(Stack)(({ theme }) => ({
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

export default function ReserveEdit(props: { disableCustomTheme?: boolean , startDate : Date, endDate : Date}) {
    const loginUser: Member = useSelector(state => state.user);
    let [searchParams] = useSearchParams();
    let startDate: Date = new Date();

    if (searchParams.get("start")) {
        console.log(searchParams.get("start"));
        startDate = moment(searchParams.get("start"),"YYYYMMDD HH:mm");
    }

    const validateInputs = () => {

        return true;
    };

    const navigate = useNavigate();

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {

        const data = new FormData(event.currentTarget);

    };

    const formatPhoneNumber = (phoneNumberString: string) => {
        var cleaned = ('' + phoneNumberString).replace(/\D/g, '');
        var match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match) {
            return '(' + match[1] + ') ' + match[2] + '-' + match[3];
        }
        return null;
    }

    const theme = useTheme();
    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <SignUpContainer direction="column" justifyContent="space-between">
                <Card variant="outlined">
                    <img src={'../public/dorothy.png'} alt={'Dorothy Hairshop'}/>
                    <Typography
                        component="h1"
                        variant="h4"
                        sx={{width: '100%', fontSize: 'clamp(2rem, 10vw, 2.15rem)'}}
                    >
                        Reservation
                    </Typography>
                    <Typography
                        component="h4"
                        variant="h4"
                        sx={{width: '100%', fontSize: 'clamp(1rem, 10vw, 1.15rem)'}}
                    >
                        { moment( startDate ).format('YYYY/MM/DD ddd HH:mm')}
                    </Typography>

                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        sx={{display: 'flex', flexDirection: 'column', gap: 2}}
                    >
                        <Typography
                            component="h4"
                            variant="h4"
                            sx={{width: '100%', fontSize: 'clamp(1rem, 10vw, 1.15rem)'}}
                        >
                            {loginUser.name} { formatPhoneNumber( loginUser.phone)}
                        </Typography>
                        <FormGroup>
                            <FormControlLabel disabled control={<Checkbox defaultChecked />} label="남자헤어컷" />
                            <FormControlLabel  control={<Checkbox />} label="샴푸+드라이" />
                            <FormControlLabel  control={<Checkbox />} label="다운펌" />
                            <FormControlLabel  control={<Checkbox />} label="세치염색" />
                        </FormGroup>
                        <TextField
                            placeholder="Please leave your extra requirements"
                            multiline
                            rows={2}
                            maxRows={4}
                        />
                        <CardActions >
                        <Button
                            type="submit"
                            size="large"
                            variant="contained"
                            onClick={validateInputs}
                        >
                            Reserve
                        </Button>
                        <Button
                            size="large"
                            variant="contained"
                            onClick={() => navigate(-1)}
                            color="info"
                        >
                            Cancel
                        </Button>
                        </CardActions>
                    </Box>

                </Card>
            </SignUpContainer>
        </AppProvider>
    );
}