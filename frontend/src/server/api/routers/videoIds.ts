// server/api/routers/videoIds.ts

import { z } from "zod";
import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";

export const videoIdsRouter = createTRPCRouter({
  checkVideoId: publicProcedure
    .input(z.object({ videoId: z.string() }))
    .query(async ({ input }) => {
      // return if input.videoId is empty string
      if (input.videoId === "") {
        return { isValid: false, error: "Invalid video ID" };
      }

      console.log("checkVideoId called with", input.videoId);
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
    .input(
      z.object({
        companyName: z.string(),
        email: z.string(),
        videoId: z.string(),
        tokens: z.string(),
        platform: z.string(),
      })
    )
    .query(async ({ input }) => {
      // return if input.videoId is empty string
      if (input.videoId === "") {
        return { isValid: false, error: "Invalid video ID" };
      }

      console.log("fetchVideoId called with", input);
      const response = await fetch(
        `${process.env.BASE_URL_CHECKOUT}/api/cart/order`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            companyName: input.companyName,
            email: input.email,
            videoId: input.videoId,
            tokens: input.tokens,
            platform: input.platform,
          }),
        }
      );
      console.log("fetchVideoId response", response);
      console.log("\n");

      if (response.ok) {
        const data = await response.json();
        return { isValid: true, traceId: data.traceId };
      } else {
        const error = await response.json();
        return { isValid: false, error: error.error };
      }
    }),
});
