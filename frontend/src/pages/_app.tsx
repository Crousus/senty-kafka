import { type AppType } from "next/app";
import { type Session } from "next-auth";
import { SessionProvider } from "next-auth/react";

import { api } from "~/utils/api";

import "~/styles/globals.css";

import { useState } from "react";
import { CheckedVideosContext } from "~/contexts/checkedVideosContext";

const MyApp: AppType<{ session: Session | null }> = ({
  Component,
  pageProps: { session, ...pageProps },
}) => {
  const [checkedVideos, setCheckedVideos] = useState<string[]>([]);

  return (
    <SessionProvider session={session}>
      <CheckedVideosContext.Provider
        value={{ checkedVideos, setCheckedVideos }}
      >
        <Component {...pageProps} />
      </CheckedVideosContext.Provider>
    </SessionProvider>
  );
};

export default api.withTRPC(MyApp);
