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

export default function MemberList() {
    const [users, setUsers] = useState<Member[]>([]);
    const [page, setPage] = useState(0);
    const [sortField, setSortField] = useState("createDate");
    const [sortDirection, setSortDirection] = useState<"asc" | "desc">("desc");
    const [loading, setLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true);
    const navigate = useNavigate();

    const fetchUsers = () => {
        if (loading || !hasMore) return;

        setLoading(true);
        const url = `/api/user/list?page=${page}&size=20&sort=${sortField},${sortDirection}`;
        fetch(url)
            .then((response) => response.json())
            .then((data) => {
                if (data.code === 200) {
                    setUsers((prevUsers) => [...prevUsers, ...data.payload.content]);
                    setHasMore(data.payload.content.length > 0);
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
        <Paper>
            <TableContainer
                onScroll={(event) => {

                    let { scrollTop, scrollHeight, clientHeight } = event.currentTarget;
                    scrollTop = Math.floor(scrollTop);
                    if (scrollHeight - scrollTop === clientHeight && hasMore) {
                        setPage((prevPage) => prevPage + 1);
                    }else{
//                        alert("sh:"+scrollHeight +"st: "+scrollTop +"ch: "+clientHeight+" HM:"+hasMore); // Debugging
                    }
                }}
                style={{
                    maxHeight: '81vh',
                    overflowY: "scroll", // Ensure scrolling is enabled
                    WebkitOverflowScrolling: "touch", // Enable smooth scrolling on iOS
                    touchAction: "auto", // Allow touch gestures for scrolling
                    position: 'relative', // Ensure proper layout
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
            <Footer />
        </Paper>
    );
}