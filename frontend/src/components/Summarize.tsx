import { useContext } from "react";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
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

const Summarize = () => {
  const { checkedVideos } = useContext(CheckedVideosContext);

  // Mock data
  const videoData = [
    {
      videoId: "r0cM20WPyqI",
      sentiment: [
        { date: "2023-05-01", sentiment: 7, comments: 50 },
        { date: "2023-05-02", sentiment: 8, comments: 70 },
        { date: "2023-05-03", sentiment: 6, comments: 30 },
      ],
    },
    {
      videoId: "lRuzXBN79Fw",
      sentiment: [
        { date: "2023-05-01", sentiment: 5, comments: 60 },
        { date: "2023-05-02", sentiment: 7, comments: 80 },
        { date: "2023-05-03", sentiment: 6, comments: 50 },
      ],
    },
    {
      videoId: "zmADLllSxOE",
      sentiment: [
        { date: "2023-05-01", sentiment: 9, comments: 50 },
        { date: "2023-05-02", sentiment: 8, comments: 20 },
        { date: "2023-05-03", sentiment: 9, comments: 10 },
      ],
    },
  ];

  // Filter video data for checked videos
  const checkedData = videoData.filter((video) =>
    checkedVideos.includes(video.videoId)
  );

  // Return early if no data to display
  if (checkedData.length === 0) {
    return (
      <div>
        <div className="grid grid-cols-1 gap-4">
          <div className="h-48 rounded-md border px-2">
            Checked Videos: {checkedVideos.join(", ")}
          </div>
          <div className="h-48 rounded-md border px-2">No data to display</div>
          <div className="h-48 rounded-md border px-2">Wordcloud</div>
        </div>
      </div>
    );
  }

  // Consolidate data for chart
  const chartData = [];
  for (let i = 0; i < checkedData[0].sentiment.length; i++) {
    const dataPoint = { date: checkedData[0].sentiment[i].date };
    checkedData.forEach((video) => {
      dataPoint[`sentiment_${video.videoId}`] = video.sentiment[i].sentiment;
      dataPoint[`comments_${video.videoId}`] = video.sentiment[i].comments;
    });
    chartData.push(dataPoint);
  }

  return (
    <div>
      <div className="grid grid-cols-1 gap-4">
        <div className="h-48 rounded-md border border-slate-700 px-2">
          Checked Videos: {checkedVideos.join(", ")}
        </div>
        <div className="rounded-md border border-slate-700 px-2">
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={chartData}>
              <CartesianGrid stroke="#ccc" />
              <XAxis dataKey="date" stroke="white" />
              <YAxis yAxisId="left" orientation="left" stroke="white" />
              <YAxis yAxisId="right" orientation="right" stroke="white" />
              <Tooltip />
              <Legend />
              {checkedData.map((video) => (
                <>
                  <Line
                    yAxisId="left"
                    type="monotone"
                    dataKey={`sentiment_${video.videoId}`}
                    stroke="#8884d8"
                    name={`${video.videoId} sentiment`}
                  />
                  <Line
                    yAxisId="right"
                    type="monotone"
                    dataKey={`comments_${video.videoId}`}
                    stroke="#82ca9d"
                    name={`${video.videoId} comments`}
                  />
                </>
              ))}
            </LineChart>
          </ResponsiveContainer>
        </div>
        <div className="h-48 rounded-md border border-slate-700 px-2">
          Wordcloud
        </div>
      </div>
    </div>
  );
};

export default Summarize;
