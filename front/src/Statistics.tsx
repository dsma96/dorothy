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
    manCutCount: number;
    manCutSale: number;
    manRootCount: number;
    manRootSale: number;
    manPermCount: number;
    manPermSale: number;
    womanCutCount: number;
    womanCutSale: number;
    womanRootCount: number;
    womanRootSale: number;
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
                data: stats.map((stat) => stat.manCutSale + stat.manPermSale + stat.manRootSale),
                borderColor: 'rgba(153, 102, 255, 1)',
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                fill: true,
            },
            {
                label: 'Woman Sales',
                data: stats.map((stat) => stat.womanCutSale + stat.womanRootSale),
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
            <Paper style={{ width: '100%', overflowX: 'auto', marginTop: '20px' }}>
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
                                    <TableCell   style={{ padding:'4px' }}>{stat.manCutCount + stat.manPermCount + stat.manRootCount}</TableCell>
                                    <TableCell   style={{ padding:'4px' }}>{stat.manCutSale + stat.manPermSale + stat.manRootSale+'$'}</TableCell>
                                    <TableCell   style={{ padding:'4px' }}>{stat.womanCutCount + stat.womanRootCount}</TableCell>
                                    <TableCell   style={{ padding:'4px' }}>{stat.womanCutSale + stat.womanRootSale+'$'}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
                <Footer backUrl={"BACK"}/>
            </Paper>
        </div>
    );
};

export default Statistics;