import {styled, useTheme} from "@mui/material/styles";
import MuiCard from "@mui/material/Card";
import Stack from "@mui/material/Stack";
import {AppProvider} from "@toolpad/core/AppProvider";
import CssBaseline from "@mui/material/CssBaseline";
import Typography from "@mui/material/Typography";
import moment from 'moment'


import * as React from "react";
import {useNavigate} from "react-router";
import Footer from "./components/Footer";
import {Reservation, HairService} from './typedef';
import {Table, TableBody, TableCell, TableHead, TablePagination, TableRow} from "@mui/material";
import {useEffect} from "react";

interface Column {
    id: 'startDate' | 'services' | 'status';
    label: string;
    minWidth?: number;
    align?: 'right';
    format?: (value: string) => string;
}


const columns: readonly Column[] = [
    { id: 'startDate', label: 'Date', minWidth: 120,
      format: (value: string) =>  moment(value,'YYYYMMDDTHH:mm').format('YYYY/MM/DD') ,
    },
    { id: 'services', label: 'Services', minWidth: 140}
];

const Card = styled(MuiCard)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(1),
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

const ReserveHistoryContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(2),
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


export default function ReserveHistory() {
    const theme = useTheme();
    const navigate = useNavigate();

    const [page, setPage] = React.useState(0);
    const PAGE_PER_SIZE = 100;
    const [rows, setRows] = React.useState<Reservation[]>([]);


    const fetchData = (page = 0  ) => {
        const url = `/api/reserve/history?page=${page}&size=${PAGE_PER_SIZE}`;

        fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response =>response.json())
            .then(
                data => {
                    if( data.code == 200 ){
                        if( data.payload.content.length > 0) {
                            let newRow = [] as Reservation[];
                            data.payload.content.map((item: Reservation) => {
                                newRow.push({
                                    startDate: item.startDate,
                                    services: item.services.map(service => service.name).join(", "),
                                    status: item.status
                                })
                            });
                            setRows(newRow);
                        }else{
                            setRows([]);
                        }
                    }
                }
            )
            .catch(error => console.error("Error:", error));

    }

    useEffect(() => {
        fetchData( page);
    },[page] )

    return (
        <AppProvider theme={theme}>
            <CssBaseline enableColorScheme />
            <ReserveHistoryContainer direction="column" justifyContent="space-between">
                <img src={'./dorothy.png'} alt={'Dorothy'}/>

                <Typography
                    component="h6"
                    variant="h6"
                >
                    Reservation History
                </Typography>

                <Card variant="outlined" style={{overflowY: 'scroll'}}>
                    <Table stickyHeader aria-label="sticky table">
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell
                                        key={column.id}
                                        align={column.align}
                                        style={{minWidth: column.minWidth}}
                                    >
                                        {column.label}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows
                                .slice(page * PAGE_PER_SIZE, page * PAGE_PER_SIZE + PAGE_PER_SIZE)
                                .map((row, index) => {
                                    return (
                                        <TableRow hover role="checkbox" tabIndex={-1} key={row.startDate} sx={{
                                            backgroundColor: index % 2 === 0 ? '#ffffff' : '#fcf3cf', // White for even, light gray for odd
                                        }}>
                                            {columns.map((column) => {
                                                const value = row[column.id];
                                                return (
                                                    <TableCell key={column.id} align={column.align}>
                                                        {column.format ? column.format(value) : value}
                                                    </TableCell>
                                                );
                                            })}
                                        </TableRow>
                                    );
                                })}
                        </TableBody>
                    </Table>


                </Card>
                <Footer backUrl="BACK" showMyInfo={false}></Footer>
            </ReserveHistoryContainer>
        </AppProvider>
    );
}