import { useContext, useEffect, useState } from "react";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
import CustomChart from "./CustomChart";
import Wordcloud from "./Wordcloud";
import LanguageChart from "./LanguageChart";

// List of colors and line types for differentiating lines in the chart
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

const Summarize = () => {
  const { checkedVideos } = useContext(CheckedVideosContext);
  const [checkedWordcloudData, setCheckedWordcloudData] = useState([]);
  const [languageChartData, setLanguageChartData] = useState([]);

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

  // Mock data
  const wordcloudData = {
    r0cM20WPyqI: [
      "word1",
      "word2",
      "word3",
      "word4",
      "word5",
      "word6",
      "word7",
      "word8",
      "word9",
      "word10",
    ],
    lRuzXBN79Fw: [
      "word11",
      "word12",
      "word13",
      "word14",
      "word15",
      "word16",
      "word17",
      "word18",
      "word19",
      "word20",
    ],
    zmADLllSxOE: [
      "word21",
      "word22",
      "word23",
      "word24",
      "word25",
      "word26",
      "word27",
      "word28",
      "word29",
      "word30",
    ],
  };

  const languageData = {
    r0cM20WPyqI: [
      { language: "English", comments: 25 },
      { language: "Spanish", comments: 15 },
      { language: "French", comments: 10 },
    ],
    lRuzXBN79Fw: [
      { language: "English", comments: 40 },
      { language: "Spanish", comments: 20 },
      { language: "German", comments: 10 },
    ],
    zmADLllSxOE: [
      { language: "English", comments: 15 },
      { language: "French", comments: 5 },
      { language: "Italian", comments: 10 },
    ],
  };

  useEffect(() => {
    let newCheckedWordcloudData = [...checkedWordcloudData];
    checkedVideos.forEach((videoId) => {
      if (wordcloudData[videoId]) {
        // Add words if they're not already in the state
        wordcloudData[videoId].forEach((word) => {
          if (!newCheckedWordcloudData.includes(word)) {
            newCheckedWordcloudData.push(word);
          }
        });
      }
    });
    // Remove words that are not in any of the checked videos
    newCheckedWordcloudData = newCheckedWordcloudData.filter((word) =>
      checkedVideos.some((videoId) => wordcloudData[videoId]?.includes(word))
    );
    console.log("");
    console.log("checkedVideos", checkedVideos);
    console.log("checkedWordcloudData", checkedWordcloudData);
    console.log("wordcloudData", wordcloudData);
    console.log("newCheckedWordcloudData", newCheckedWordcloudData);
    setCheckedWordcloudData(newCheckedWordcloudData);
  }, [checkedVideos]);

  useEffect(() => {
    const newLanguageChartData = [];
    checkedVideos.forEach((videoId) => {
      if (languageData[videoId]) {
        newLanguageChartData.push(...languageData[videoId]);
      }
    });
    setLanguageChartData(newLanguageChartData);
  }, [checkedVideos]);

  // Filter video data for checked videos
  const checkedData = videoData.filter((video) =>
    checkedVideos.includes(video.videoId)
  );

  // Return early if no data to display
  if (checkedData.length === 0) {
    return (
      <div>
        <p className="text-center text-slate-400">No data to display</p>
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
        <div className="rounded-md border border-slate-700 p-4">
          <CustomChart
            checkedData={checkedData}
            chartData={chartData}
            colors={colors}
            lineTypes={lineTypes}
          />
        </div>
        {checkedWordcloudData.length > 0 && (
          <div className="rounded-md border border-slate-700 p-4">
            {
              void console.log(
                "RETURN checkedWordcloudData",
                checkedWordcloudData
              )
            }
            <Wordcloud data={checkedWordcloudData} />
          </div>
        )}
        {languageChartData.length > 0 && (
          <div className="rounded-md border border-slate-700 p-4">
            <LanguageChart data={languageChartData} />
          </div>
        )}
      </div>
    </div>
  );
};

export default Summarize;
