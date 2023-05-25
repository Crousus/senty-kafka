// contexts/orderedVideosContext.ts
import { createContext } from "react";

type OrderedVideosContextType = {
  orderedVideos: string[];
  setOrderedVideos: React.Dispatch<React.SetStateAction<string[]>>;
};

export const OrderedVideosContext = createContext<
  OrderedVideosContextType | undefined
>(undefined);
