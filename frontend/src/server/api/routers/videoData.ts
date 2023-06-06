// src/server/api/routers/videoData.ts

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
    .query(async ({ input }) => {
      let videoData: VideoData[];

      if (process.env.DEBUG === "true") {
        // Hardcoded data
        videoData = [
          {
            sentiment: [
              {
                date: "2023-01-10T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-02-09T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-02-10T00:00:00Z",
                sentiment: 0.0,
                comments: 14,
              },
              {
                date: "2023-02-11T00:00:00Z",
                sentiment: 0.0,
                comments: 6,
              },
              {
                date: "2023-02-12T00:00:00Z",
                sentiment: 0.0,
                comments: 3,
              },
              {
                date: "2023-02-13T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-02-14T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-02-15T00:00:00Z",
                sentiment: 0.0,
                comments: 2,
              },
              {
                date: "2023-02-17T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-02-18T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-02-20T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-02-21T00:00:00Z",
                sentiment: 0.0,
                comments: 2,
              },
              {
                date: "2023-02-23T00:00:00Z",
                sentiment: 0.0,
                comments: 3,
              },
              {
                date: "2023-02-24T00:00:00Z",
                sentiment: 0.0,
                comments: 2,
              },
              {
                date: "2023-02-27T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-02-28T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-03-07T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-03-10T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-03-14T00:00:00Z",
                sentiment: 0.0,
                comments: 2,
              },
              {
                date: "2023-03-22T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-03-24T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-03-27T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-03-30T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-04-04T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-04-09T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-04-25T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-05-01T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-05-02T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-05-13T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-05-17T00:00:00Z",
                sentiment: 0.0,
                comments: 2,
              },
              {
                date: "2023-05-18T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
            ],
            videoId: "u36A-YTxiOw",
          },
          {
            sentiment: [
              {
                date: "2023-05-18T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-05-30T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
              {
                date: "2023-06-02T00:00:00Z",
                sentiment: 0.0,
                comments: 1,
              },
            ],
            videoId: "Th8JoIan4dg",
          },
        ];
      } else {
        // Fetch data from the provided URL
        console.log("getVideoData called with", input);
        const response = await fetch(
          `${process.env.BASE_URL_STREAM}/sentiment/total/24h`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ videoIds: input.videoIds }),
          }
        );
        videoData = await response.json();
      }

      // filter the data based on the input videoIds
      const filteredData = videoData.filter((data) =>
        input.videoIds.includes(data.videoId)
      );

      console.log("getVideoData response", filteredData);
      console.log("\n");

      return filteredData;
    }),
});
