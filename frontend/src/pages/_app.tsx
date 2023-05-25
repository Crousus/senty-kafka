import { type AppType } from "next/app";
import { type Session } from "next-auth";
import { SessionProvider } from "next-auth/react";

import { api } from "~/utils/api";

import "~/styles/globals.css";

import { useState } from "react";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";
import { OrderedVideosContext } from "~/contexts/orderedVideosContext";

const MyApp: AppType<{ session: Session | null }> = ({
  Component,
  pageProps: { session, ...pageProps },
}) => {
  const [checkedVideos, setCheckedVideos] = useState<string[]>([]);
  const [orderedVideos, setOrderedVideos] = useState<string[]>([]);

  return (
    <SessionProvider session={session}>
      <OrderedVideosContext.Provider
        value={{ orderedVideos, setOrderedVideos }}
      >
        <CheckedVideosContext.Provider
          value={{ checkedVideos, setCheckedVideos }}
        >
          <Component {...pageProps} />
        </CheckedVideosContext.Provider>
      </OrderedVideosContext.Provider>
    </SessionProvider>
  );
};

export default api.withTRPC(MyApp);
