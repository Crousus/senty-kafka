import { useContext, useEffect, useState } from "react";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
import { RefetchContext } from "~/contexts/refetchContext";
import { api } from "~/utils/api";

const sentimentEmojis = ["🤬", "😢", "🙁", "😐", "🙂", "😄"];

const LastestComments = () => {
  const { checkedVideos } = useContext(CheckedVideosContext);
  const { isRefetchActive } = useContext(RefetchContext);

  const [latestCommentsData, setLatestCommentsData] = useState({});

  const latestCommentsResponse = api.comments.getLatestComments.useQuery({
    videoIds: checkedVideos,
  });

  useEffect(() => {
    if (isRefetchActive) {
      const interval = setInterval(() => {
        latestCommentsResponse.refetch().catch((err) => {
          console.log("latestCommentsResponse err", err);
        });
      }, 5000);

      return () => clearInterval(interval); // clear interval on component unmount
    }
  }, [isRefetchActive]);

  useEffect(() => {
    if (latestCommentsResponse.data) {
      setLatestCommentsData(latestCommentsResponse.data);
    }
  }, [latestCommentsResponse.data]);

  if (Object.keys(latestCommentsData).length === 0) {
    return <div>No data</div>;
  }

  // Map over the latestCommentsData object and display the data in a table for each videoId
  return (
    <div>
      {Object.entries(latestCommentsData).map(([videoId, comments]) => (
        <div key={videoId} className="mb-3 border-b-4">
          <h2 className="text-xl">Video ID: {videoId}</h2>
          <table>
            <thead>
              <tr>
                <th>Comment</th>
                <th>Date</th>
                <th>Sentiment</th>
              </tr>
            </thead>
            <tbody>
              {comments.reverse().map((comment) => (
                <tr key={comment.comment_id} className="border-y">
                  <td>{comment.comment}</td>
                  {/* human readable data from timestamp */}
                  <td>{new Date(comment.timestamp).toLocaleString()}</td>
                  <td className="text-center text-2xl">
                    {sentimentEmojis[Math.round(comment.sentiment_score * 5)] ||
                      "-"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}
    </div>
  );
};

export default LastestComments;
