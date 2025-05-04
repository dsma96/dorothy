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
import { Member } from "./typedef";
import Footer from "./components/Footer";
import {styled} from "@mui/material/styles";
import Stack from "@mui/material/Stack";

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
    const [users, setUsers] = useState<Member[]>([]);
    const [page, setPage] = useState(0);
    const [sortField, setSortField] = useState("createDate");
    const [sortDirection, setSortDirection] = useState<"asc" | "desc">("desc");
    const [loading, setLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true);
    const navigate = useNavigate();

    const fetchUsers = () => {
        if ( !hasMore) return;
        console.log(`Fetching users: page=${page}, sortField=${sortField}, sortDirection=${sortDirection}`);
        setLoading(true);
        const url = `/api/user/list?page=${page}&size=25&sort=${sortField},${sortDirection}`;
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

    return (
        <MemberListContainer>
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
                                ID
                            </TableCell>
                            <TableCell>
                                <TableSortLabel
                                    active={sortField === "name"}
                                    direction={sortDirection}
                                    onClick={() => handleSort("userName")}
                                >
                                    Name
                                </TableSortLabel>
                            </TableCell>
                            <TableCell>Phone</TableCell>
                            <TableCell>
                                <TableSortLabel
                                    active={sortField === "가입일"}
                                    direction={sortDirection}
                                    onClick={() => handleSort("createDate")}
                                >
                                    가입일
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
                                    onClick={() => handleUserClick(user.id)}
                                >
                                    {user.name}
                                </TableCell>
                                <TableCell
                                    style={{ padding:'4px' }}
                                    onClick={()=> {window.location.href=`sms:${user.phone}`}}
                                >
                                    {`${user.phone.substring(0,3)}-${user.phone.substring(3,6)}-${user.phone.substring(6)}`}
                                </TableCell>
                                <TableCell
                                    style={{ padding:'4px' }}
                                >{user.createDate}</TableCell>
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