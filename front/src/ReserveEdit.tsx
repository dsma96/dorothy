import * as React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CssBaseline from '@mui/material/CssBaseline';

import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import MuiCard from '@mui/material/Card';
import {styled, useTheme} from '@mui/material/styles';
import { AppProvider } from '@toolpad/core/AppProvider';
import {CardActions, FormGroup} from "@mui/material";
import FormControlLabel from "@mui/material/FormControlLabel";
import type {Member} from 'type'
import { useSelector, useDispatch } from 'react-redux';
import {useNavigate} from "react-router";
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

export default function ReserveEdit(props: { disableCustomTheme?: boolean }) {
    const loginUser: Member = useSelector( state => state.user);

    const validateInputs = () => {

        return true;
    };

    const navigate = useNavigate();

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {

        const data = new FormData(event.currentTarget);
        console.log({
            name: data.get('name'),
            lastName: data.get('lastName'),
            email: data.get('email'),
            password: data.get('password'),
        });
    };

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
                        2025/01/24 09:30
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
                            {loginUser.name} {loginUser.phone}
                        </Typography>
                        <FormGroup>
                            <FormControlLabel disabled control={<Checkbox defaultChecked />} label="남자헤어컷" />
                            <FormControlLabel  control={<Checkbox />} label="샴푸+드라이" />
                            <FormControlLabel  control={<Checkbox />} label="다운펌" />
                            <FormControlLabel  control={<Checkbox />} label="세치염색" />
                        </FormGroup>
                        {/*<FormControlLabel*/}
                        {/*    control={<Checkbox value="allowExtraEmails" color="primary" />}*/}
                        {/*    label="I want to receive "*/}
                        {/*/>*/}
                        <CardActions >
                        <Button
                            type="submit"
                            size="large"
                            variant="contained"
                            onClick={validateInputs}
                        >
                            Done
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