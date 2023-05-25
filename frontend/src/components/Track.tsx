// src/components/Track.tsx

import React, { useContext, useEffect, useState } from "react";
import Image from "next/image";
import { api } from "~/utils/api";
import { OrderedVideosContext } from "~/contexts/orderedVideosContext";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";

const sentimentEmojis = ["🤬", "😢", "🙁", "😐", "🙂", "😄"];

const Track = () => {
  const { orderedVideos, setOrderedVideos } = useContext(OrderedVideosContext);
  const { checkedVideos, setCheckedVideos } = useContext(CheckedVideosContext);

  console.log("orderedVideos", orderedVideos);
  console.log("checkedVideos", checkedVideos);
  console.log("\n");

  const [videos, setVideos] = useState([]);
  const [commentsData, setCommentsData] = useState({});
  const [sentimentData, setSentimentData] = useState({});

  const videosResponse = api.videos.getVideos.useQuery({
    videoIds: orderedVideos,
  });

  const commentsDataResponse = api.comments.getCommentsCount.useQuery({
    videoIds: orderedVideos,
  });

  const sentimentDataResponse = api.comments.getSentimentAvg.useQuery({
    videoIds: orderedVideos,
  });

  useEffect(() => {
    const interval = setInterval(() => {
      videosResponse.refetch().catch((err) => {
        console.log("latestCommentsResponse err", err);
      });
      commentsDataResponse.refetch().catch((err) => {
        console.log("latestCommentsResponse err", err);
      });
      sentimentDataResponse.refetch().catch((err) => {
        console.log("latestCommentsResponse err", err);
      });
    }, 5000);

    return () => clearInterval(interval); // clear interval on component unmount
  }, []);

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
  }, [
    videosResponse.data,
    commentsDataResponse.data,
    sentimentDataResponse.data,
  ]);

  const handleCheckboxChange = (videoId: string) => {
    setCheckedVideos((prevVideos: string[]) =>
      prevVideos.includes(videoId)
        ? prevVideos.filter((id) => id !== videoId)
        : [...prevVideos, videoId]
    );
  };

  const handleDeleteVideo = (videoId: string) => {
    setOrderedVideos((prevVideoIds) =>
      prevVideoIds.filter((id) => id !== videoId)
    );
  };

  console.log("videos", videos);

  if (videos.length === 0) {
    return (
      <div>
        <p className="text-center text-slate-400">No data to display</p>
      </div>
    );
  }

  return (
    <div>
      {videos.map((video, index) => (
        <div
          key={index}
          className={`mb-4 cursor-pointer rounded-md border p-4 ${
            checkedVideos.includes(video.videoId)
              ? "border-emerald-700 bg-[#022c22] hover:bg-[#022c22]"
              : "border-slate-700 bg-slate-900 hover:bg-slate-800"
          }`}
          onClick={() => handleCheckboxChange(video.videoId)}
        >
          <div className="mb-4 grid grid-cols-12 gap-4">
            <Image
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
                {commentsData[video.videoId].toLocaleString() || "-"}
              </p>
              <p className="mb-4 text-sm text-slate-400">comments</p>
              <p className="text-3xl font-bold">
                {sentimentEmojis[
                  Math.round(sentimentData[video.videoId] * 5)
                ] || "-"}
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
