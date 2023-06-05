import { createTRPCRouter, publicProcedure } from "~/server/api/trpc";
import { z } from "zod";

interface OrdersStatusResponse {
  status: string;
}

export const ordersRouter = createTRPCRouter({
  getStatus: publicProcedure
    .input(z.object({ traceId: z.string() }))
    .query(async ({ input }) => {
      const response = await fetch(
        `${process.env.BASE_URL_CHECKOUT}/api/cart/order/id/${input.traceId}`
      );

      const data = (await response.json()) as OrdersStatusResponse;
      return data.status;
    }),

  getStatuses: publicProcedure
    .input(z.array(z.string()))
    .query(async ({ input }) => {
      const response = await fetch(
        `${process.env.BASE_URL_CHECKOUT}/api/cart/order/ids`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(input),
        }
      );

      const data = await response.json();
      return data;
    }),
});
