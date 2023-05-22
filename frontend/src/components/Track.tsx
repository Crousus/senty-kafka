const Track = () => {
  const videos = [
    {
      thumbnail: "Thumbnail 1",
      title: "Title 1",
      channel: "Channel 1",
      date: "Date 1",
      sumViews: "Sum Views 1",
      sumComments: "Sum Comments 1",
      avgSentiment: "Avg Sentiment 1",
      checked: false,
    },
    {
      thumbnail: "Thumbnail 2",
      title: "Title 2",
      channel: "Channel 2",
      date: "Date 2",
      sumViews: "Sum Views 2",
      sumComments: "Sum Comments 2",
      avgSentiment: "Avg Sentiment 2",
      checked: false,
    },
    // Add more videos here...
  ];

  return (
    <div>
      {videos.map((video, index) => (
        <div key={index} className="mb-4 rounded-md border p-4">
          <div className="mb-4 grid grid-cols-9 gap-4">
            <div className="col-span-2 rounded-md border p-2">
              {video.thumbnail}
            </div>
            <div className="col-span-3 border-x">
              <p>{video.title}</p>
              <p>{video.channel}</p>
              <p>{video.date}</p>
            </div>
            <div className="col-span-3 border-x">
              <p>{video.sumViews}</p>
              <p>{video.sumComments}</p>
              <p>{video.avgSentiment}</p>
            </div>
            <div className="col-span-1 rounded-md border p-2">
              {video.checked ? "Checked" : "Unchecked"}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default Track;
