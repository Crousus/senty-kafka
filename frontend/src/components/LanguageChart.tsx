// src/components/LanguageChart.tsx

import React, { useContext, useEffect, useState } from "react";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
import { api } from "~/utils/api";

const LanguageChart = () => {
  const { checkedVideos } = useContext(CheckedVideosContext);

  const [languageData, setLanguageData] = useState({});

  const [languageChartData, setLanguageChartData] = useState([]);

  const languageDataResponse = api.languageData.getLanguageData.useQuery({
    videoIds: checkedVideos,
  });

  useEffect(() => {
    const interval = setInterval(() => {
      languageDataResponse.refetch().catch((err) => {
        console.log("languageDataResponse err", err);
      });
    }, 5000);

    return () => clearInterval(interval); // clear interval on component unmount
  }, []);

  useEffect(() => {
    if (languageDataResponse.data) {
      setLanguageData(languageDataResponse.data);
    }
  }, [languageDataResponse.data]);

  useEffect(() => {
    let newLanguageChartData = [];
    Object.keys(languageData).forEach((videoId) => {
      Object.keys(languageData[videoId]).forEach((languageCode) => {
        const count = languageData[videoId][languageCode];
        newLanguageChartData.push({ language: languageCode, count });
      });
    });
    setLanguageChartData(newLanguageChartData);
  }, [checkedVideos, languageData]);

  // Process the data to create the required chart configuration
  const languageDataDisplay = languageChartData.reduce((acc, item) => {
    const existingIndex = acc.findIndex((el) => el.language === item.language);
    if (existingIndex !== -1) {
      acc[existingIndex].count += item.count;
    } else {
      acc.push({ language: item.language, count: item.count });
    }
    return acc;
  }, []);

  const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#a4de6c", "#d0ed57"];

  if (languageDataDisplay.length === 0) {
    return <div>No data</div>;
  }

  return (
    <ResponsiveContainer width="100%" height={300}>
      <PieChart width={400} height={300}>
        <Legend />
        <Pie
          data={languageDataDisplay}
          dataKey="count"
          nameKey="language"
          cx="50%"
          cy="50%"
          outerRadius={80}
          fill="#8884d8"
          label
        >
          {languageDataDisplay.map((entry, index) => (
            <Cell key={index} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip
          contentStyle={{ backgroundColor: "#000" }}
          itemStyle={{ color: "#fff" }}
        />
      </PieChart>
    </ResponsiveContainer>
  );
};

export default LanguageChart;
