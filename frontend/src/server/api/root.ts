// server/api/root.ts
import { createTRPCRouter } from "~/server/api/trpc";
import { videosRouter } from "~/server/api/routers/videos";
import { videoDataRouter } from "~/server/api/routers/videoData";
import { wordcloudDataRouter } from "~/server/api/routers/wordcloudData";
import { languageDataRouter } from "~/server/api/routers/languageData";
import { videoIdsRouter } from "./routers/videoIds";
import { commentsRouter } from "./routers/comments";

export const appRouter = createTRPCRouter({
  videos: videosRouter,
  videoData: videoDataRouter,
  wordcloudData: wordcloudDataRouter,
  languageData: languageDataRouter,
  videoIds: videoIdsRouter,
  comments: commentsRouter,
});

export type AppRouter = typeof appRouter;
