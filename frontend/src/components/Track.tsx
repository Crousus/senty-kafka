// src/components/Track.tsx

import React, { useContext, useEffect, useState } from "react";
import { api } from "~/utils/api";
import { OrderedVideosContext } from "~/contexts/orderedVideosContext";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
import { RefetchContext } from "~/contexts/refetchContext";

const sentimentEmojis = ["🤬", "😢", "🙁", "😐", "🙂", "😄"];

const Track = () => {
  const { orderedVideos, setOrderedVideos } = useContext(OrderedVideosContext);
  const { checkedVideos, setCheckedVideos } = useContext(CheckedVideosContext);
  const { isRefetchActive } = useContext(RefetchContext);

  console.log("orderedVideos", orderedVideos);
  console.log("checkedVideos", checkedVideos);
  console.log("\n");

  const [videos, setVideos] = useState([]);
  const [commentsData, setCommentsData] = useState({});
  const [sentimentData, setSentimentData] = useState({});
  const [statusData, setStatusData] = useState({});

  console.log("statusData", statusData);

  const videosResponse = api.videos.getVideos.useQuery({
    videoIds: orderedVideos.map((video) => video.videoId),
  });

  const commentsDataResponse = api.comments.getCommentsCount.useQuery({
    videoIds: orderedVideos.map((video) => video.videoId),
  });

  const sentimentDataResponse = api.comments.getSentimentAvg.useQuery({
    videoIds: orderedVideos.map((video) => video.videoId),
  });

  const statusDataResponse = api.orders.getStatuses.useQuery({
    traceIds: orderedVideos.map((video) => video.traceId),
  });

  useEffect(() => {
    if (isRefetchActive) {
      const interval = setInterval(async () => {
        try {
          await videosResponse.refetch();
          await commentsDataResponse.refetch();
          await sentimentDataResponse.refetch();
          await statusDataResponse.refetch();
        } catch (err) {
          console.log("Error during refetch", err);
        }
      }, 5000);

      return () => clearInterval(interval);
    }
  }, [isRefetchActive]);

  useEffect(() => {
    if (videosResponse.data) {
      setVideos(videosResponse.data);
      // console.log("videos", videosResponse.data);
    }
    if (commentsDataResponse.data) {
      setCommentsData(commentsDataResponse.data);
      // console.log("commentsData", commentsDataResponse.data);
    }
    if (sentimentDataResponse.data) {
      setSentimentData(sentimentDataResponse.data);
      // console.log("sentimentData", sentimentDataResponse.data);
    }
    if (statusDataResponse.data) {
      setStatusData(statusDataResponse.data);
      // console.log("statusData", statusDataResponse.data);
    }
  }, [
    videosResponse.data,
    commentsDataResponse.data,
    sentimentDataResponse.data,
    statusDataResponse.data,
  ]);

  const handleCheckboxChange = (videoId: string) => {
    console.log("CHECKED videoId", videoId);
    setCheckedVideos((prevVideos: string[]) =>
      prevVideos.includes(videoId)
        ? prevVideos.filter((id) => id !== videoId)
        : [...prevVideos, videoId]
    );
  };

  const handleDeleteVideo = (videoId: string) => {
    setOrderedVideos((prevVideos) =>
      prevVideos.filter((video) => video.videoId !== videoId)
    );
  };

  console.log("videos", videos);

  return (
    <div>
      {videos.map((video, index) => (
        <div
          key={index}
          className={`mb-4 cursor-pointer rounded-md border p-4 ${
            checkedVideos.some((v) => v === video.videoId)
              ? "border-emerald-700 bg-[#022c22] hover:bg-[#022c22]"
              : "border-slate-700 bg-slate-900 hover:bg-slate-800"
          }`}
          onClick={() => handleCheckboxChange(video.videoId)}
        >
          <p className="mb-1 text-slate-400">
            Trace ID:{" "}
            {orderedVideos.find((v) => v.videoId === video.videoId)?.traceId ||
              "Not found"}
          </p>
          <p className="mb-1 text-slate-400">
            <p className="mb-1 text-slate-400">
              Status:{" "}
              {Array.isArray(statusData)
                ? statusData.find(
                    (status) =>
                      typeof status.videoId === "string" &&
                      status.videoId.split("=").length > 1 &&
                      status.videoId.split("=")[1] === video.videoId
                  )?.status || "N/A"
                : "N/A"}
            </p>
          </p>
          <div className="mb-4 grid grid-cols-12 gap-4">
            <img
              src={video.defaultThumbnailUrl}
              alt={video.title}
              className="col-span-4 rounded-md"
              width={640}
              height={360}
            />

            <div className="col-span-4">
              <h3 className="font-bold">{video.title}</h3>
              <p className="text-slate-400">{video.channelTitle}</p>
            </div>
            <div className="col-span-3">
              <p className="text-3xl font-bold">
                {commentsData[video.videoId]?.toLocaleString() || "-"}
              </p>
              <p className="mb-4 text-sm text-slate-400">comments</p>
              <p className="text-3xl font-bold">
                {sentimentData[video.videoId]
                  ? sentimentEmojis[
                      Math.round(sentimentData[video.videoId] * 5)
                    ]
                  : "-"}
              </p>
              <p className="text-sm text-slate-400">sentiment</p>
            </div>
            <div className="col-span-1 grid items-center justify-center rounded-md p-2 ">
              <button
                className="text-red-500"
                onClick={() => {
                  checkedVideos.includes(video.videoId)
                    ? handleDeleteVideo(video.videoId)
                    : null;
                }}
              >
                X
              </button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default Track;
