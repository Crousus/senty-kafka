import { createContext } from "react";
import type { Dispatch, SetStateAction } from "react";

export const CheckedVideosContext = createContext<{
  checkedVideos: string[];
  setCheckedVideos: Dispatch<SetStateAction<string[]>>;
}>({
  checkedVideos: [],
  setCheckedVideos: () => {
    throw new Error("setCheckedVideos is not implemented");
  },
});
