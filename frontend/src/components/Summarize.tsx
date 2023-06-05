// src/components/Summarize.tsx

import { useContext } from "react";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
import CustomChart from "./CustomChart";
import Wordcloud from "./Wordcloud";
import LanguageChart from "./LanguageChart";
import LastestComments from "./LastestComments";

const Summarize = () => {
  return (
    <div>
      <div className="grid grid-cols-1 gap-4">
        <div className="rounded-md border border-slate-700 bg-slate-900 p-4">
          <h3 className="mb-3 font-bold">Comments and Sentiment</h3>
          <CustomChart />
        </div>
        <div className="rounded-md border border-slate-700 bg-slate-900 p-4">
          <h3 className="mb-3 font-bold">Wordcloud</h3>
          <Wordcloud />
        </div>
        <div className="rounded-md border border-slate-700 bg-slate-900 p-4">
          <h3 className="mb-3 font-bold">Language Distribution</h3>
          <LanguageChart />
        </div>
        <div className="rounded-md border border-slate-700 bg-slate-900 p-4">
          <h3 className="mb-3 font-bold">Latest Comments</h3>
          <LastestComments />
        </div>
      </div>
    </div>
  );
};

export default Summarize;
