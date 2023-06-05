/**
 * Run `build` or `dev` with `SKIP_ENV_VALIDATION` to skip env validation.
 * This is especially useful for Docker builds.
 */
!process.env.SKIP_ENV_VALIDATION && (await import("./src/env.mjs"));

/** @type {import("next").NextConfig} */
const config = {
  reactStrictMode: true,

  /**
   * If you have the "experimental: { appDir: true }" setting enabled, then you
   * must comment the below `i18n` config out.
   *
   * @see https://github.com/vercel/next.js/issues/41980
   */
  i18n: {
    locales: ["en"],
    defaultLocale: "en",
  },

  images: {
    domains: ["i.ytimg.com"],
  },

  eslint: {
    ignoreDuringBuilds: true,
  },

  env: {
    NEXTAUTH_SECRET: "T9QPM7PMBD9HCEy0yvOUzXlsPGz3rbGMtmMNNuJ7W78=",
    DATABASE_URL: "file:./db.sqlite",
    DISCORD_CLIENT_ID: "",
    DISCORD_CLIENT_SECRET: "",
    NEXTAUTH_URL: "http://0.0.0.0:3000",
  },

  typescript: {
    // !! WARN !!
    // Dangerously allow production builds to successfully complete even if
    // your project has type errors.
    // !! WARN !!
    ignoreBuildErrors: true,
  },

};


export default config;
