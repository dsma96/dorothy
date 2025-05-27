import React, { useEffect } from "react";
import { styled, useTheme } from "@mui/material/styles";
import { AppProvider } from "@toolpad/core/AppProvider";
import CssBaseline from "@mui/material/CssBaseline";
import Typography from "@mui/material/Typography";
import moment from "moment";
import { useNavigate } from "react-router";
import { useSearchParams } from "react-router-dom";
import Header from "./components/Header";
import Footer from "./components/Footer";
import ReservationHistoryTable from "./components/ReservationHistoryTable";
import { Reservation } from "./typedef";

const ReserveHistoryContainer = styled("div")(({ theme }) => ({
    height: "calc((1 - var(--template-frame-height, 0)) * 100dvh)",
    minHeight: "100%",
    padding: theme.spacing(1),
    "&::before": {
        content: '""',
        display: "block",
        position: "absolute",
        zIndex: -1,
        inset: 0,
        backgroundImage:
            "radial-gradient(ellipse at 50% 50%, hsl(210, 100%, 97%), hsl(0, 0%, 100%))",
        backgroundRepeat: "no-repeat",
    },
}));

export default function ReserveHistory() {
    const theme = useTheme();

    const [searchParams] = useSearchParams();

    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <ReserveHistoryContainer>
                <Header />
                <Typography component="h6" variant="h6">
                    Reservation History
                </Typography>
                <ReservationHistoryTable
                    userId={searchParams.get("userId") ? searchParams.get("userId") : ''}
                />
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
            </ReserveHistoryContainer>
        </AppProvider>
    );
}