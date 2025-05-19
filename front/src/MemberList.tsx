import React, { useEffect, useState } from "react";
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TableSortLabel,
    Paper,
    CircularProgress,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { MemberStat } from "./typedef";
import Footer from "./components/Footer";
import {styled} from "@mui/material/styles";
import Stack from "@mui/material/Stack";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const MemberListContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(1),
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

export default function MemberList() {
    const [users, setUsers] = useState<MemberStat[]>([]);
    const [page, setPage] = useState(0);
    const [sortField, setSortField] = useState("userId");
    const [sortDirection, setSortDirection] = useState<"asc" | "desc">("desc");
    const [loading, setLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true);
    const navigate = useNavigate();

    const fetchUsers = () => {
        if ( !hasMore) return;
        console.log(`Fetching users: page=${page}, sortField=${sortField}, sortDirection=${sortDirection}`);
        setLoading(true);
        const url = `/api/user/stat?page=${page}&size=25&sort=${sortField},${sortDirection}`;
        fetch(url)
            .then((response) => response.json())
            .then((data) => {
                if (data.code === 200) {
                    setUsers((prevUsers) => [...prevUsers, ...data.payload.content]);
                    setHasMore(!data.payload.last);
                }
            })
            .catch((error) => console.error("Error fetching users:", error))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchUsers();
    }, [page, sortField, sortDirection]);

    const handleSort = (field: string) => {
        const isAsc = sortField === field && sortDirection === "asc";
        setSortDirection(isAsc ? "desc" : "asc");
        setSortField(field);
        setPage(0);
        setUsers([]);
        setHasMore(true);
    };


    const handleUserClick = (userId: number) => {
        navigate(`/reserveHistory?userId=${userId}`);
    };

    const handleNameClick = (memo: string) => {
        if( !memo || memo.length < 1) return;

        toast.info(memo , {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            style: {
                backgroundColor: "#FFFB87", // Post-it yellow
                color: "#000", // Black text for contrast
                fontWeight: "bold",
            },
        });
    };

    return (
        <MemberListContainer>
        <ToastContainer
            style={{
                top: "50%",
                left: "50%",
                transform: "translate(-50%, -50%)",
                position: "fixed"
            }}
        />
            <Paper>
            <TableContainer
                onScroll={(event) => {

                    let { scrollTop, scrollHeight, clientHeight } = event.currentTarget;
                    scrollTop = Math.floor(scrollTop);
                    if (scrollHeight - scrollTop >= (clientHeight  ) && hasMore) {
                        setPage((prevPage) => prevPage + 1);
                    }else{
//                        alert("sh:"+scrollHeight +"st: "+scrollTop +"ch: "+clientHeight+" HM:"+hasMore); // Debugging
                    }
                }}
                style={{
                    maxHeight: 'calc(100vh - 64px )',
                    overflowY: "auto", // Ensure scrolling is enabled
                    WebkitOverflowScrolling: "touch", // Enable smooth scrolling on iOS
                    touchAction: "auto", // Allow touch gestures for scrolling
                    position: 'relative', // Ensure proper layout
                    marginLeft:'10px'
                }}
            >
                <Table stickyHeader

                >
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                <TableSortLabel
                                    active={sortField === "userId"}
                                    direction={sortDirection}
                                    onClick={() => handleSort("userId")}
                                >
                                ID
                                </TableSortLabel>
                            </TableCell>
                            <TableCell>
                                <TableSortLabel
                                    active={sortField === "Name"}
                                    direction={sortDirection}
                                    onClick={() => handleSort("userName")}
                                >
                                    Name
                                </TableSortLabel>
                            </TableCell>
                            <TableCell >
                                <TableSortLabel
                                    active={sortField === "r.reservationCount"}
                                    direction={sortDirection}
                                    onClick={() => handleSort("r.reservationCount")}
                                >
                                CNT
                                </TableSortLabel>
                            </TableCell>
                            <TableCell>
                                <TableSortLabel
                                    active={sortField === "createDate"}
                                    direction={sortDirection}
                                    onClick={() => handleSort("createDate")}
                                >
                                    Join
                                </TableSortLabel>
                            </TableCell>
                            <TableCell>
                                <TableSortLabel
                                    active={sortField === "lastDate"}
                                    direction={sortDirection}
                                    onClick={() => handleSort("r.lastVisitDate")}
                                >
                                    Last
                                </TableSortLabel>

                            </TableCell>
                            <TableCell>
                                <TableSortLabel
                                    active={sortField === "firstDate"}
                                    direction={sortDirection}
                                    onClick={() => handleSort("r.fistVisitDate")}
                                >
                                    First
                                </TableSortLabel>
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.map((user, index) => (
                            <TableRow
                                key={user.id}
                                style={{
                                    backgroundColor: index % 2 === 1 ? "#f5f5f5" : "inherit",
                                }}
                            >
                                <TableCell
                                    style={{ padding:'4px' }}
                                >
                                    {user.id}
                                </TableCell>
                                <TableCell
                                    style={{ padding:'4px' }}
                                    onClick={() => handleNameClick(user.memo || "")}
                                >
                                    {user.name}
                                </TableCell>
                                <TableCell
                                    style={{ padding:'0px', minWidth: '2em' }}
                                    onClick={() => handleUserClick(user.id)}
                                >
                                    {user.reservationCount}
                                </TableCell>
                                <TableCell
                                    style={{ padding:'4px' }}
                                >{user.createDate}</TableCell>
                                <TableCell>
                                    {user.lastDate}
                                </TableCell>
                                <TableCell>
                                    {user.firstDate}
                                </TableCell>

                            </TableRow>
                        ))}
                        {loading && (
                            <TableRow>
                                <TableCell colSpan={5} align="center">
                                    <CircularProgress size={24} />
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

        </Paper>
            <Footer style={{
                position: 'fixed',
                bottom: 0,
                left: 0,
                right: 0,
                backgroundColor: '#fff',
                zIndex: 1,
            }}
                    backUrl={"/"}
            />
        </MemberListContainer>
    );
}