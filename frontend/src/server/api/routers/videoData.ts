import { z } from "zod";
import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";

interface VideoData {
  videoId: string;
  sentiment: {
    date: string;
    sentiment: number;
    comments: number;
  }[];
}

export const videoDataRouter = createTRPCRouter({
  getVideoData: publicProcedure
    .input(
      z.object({
        videoIds: z.array(z.string()),
      })
    )
    .query(({ input }) => {
      const videoData: VideoData[] = [
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

      // filter the data based on the input videoIds
      const filteredData = videoData.filter((data) =>
        input.videoIds.includes(data.videoId)
      );
      return filteredData;
    }),
});
