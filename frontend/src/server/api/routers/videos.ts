// server/api/routers/videos.ts
import { z } from "zod";
import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";
import axios from "axios";
import { on } from "events";

interface Video {
  videoId: string;
  publishedAt: string;
  dislikeCount: string;
  likeCount: number;
  defaultThumbnailUrl: string;
  viewCount: number;
  title: string;
  channelId: string;
  channelTitle: string;
  favoriteCount: number;
  commentCount: number;
}

export const videosRouter = createTRPCRouter({
  getVideos: publicProcedure
    .input(
      z.object({
        videoIds: z.array(z.string()),
      })
    )
    .query(async ({ input }) => {
      const videoIds = input.videoIds;

      const videos: Video[] = await Promise.all(
        videoIds.map(async (videoId) => {
          console.log("getVideos called with", videoId);
          const response = await axios.get(
            `http://130.82.245.25:7001/api/scraperyoutube/verify?url=https://www.youtube.com/watch?v=${videoId}`
          );
          const data = response.data.data;

          console.log("getVideos response", data);
          console.log("\n");

          return {
            videoId: videoId,
            publishedAt: data.publishedAt,
            dislikeCount: data.dislikeCount,
            likeCount: parseInt(data.likeCount),
            defaultThumbnailUrl: data.standardThumbnailUrl,
            viewCount: parseInt(data.viewCount),
            title: data.title,
            channelId: data.channelId,
            channelTitle: data.channelTitle,
            favoriteCount: parseInt(data.favoriteCount),
            commentCount: parseInt(data.commentCount),
          };
        })
      );

      return videos;
    }),
});
