// src/pages/_app.tsx

import { type AppType } from "next/app";
import { type Session } from "next-auth";
import { SessionProvider } from "next-auth/react";

import { api } from "~/utils/api";

import "~/styles/globals.css";

import { useState } from "react";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
import { OrderedVideosContext } from "~/contexts/orderedVideosContext";
import type { OrderedVideoType } from "~/contexts/orderedVideosContext";
import { RefetchContext } from "~/contexts/refetchContext";

const MyApp: AppType<{ session: Session | null }> = ({
  Component,
  pageProps: { session, ...pageProps },
}) => {
  const [checkedVideos, setCheckedVideos] = useState<string[]>([]);
  const [orderedVideos, setOrderedVideos] = useState<OrderedVideoType[]>([]);
  const [isRefetchActive, setIsRefetchActive] = useState<boolean>(true);

  return (
    <SessionProvider session={session}>
      <OrderedVideosContext.Provider
        value={{ orderedVideos, setOrderedVideos }}
      >
        <CheckedVideosContext.Provider
          value={{ checkedVideos, setCheckedVideos }}
        >
          <RefetchContext.Provider
            value={{ isRefetchActive, setIsRefetchActive }}
          >
            <Component {...pageProps} />
          </RefetchContext.Provider>
        </CheckedVideosContext.Provider>
      </OrderedVideosContext.Provider>
    </SessionProvider>
  );
};

export default api.withTRPC(MyApp);
