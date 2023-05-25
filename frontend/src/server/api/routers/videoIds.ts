// server/api/routers/videoIds.ts

import { z } from "zod";
import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";

export const videoIdsRouter = createTRPCRouter({
  checkVideoId: publicProcedure
    .input(z.object({ videoId: z.string() }))
    .query(async ({ input }) => {
      console.log("checkVideoId called with", input);
      const response = await fetch(
        `http://130.82.245.25:7001/api/scraperyoutube/verify?url=https://www.youtube.com/watch?v=${input.videoId}`
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
        `http://130.82.245.25:7001/api/scraperyoutube/fetch?videoId=${input.videoId}`
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
