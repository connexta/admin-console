import React from 'react'
import {connect} from 'react-redux'
import {BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';

const data = [
    {name: 'Bundle A', female: 2400, male: 2400},
    {name: 'Bundle B', female: 1398, male: 2210},
    {name: 'Bundle C', female: 9800, male: 2290},
    {name: 'Bundle D', female: 3908, male: 2000},
    {name: 'Bundle E', female: 4800, male: 2181},
    {name: 'Bundle F', female: 3800, male: 2500},
    {name: 'Bundle G', female: 4300, male: 2100},
];

const BundleTimeline = ({state, actions}) => (
    <BarChart width={1200} height={1200} data={data} layout='vertical'
              margin={{top: 20, right: 30, left: 20, bottom: 5}}>
        <CartesianGrid strokeDasharray="3 3"/>
        <XAxis type="number" />
        <YAxis dataKey="name" type="category"/>
        <Tooltip/>
        <Legend />
        <Bar dataKey="female" stackId="a" fill="#8884d8" />
        <Bar dataKey="male" stackId="a" fill="#82ca9d" />
    </BarChart>
)

export default connect(undefined, undefined)(BundleTimeline)