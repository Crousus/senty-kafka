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
      const response = await fetch("http://10.0.72.186:7000/comments/count", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ videoIds: input.videoIds }),
      });

      const data = (await response.json()) as CommentsResponse;
      return data;
    }),

  getSentimentAvg: publicProcedure
    .input(z.object({ videoIds: z.array(z.string()) }))
    .query(async ({ input }) => {
      const response = await fetch("http://10.0.72.186:7000/sentiment/total", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ videoIds: input.videoIds }),
      });

      const data = (await response.json()) as CommentsResponse;
      return data;
    }),
  getLatestComments: publicProcedure
    .input(z.object({ videoIds: z.array(z.string()) }))
    .query(async ({ input }) => {
      const response = await fetch("http://10.0.72.186:7000/comments/last5", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ videoIds: input.videoIds }),
      });

      const data = (await response.json()) as Last5;
      return data;
    }),
});
