import { useContext, useState, ChangeEvent, FormEvent } from "react";
import { OrderedVideosContext } from "~/contexts/orderedVideosContext";
import { api } from "~/utils/api";
import type { NextPage } from "next";
import Spinner from "./Spinner";

const Order: NextPage = () => {
  const { orderedVideos, setOrderedVideos } = useContext(OrderedVideosContext);

  const [formData, setFormData] = useState({
    companyName: "",
    email: "",
    videoId: "",
    tokens: "",
    platform: "",
    password: "", // Add this line
    voucher: "", // Add this line
    orderType: "camunda",
  });

  console.log("formData", formData);

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

    videoIdMutation.mutate(
      {
        videoId: formData.videoId,
      },
      {
        onSuccess: (data) => {
          if (data?.isValid) {
            // If not already in orderedVideos, add it
            if (
              !orderedVideos.find((video) => video.videoId === formData.videoId)
            ) {
              fetchVideoIdMutation.mutate(formData, {
                onSuccess: (data) => {
                  console.log("EDPO MUTATED WITH: ", formData);
                  console.log("EDPO DATA: ", data);
                  if (data?.traceId) {
                    setOrderedVideos([
                      ...orderedVideos,
                      { videoId: formData.videoId, traceId: data.traceId },
                    ]);
                    setThanksMessage("Thank you for your order!");
                  } else {
                    setThanksMessage(
                      `Error: ${
                        fetchVideoIdMutation.error || "Failed to fetch Video ID"
                      }`
                    );
                  }
                },
                onError: (error) => {
                  console.error("There was an error!", error);
                  setThanksMessage(`Error: Video ID verification failed`);
                },
              });
            } else {
              setThanksMessage(
                `Error: Video ID ${formData.videoId} already exists`
              );
            }
          } else {
            setThanksMessage(
              `Error: ${
                videoIdMutation.error || "Video ID verification failed"
              }`
            );
          }
          setLoading(false); // Set loading to false
        },
        onError: (error) => {
          console.error("There was an error!", error);
          setThanksMessage(`Error: Video ID verification failed`);
          setLoading(false); // Set loading to false
        },
      }
    );
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
            className="mb-4 w-full rounded-md px-3 py-2"
          >
            <option value="">Platform</option>
            <option value="YouTube">YouTube</option>
          </select>
          <div />
          <label className="pt-8">
            Order with
            <select
              id="orderType"
              name="orderType"
              required
              value={formData.orderType}
              onChange={handleInputChange}
              className="mb-2 w-full rounded-md px-3 py-2"
            >
              <option value="camunda">Camunda</option>
              <option value="scraper">Scraper only</option>
            </select>
          </label>
          <button
            type="submit"
            className="mt-4 w-full rounded-md border border-slate-700 py-2 text-white hover:border-slate-600 hover:bg-slate-800"
          >
            {loading ? <Spinner /> : "Order"}
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
