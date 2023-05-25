import { z } from "zod";
import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";

interface WordcloudData {
  [videoId: string]: string[];
}

export const wordcloudDataRouter = createTRPCRouter({
  getWordcloudData: publicProcedure
    .input(
      z.object({
        videoIds: z.array(z.string()),
      })
    )
    .query(({ input }) => {
      const wordcloudData: WordcloudData = {
        r0cM20WPyqI: [
          "genuis",
          "amazing",
          "great",
          "awesome",
          "cool",
          "fantastic",
          "wonderful",
          "nice",
          "good",
          "excellent",
        ],
        lRuzXBN79Fw: [
          "bravo",
          "brilliant",
          "superb",
          "outstanding",
          "impressive",
          "exceptional",
          "marvelous",
          "splendid",
          "terrific",
          "phenomenal",
        ],
        zmADLllSxOE: [
          "word21",
          "word22",
          "word23",
          "word24",
          "word25",
          "word26",
          "word27",
          "word28",
          "word29",
          "word30",
        ],
      };

      // filter the data based on the input videoIds
      const filteredData: Partial<WordcloudData> = Object.keys(wordcloudData)
        .filter((key) => input.videoIds.includes(key))
        .reduce((obj: Partial<WordcloudData>, key) => {
          if (wordcloudData[key]) {
            obj[key] = wordcloudData[key];
          }
          return obj;
        }, {});
      return filteredData;
    }),
});
