import React, { useEffect, useState } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Typography } from '@mui/material';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';
import Footer from "./components/Footer";

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

interface StatData {
    period: string;
    totalCount: number;
    totalSale: number;
    manCount: number;
    manSale: number;

    womanCount: number;
    womanSale: number;

}

const Statistics: React.FC = () => {
    const [stats, setStats] = useState<StatData[]>([]);
    const [loading, setLoading] = useState(true);
    let now = new Date();
    useEffect(() => {
        fetch(`/api/stat/${now.getFullYear()}/monthly`)
            .then((response) => response.json())
            .then((data) => {
                setStats(data.payload);
                setLoading(false);
            })
            .catch((error) => {
                console.error('Error fetching statistics:', error);
                setLoading(false);
            });
    }, []);

    if (loading) {
        return <Typography>Loading...</Typography>;
    }

    const chartData = {
        labels: stats.map((stat) => stat.period),
        datasets: [
            {
                label: 'Total Sales',
                data: stats.map((stat) => stat.totalSale),
                borderColor: 'rgba(75, 192, 192, 1)',
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                fill: true,
            },
            {
                label: 'Man Sales',
                data: stats.map((stat) => stat.manSale ),
                borderColor: 'rgba(153, 102, 255, 1)',
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                fill: true,
            },
            {
                label: 'Woman Sales',
                data: stats.map((stat) => stat.womanSale),
                borderColor: 'rgba(255, 159, 64, 1)',
                backgroundColor: 'rgba(255, 159, 64, 0.2)',
                fill: true,
            },
        ],
    };

    return (
        <div style={{ padding: '10px' }}>
            <Typography variant="h5" gutterBottom>
                월별 통계
            </Typography>

            <div style={{ overflowX: 'auto', width: '100%' }}>
                <div >
                    <Line
                        data={chartData}
                        options={{
                            responsive: true,
                            plugins: {
                                legend: {
                                    position: 'top',
                                },
                                title: {
                                    display: false,
                                    text: 'Monthly Sales Statistics',
                                },
                            },
                            maintainAspectRatio: false,
                        }}
                        height={250}
                    />
                </div>
            </div>

            {/* Table */}
            <Paper style={{ width: '100%', overflowX: 'auto', marginTop: '10px' }}>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell   style={{ padding:'4px' }}>Period</TableCell>
                                <TableCell   style={{ padding:'4px' }}>Total Count</TableCell>
                                <TableCell   style={{ padding:'4px' }}>Total Sale</TableCell>
                                <TableCell   style={{ padding:'4px' }}>Man Count</TableCell>
                                <TableCell   style={{ padding:'4px' }}>Man Sale</TableCell>
                                <TableCell   style={{ padding:'4px' }}>Woman Count</TableCell>
                                <TableCell   style={{ padding:'4px' }}>Woman Sale</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {stats.map((stat, index) => (
                                <TableRow key={index}
                                          style={{
                                              backgroundColor: index % 2 === 0 ? '#f5f5f5' : 'inherit', // Light gray for even rows
                                          }}
                                >
                                    <TableCell    style={{ padding:'4px' }}>{stat.period}</TableCell>
                                    <TableCell    style={{ padding:'4px' }}>{stat.totalCount}</TableCell>
                                    <TableCell   style={{ padding:'4px' }}>{stat.totalSale +'$'}</TableCell>
                                    <TableCell   style={{ padding:'4px' }}>{stat.manCount}</TableCell>
                                    <TableCell   style={{ padding:'4px' }}>{stat.manSale+'$'}</TableCell>
                                    <TableCell   style={{ padding:'4px' }}>{stat.womanCount}</TableCell>
                                    <TableCell   style={{ padding:'4px' }}>{stat.womanSale + '$'}</TableCell>

                                </TableRow>
                            ))}
                            <TableRow style={{ backgroundColor: '#e0e0e0', fontWeight: 'bold' }}>
                                <TableCell style={{ padding: '4px' }}>Total</TableCell>
                                <TableCell style={{ padding: '4px' }}>
                                    {stats.reduce((sum, stat) => sum + stat.totalCount, 0)}
                                </TableCell>
                                <TableCell style={{ padding: '4px' }}>
                                    {stats.reduce((sum, stat) => sum + stat.totalSale, 0) + '$'}
                                </TableCell>
                                <TableCell style={{ padding: '4px' }}>
                                    {stats.reduce((sum, stat) => sum + stat.manCount, 0)}
                                </TableCell>
                                <TableCell style={{ padding: '4px' }}>
                                    {stats.reduce((sum, stat) => sum + stat.manSale, 0) + '$'}
                                </TableCell>
                                <TableCell style={{ padding: '4px' }}>
                                    {stats.reduce((sum, stat) => sum + stat.womanCount,  0)}
                                </TableCell>
                                <TableCell style={{ padding: '4px' }}>
                                    {stats.reduce((sum, stat) => sum + stat.womanSale, 0) + '$'}
                                </TableCell>
                            </TableRow>
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
            }
            }backUrl={"BACK"}/>
        </div>
    );
};

export default Statistics;