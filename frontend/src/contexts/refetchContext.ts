import { createContext, Dispatch, SetStateAction } from "react";

interface RefetchContextType {
  isRefetchActive: boolean;
  setIsRefetchActive: Dispatch<SetStateAction<boolean>>;
}

export const RefetchContext = createContext<RefetchContextType | undefined>(
  undefined
);
