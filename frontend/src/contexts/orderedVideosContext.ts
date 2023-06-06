// contexts/orderedVideosContext.ts

import { createContext } from "react";

export type OrderedVideoType = {
  videoId: string;
  traceId: string;
};

type OrderedVideosContextType = {
  orderedVideos: OrderedVideoType[];
  setOrderedVideos: React.Dispatch<React.SetStateAction<OrderedVideoType[]>>;
};

export const OrderedVideosContext = createContext<
  OrderedVideosContextType | undefined
>(undefined);
