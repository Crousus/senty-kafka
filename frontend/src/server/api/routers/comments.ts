// src/server/api/routers/comments.ts

import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";
import { z } from "zod";

interface CommentsResponse {
  [videoId: string]: number;
}

interface SubComment {
  video_id: string;
  comment_id: string;
  timestamp: string;
  comment: string;
  sentiment_score: number;
  language: string;
  sub_comments: null;
}

interface Last5 {
  [videoId: string]: SubComment[];
}

export const commentsRouter = createTRPCRouter({
  getCommentsCount: publicProcedure
    .input(z.object({ videoIds: z.array(z.string()) }))
    .query(async ({ input }) => {
      // TODO: return if input.videoIds is empty array

      console.log("getCommentsCount called with", input);
      const response = await fetch(
        `${process.env.BASE_URL_STREAM}/comments/count`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ videoIds: input.videoIds }),
        }
      );

      const data = (await response.json()) as CommentsResponse;
      console.log("getCommentsCount response", data);
      console.log("\n");
      return data;
    }),

  getSentimentAvg: publicProcedure
    .input(z.object({ videoIds: z.array(z.string()) }))
    .query(async ({ input }) => {
      console.log("getSentimentAvg called with", input);
      const response = await fetch(
        `${process.env.BASE_URL_STREAM}/sentiment/total`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ videoIds: input.videoIds }),
        }
      );

      const data = (await response.json()) as CommentsResponse;
      console.log("getSentimentAvg response", data);
      console.log("\n");
      return data;
    }),
  getLatestComments: publicProcedure
    .input(z.object({ videoIds: z.array(z.string()) }))
    .query(async ({ input }) => {
      const response = await fetch(
        `${process.env.BASE_URL_STREAM}/comments/last5`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ videoIds: input.videoIds }),
        }
      );

      const data = (await response.json()) as Last5;
      return data;
    }),
});
