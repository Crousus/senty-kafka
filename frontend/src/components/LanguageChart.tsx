import React from "react";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

const LanguageChart = ({ data }) => {
  // Process the data to create the required chart configuration
  const languageData = data.reduce((acc, item) => {
    const existingIndex = acc.findIndex((el) => el.language === item.language);
    if (existingIndex !== -1) {
      acc[existingIndex].comments += item.comments;
    } else {
      acc.push({ language: item.language, comments: item.comments });
    }
    return acc;
  }, []);

  const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#a4de6c", "#d0ed57"];

  return (
    <ResponsiveContainer width="100%" height={300}>
      <PieChart width={400} height={300}>
        <Legend />
        <Pie
          data={languageData}
          dataKey="comments"
          nameKey="language"
          cx="50%"
          cy="50%"
          outerRadius={80}
          fill="#8884d8"
          label
        >
          {languageData.map((entry, index) => (
            <Cell key={index} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip />
      </PieChart>
    </ResponsiveContainer>
  );
};

export default LanguageChart;
