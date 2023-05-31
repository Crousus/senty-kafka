import { z } from "zod";
import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";

interface LanguageData {
  [videoId: string]: {
    [languageCode: string]: number;
  };
}

export const languageDataRouter = createTRPCRouter({
  getLanguageData: publicProcedure
    .input(
      z.object({
        videoIds: z.array(z.string()),
      })
    )
    .query(async ({ input }) => {
      let languageData: LanguageData;

      if (process.env.DEBUG === "true") {
        // Hardcoded data
        languageData = {
          zmADLllSxOE: {
            de: 9,
            ru: 2,
            pt: 1,
            rw: 2,
            kab: 1,
            br: 3,
            dv: 4,
            fy: 1,
            uk: 1,
            ia: 3,
            id: 1,
            cnh: 2,
            ca: 2,
            mt: 2,
            en: 109,
            eo: 1,
            it: 4,
            es: 3,
            zh: 1,
            cs: 1,
            ar: 2,
            ja: 1,
            rm: 1,
            pl: 1,
            tr: 1,
          },
          r0cM20WPyqI: {
            de: 6,
            hi: 1,
            pt: 3,
            rw: 1,
            mt: 6,
            en: 120,
            it: 3,
            zh: 3,
            es: 3,
            br: 1,
            dv: 5,
            ja: 1,
            id: 1,
            pl: 1,
            ur: 1,
            tr: 1,
          },
          y7p3YC4CAGI: {
            de: 1,
            pt: 2,
            mt: 1,
            en: 74,
            eo: 2,
            it: 3,
            ta: 1,
            zh: 2,
            br: 1,
            dv: 7,
            cy: 2,
            ia: 2,
            id: 3,
          },
        };
      } else {
        // Fetch data from the provided URL
        console.log("getLanguageData called with", input);
        const response = await fetch(
          `${process.env.BASE_URL_STREAM}/comments/count/lang`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ videoIds: input.videoIds }),
          }
        );
        languageData = await response.json();
      }

      // Filter the data based on the input videoIds
      const filteredData: Partial<LanguageData> = Object.keys(languageData)
        .filter((key) => input.videoIds.includes(key))
        .reduce((obj: Partial<LanguageData>, key) => {
          obj[key] = languageData[key];
          return obj;
        }, {});

      console.log("getLanguageData response", filteredData);
      console.log("\n");

      return filteredData;
    }),
});
