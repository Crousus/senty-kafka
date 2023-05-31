// server/api/routers/videoIds.ts

import { z } from "zod";
import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";

export const videoIdsRouter = createTRPCRouter({
  checkVideoId: publicProcedure
    .input(z.object({ videoId: z.string() }))
    .query(async ({ input }) => {
      console.log("checkVideoId called with", input);
      const response = await fetch(
        `${process.env.BASE_URL_SCRAPER}/api/scraperyoutube/verify?url=https://www.youtube.com/watch?v=${input.videoId}`
      );
      console.log("checkVideoId response", response);
      console.log("\n");
      if (response.ok) {
        return { isValid: true };
      } else {
        return { isValid: false, error: "Invalid video ID" };
      }
    }),
  fetchVideoId: publicProcedure
    .input(z.object({ videoId: z.string() }))
    .query(async ({ input }) => {
      console.log("fetchVideoId called with", input);
      const response = await fetch(
        `${process.env.BASE_URL_SCRAPER}/api/scraperyoutube/fetch?videoId=${input.videoId}`
      );
      console.log("fetchVideoId response", response);
      console.log("\n");
      if (response.ok) {
        return { isValid: true };
      } else {
        return { isValid: false, error: "Invalid video ID" };
      }
    }),
});
