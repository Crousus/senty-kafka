// contexts/orderedVideosContext.ts
import { createContext } from "react";

type OrderedVideoType = {
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
