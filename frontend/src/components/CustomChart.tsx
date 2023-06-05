// src/components/CustomChart.tsx

import { useContext, useEffect, useState } from "react";
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
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
import { api } from "~/utils/api";

type VideoDataType = {
  videoId: string;
  sentiment: { date: string; sentiment: number; comments: number }[];
}[];

const colors = [
  "#8884d8",
  "#82ca9d",
  "#ffc658",
  "#a4de6c",
  "#d0ed57",
  "#ffc658",
  "#8884d8",
];
const lineTypes = [
  "monotone",
  "linear",
  "step",
  "stepBefore",
  "stepAfter",
  "basis",
  "basisOpen",
  "basisClosed",
  "natural",
];

const createChartData = (checkedData: VideoDataType) => {
  const allDates = new Set();
  checkedData.forEach((video) => {
    video.sentiment.forEach(({ date }) => {
      allDates.add(date);
    });
  });

  const data: Record<string, any>[] = Array.from(allDates).map((date) => ({
    date,
  }));

  checkedData.forEach((video, i) => {
    video.sentiment.forEach(({ date, sentiment, comments }) => {
      const existingPoint = data.find((point) => point.date === date);
      if (existingPoint) {
        existingPoint[`sentiment_${video.videoId}`] = sentiment;
        existingPoint[`comments_${video.videoId}`] = comments;
      }
    });
  });

  return data;
};

const CustomChart = () => {
  const { checkedVideos } = useContext(CheckedVideosContext);

  const [videoData, setVideoData] = useState([]);

  const [chartData, setChartData] = useState([]);

  const videoDataResponse = api.videoData.getVideoData.useQuery({
    videoIds: checkedVideos,
  });

  useEffect(() => {
    if (videoDataResponse.data) {
      setVideoData(videoDataResponse.data);
    }
  }, [videoDataResponse.data]);

  // Filter video data for checked videos
  const checkedData = videoData.filter((video) =>
    checkedVideos.includes(video.videoId)
  );

  useEffect(() => {
    // Filter video data for checked videos
    const checkedData = videoData.filter((video) =>
      checkedVideos.includes(video.videoId)
    );
    setChartData(createChartData(checkedData));
  }, [videoData, checkedVideos]);

  useEffect(() => {
    // Filter video data for checked videos
    const checkedData = videoData.filter((video) =>
      checkedVideos.includes(video.videoId)
    );
    setChartData(createChartData(checkedData));
  }, [videoData, checkedVideos]);

  if (checkedData.length === 0) {
    return <div>No data</div>;
  }

  return (
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
};

export default CustomChart;
