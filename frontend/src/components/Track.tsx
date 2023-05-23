import { useContext } from "react";
import Image from "next/image";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";

const Track = () => {
  const videos = [
    {
      videoId: "r0cM20WPyqI",
      publishedAt: "2023-05-10T10:00:57Z",
      dislikeCount: "",
      likeCount: 3165,
      defaultThumbnailUrl: "https://i.ytimg.com/vi/r0cM20WPyqI/sddefault.jpg",
      viewCount: 82001,
      title: "The new Porsche 718 Spyder RS: A rebel unleashed",
      channelId: "UC_BaxRhNREI_V0DVXjXDALA",
      channelTitle: "Porsche",
      favoriteCount: 0,
      commentCount: 162,
    },
    {
      videoId: "lRuzXBN79Fw",
      publishedAt: "2023-02-14T16:31:35Z",
      dislikeCount: "",
      likeCount: 994,
      defaultThumbnailUrl: "https://i.ytimg.com/vi/lRuzXBN79Fw/sddefault.jpg",
      viewCount: 26236,
      title:
        "Porsche and L’Art De L’Automobile took the 968 L’Art for a road trip in California",
      channelId: "UC_BaxRhNREI_V0DVXjXDALA",
      channelTitle: "Porsche",
      favoriteCount: 0,
      commentCount: 69,
    },
    {
      videoId: "zmADLllSxOE",
      publishedAt: "2022-11-17T03:01:45Z",
      dislikeCount: "",
      likeCount: 28614,
      defaultThumbnailUrl: "https://i.ytimg.com/vi/zmADLllSxOE/sddefault.jpg",
      viewCount: 7968701,
      title: "The new 911 Dakar",
      channelId: "UC_BaxRhNREI_V0DVXjXDALA",
      channelTitle: "Porsche",
      favoriteCount: 0,
      commentCount: 1114,
    },
  ];

  const { checkedVideos, setCheckedVideos } = useContext(CheckedVideosContext);

  const handleCheckboxChange = (videoId: string) => {
    setCheckedVideos((prevVideos: string[]) =>
      prevVideos.includes(videoId)
        ? prevVideos.filter((id) => id !== videoId)
        : [...prevVideos, videoId]
    );
  };

  return (
    <div>
      {videos.map((video, index) => (
        <div
          key={index}
          className={`mb-4 rounded-md border p-4 ${
            checkedVideos.includes(video.videoId)
              ? "border-emerald-700 bg-[#022c22]"
              : "border-slate-700"
          }`}
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
              <p className="font-bold">{video.title}</p>
              <p className="text-slate-400">{video.channelTitle}</p>
              {/* <p className="text-sm">{video.publishedAt.split("T")[0]}</p> */}
            </div>
            <div className="col-span-3">
              <p className="text-xl font-bold">{(1000).toLocaleString()}</p>
              <p className="mb-4 text-sm text-slate-400">comments</p>
              <p className="text-xl font-bold">{4.5}/5</p>
              <p className="text-sm text-slate-400">sentiment</p>
            </div>
            <div className="col-span-1 grid items-center justify-center rounded-md p-2 ">
              <input
                type="checkbox"
                checked={checkedVideos.includes(video.videoId)}
                onChange={() => handleCheckboxChange(video.videoId)}
              />
              {/* delete button */}
              <button
                className="text-red-500"
                onClick={() => void console.log("delete")}
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
