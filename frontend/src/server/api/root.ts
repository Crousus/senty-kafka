// server/api/root.ts
import { createTRPCRouter } from "~/server/api/trpc";
import { exampleRouter } from "~/server/api/routers/example";
import { videosRouter } from "~/server/api/routers/videos";
import { videoDataRouter } from "~/server/api/routers/videoData";
import { wordcloudDataRouter } from "~/server/api/routers/wordcloudData";
import { languageDataRouter } from "~/server/api/routers/languageData";
import { videoIdsRouter } from "./routers/videoIds";
import { commentsRouter } from "./routers/comments";
import { ordersRouter } from "./routers/orders";

export const appRouter = createTRPCRouter({
  example: exampleRouter,
  videos: videosRouter,
  videoData: videoDataRouter,
  wordcloudData: wordcloudDataRouter,
  languageData: languageDataRouter,
  videoIds: videoIdsRouter,
  comments: commentsRouter,
  orders: ordersRouter,
});

export type AppRouter = typeof appRouter;
