// server/api/routers/videoIds.ts

import { z } from "zod";
import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";

export const videoIdsRouter = createTRPCRouter({
  checkVideoId: publicProcedure
    .input(z.object({ videoId: z.string() }))
    .query(async ({ input }) => {
      const response = await fetch(
        `http://10.0.72.186:7001/api/scraperyoutube/verify?url=https://www.youtube.com/watch?v=${input.videoId}`
      );
      if (response.ok) {
        return { isValid: true };
      } else {
        return { isValid: false, error: "Invalid video ID" };
      }
    }),
  fetchVideoId: publicProcedure
    .input(z.object({ videoId: z.string() }))
    .query(async ({ input }) => {
      console.log("input", input);
      const response = await fetch(
        `http://10.0.72.186:7001/api/scraperyoutube/fetch?videoId=${input.videoId}`
      );
      if (response.ok) {
        return { isValid: true };
      } else {
        return { isValid: false, error: "Invalid video ID" };
      }
    }),
});
