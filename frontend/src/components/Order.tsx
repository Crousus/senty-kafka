import { useContext, useState, ChangeEvent, FormEvent } from "react";
import { OrderedVideosContext } from "~/contexts/orderedVideosContext";
import { api } from "~/utils/api";
import type { NextPage } from "next";

const Order: NextPage = () => {
  const { orderedVideos, setOrderedVideos } = useContext(OrderedVideosContext);

  const [formData, setFormData] = useState({
    companyName: "",
    email: "",
    videoId: "",
    tokens: "",
    platform: "",
  });
  const [thanksMessage, setThanksMessage] = useState("");
  const [loading, setLoading] = useState(false); // New state for loading

  const handleInputChange = (
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const videoIdMutation = api.videoIds.checkVideoId.useMutation();
  const fetchVideoIdMutation = api.videoIds.fetchVideoId.useMutation();

  const handleFormSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true); // Set loading to true

    try {
      videoIdMutation.mutate({
        videoId: formData.videoId,
      });

      if (!vidResp.data || !vidResp.data.isValid) {
        setThanksMessage(
          `Error: ${vidResp.error || "Video ID verification failed"}`
        );
      } else {
        // If not already in orderedVideos, add it
        if (!orderedVideos.includes(formData.videoId)) {
          setOrderedVideos([...orderedVideos, formData.videoId]);

          try {
            const fetchResp = await fetchVideoIdMutation.mutate(formData);
            if (!fetchResp.data || !fetchResp.data.isValid) {
              setThanksMessage(
                `Error: ${fetchResp.error || "Failed to fetch Video ID"}`
              );
            } else {
              console.log("traceId: ", fetchResp.data.traceId);
              setThanksMessage("Thank you for your order!");
            }
          } catch (err) {
            console.log("fetchVideoIdMutation err", err);
            setThanksMessage(`Error: Failed to fetch Video ID`);
          }
        }
      }
    } catch (err) {
      console.log("videoIdMutation err", err);
      setThanksMessage(`Error: Video ID verification failed`);
    }
    setLoading(false); // Set loading to false
  };

  const toggleVoucherField = () => {
    if (formData.tokens === "voucher") {
      document.getElementById("voucher-code-field")!.style.display = "block";
      document.getElementById("voucher")!.setAttribute("required", "required");
    } else {
      document.getElementById("voucher-code-field")!.style.display = "none";
      document.getElementById("voucher")!.removeAttribute("required");
    }
  };

  return (
    <div>
      <div className="flex justify-center bg-slate-900">
        <form
          id="order-form"
          onSubmit={handleFormSubmit}
          className="w-full space-y-4 rounded-md border border-slate-700 pt-4 pl-4 pr-4 md:max-w-2xl"
        >
          {/* <label htmlFor="company-name" className="block">
            Company Name
          </label> */}
          <input
            type="text"
            id="company-name"
            name="companyName"
            placeholder="Company"
            required
            value={formData.companyName}
            onChange={handleInputChange}
            className="w-full rounded-md px-4 py-2"
          />

          {/* <label htmlFor="email" className="block">
            Email
          </label> */}
          <input
            type="text"
            id="email"
            name="email"
            placeholder="Email"
            required
            value={formData.email}
            onChange={handleInputChange}
            className="rounded-mdpx-4 w-full px-3 py-2"
          />

          {/* <label htmlFor="password" className="block">
            Password
          </label> */}
          <input
            type="password"
            id="password"
            name="password"
            placeholder="Password"
            required
            value={formData.password}
            onChange={handleInputChange}
            className="w-full rounded-md px-3 py-2"
          />

          {/* <label htmlFor="video-id" className="block">
            Video ID
          </label> */}
          <input
            type="text"
            id="video-id"
            name="videoId"
            placeholder="Video ID"
            required
            value={formData.videoId}
            onChange={handleInputChange}
            className="w-full rounded-md px-3 py-2"
          />

          {/* <label htmlFor="tokens" className="block">
            Tokens
          </label> */}
          <select
            id="tokens"
            name="tokens"
            placeholder="Tokens"
            required
            value={formData.tokens}
            onChange={(e) => {
              handleInputChange(e);
              toggleVoucherField();
            }}
            className="w-full rounded-md px-3 py-2"
          >
            <option value="">Tokens</option>
            <option value="10">10</option>
            <option value="100">100</option>
            <option value="1000">1000</option>
            <option value="pay-as-you-go">Pay as you go</option>
            <option value="unlimited">Unlimited</option>
            <option value="voucher">Voucher</option>
          </select>

          <div id="voucher-code-field" style={{ display: "none" }}>
            {/* <label htmlFor="voucher" className="block">
              Voucher Code
            </label> */}
            <input
              type="text"
              id="voucher"
              name="voucher"
              placeholder="Voucher Code"
              value={formData.voucher}
              onChange={handleInputChange}
              className="w-full rounded-md px-3 py-2"
            />
          </div>
          {/* <label htmlFor="platform" className="block">
            Social Media Platform
          </label> */}
          <select
            id="platform"
            name="platform"
            required
            value={formData.platform}
            onChange={handleInputChange}
            className="w-full rounded-md px-3 py-2"
          >
            <option value="">Platform</option>
            <option value="YouTube">YouTube</option>
          </select>
          <button
            type="submit"
            className="mt-4 w-full rounded-md border border-slate-700 py-2 text-white hover:border-slate-600 hover:bg-slate-800"
          >
            {loading ? (
              <div role="status" className="flex justify-center">
                <svg
                  aria-hidden="true"
                  className="mr-2 h-6 w-6 animate-spin fill-blue-600 text-gray-200 dark:text-gray-600"
                  viewBox="0 0 100 101"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z"
                    fill="currentColor"
                  />
                  <path
                    d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z"
                    fill="currentFill"
                  />
                </svg>
                <span className="sr-only">Loading...</span>
              </div>
            ) : (
              "Order"
            )}
          </button>
          <div id="thanks" className="pb-4">
            {thanksMessage}
          </div>
        </form>
      </div>
    </div>
  );
};

export default Order;
