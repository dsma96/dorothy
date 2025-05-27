import React, {useEffect} from "react";
import {Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TablePagination} from "@mui/material";
import moment from "moment";
import {Member, Reservation} from "../typedef";
import Typography from "@mui/material/Typography";
import {useSelector} from "react-redux";
interface Column {
    id: string;
    label: string;
    minWidth?: number;
    align?: 'right';
    format?: (value: string) => string;
}

interface ReservationHistoryTableProps {

    userId?: string
}

const ReservationHistoryTable: React.FC<ReservationHistoryTableProps> = ({userId}) => {
    const loginUser: Member = useSelector(state => state.user.loginUser);

    const PAGE_PER_SIZE = 100;
    const [rows, setRows] = React.useState<Reservation[]>([]);

    const columns = [
        {
            id: "startDate",
            label: "Date",
            minWidth: 50,
            format: (value: string) => moment(value, "YYYYMMDDTHH:mm").format("YY/MM/DD"),
            align:"center"
        },
        { id: "services", label: "Services", minWidth: 120 },
    ];

    if( loginUser.rootUser){
        columns.push({
            id: "tip",
            label: "tip",
            minWidth: 50
        });
    }

    const fetchData = (userId: string, page = 0) => {
        const url = `/api/reserve/history?page=${page}&size=${PAGE_PER_SIZE}&userId=${userId}`;
        if (!userId || userId.trim() === "") {
            console.error("User ID is required to fetch reservation history.");
            return;
        }
        fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.code === 200) {
                    if (data.payload.content.length > 0) {
                        const newRow = data.payload.content.map((item: Reservation) => ({
                            startDate: item.startDate,
                            services: item.services.map((service) => service.name).join(", "),
                            status: item.status,
                            tip: item.tip
                        }));
                        setRows(newRow);
                    } else {
                        setRows([]);
                    }
                }
            })
            .catch((error) => console.error("Error:", error));
    };

    useEffect(() => {
        fetchData(userId, 0);
    }, []);

    return (
        <>
            <TableContainer style={{ maxHeight: 'calc(100vh - 200px)', overflowY: 'auto' }}>
                <Table stickyHeader aria-label="sticky table">
                    {rows.length > 0 &&
                    <TableHead>
                        <TableRow>
                            {columns.map((column) => (
                                <TableCell
                                    key={column.id}
                                    align={column.align}
                                    style={{ minWidth: column.minWidth }}
                                    sx={{
                                        backgroundColor: '#1976d2', // Custom background color
                                        color: '#ffffff', // Custom text color
                                        fontWeight: 'bold', // Optional: Make text bold
                                    }}
                                >
                                    {column.label}
                                </TableCell>
                            ))}
                        </TableRow>
                    </TableHead>
                    }
                    <TableBody>
                        {rows
                            .map((row, index) => (
                                <TableRow
                                    hover
                                    role="checkbox"
                                    tabIndex={-1}
                                    key={row.startDate}
                                    sx={{
                                        backgroundColor: index % 2 === 0 ? '#ffffff' : '#fcf3cf',
                                    }}
                                >
                                    {columns.map((column) => {
                                        const value = row[column.id];
                                        return (
                                            <TableCell key={column.id} align={column.align}>
                                                {column.format ? column.format(value) : value}
                                            </TableCell>
                                        );
                                    })}
                                </TableRow>
                            ))}
                    </TableBody>

                </Table>
                {rows.length ==0 &&
                    <Typography
                        component="h4"
                        variant="h4"
                        sx={{width: '100%', fontSize: 'clamp(0.9rem, 9vw, 0.9rem)', lineHeight: 1}}
                    >
                        New Guest!
                    </Typography>
                }
            </TableContainer>

        </>
    );
};

export default ReservationHistoryTable;