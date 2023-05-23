// CustomChart.js
import {
  LineChart,
  Line,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

const CustomChart = ({ checkedData, chartData, colors, lineTypes }) => (
  <ResponsiveContainer width="100%" height={300}>
    <LineChart data={chartData}>
      <CartesianGrid stroke="#ccc" />
      <XAxis dataKey="date" stroke="white" />
      <YAxis yAxisId="left" orientation="left" stroke="white" />
      <YAxis yAxisId="right" orientation="right" stroke="white" />
      <Tooltip
        contentStyle={{ backgroundColor: "#000" }}
        itemStyle={{ color: "#fff" }}
      />
      <Legend />
      {checkedData.map((video, index) => (
        <>
          <Line
            yAxisId="left"
            type={lineTypes[index % lineTypes.length]}
            dataKey={`sentiment_${video.videoId}`}
            stroke={colors[index % colors.length]}
            name={`${video.videoId} sentiment`}
          />
          <Line
            yAxisId="right"
            type={lineTypes[index % lineTypes.length]}
            dataKey={`comments_${video.videoId}`}
            stroke={colors[index % colors.length]}
            name={`${video.videoId} comments`}
            dot={false} // you can add this to have the second line type dotted
            strokeDasharray="5 5" // dash line
          />
        </>
      ))}
    </LineChart>
  </ResponsiveContainer>
);

export default CustomChart;
