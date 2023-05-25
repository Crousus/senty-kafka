import { useContext, useState, FormEvent } from "react";
import { OrderedVideosContext } from "~/contexts/orderedVideosContext";
import { api } from "~/utils/api";
import type { NextPage } from "next";

const Order: NextPage = () => {
  const { orderedVideos, setOrderedVideos } = useContext(OrderedVideosContext);

  const [formData, setFormData] = useState({
    companyName: "",
    email: "",
    password: "",
    videoId: "",
    tokens: "",
    voucher: "",
    platform: "",
  });
  const [thanksMessage, setThanksMessage] = useState("");

  const handleInputChange = (
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const videoIdResponse = api.videoIds.checkVideoId.useQuery({
    videoId: formData.videoId,
  });

  const fetchVideoIdResponse = api.videoIds.fetchVideoId.useQuery({
    videoId: formData.videoId,
  });

  const handleFormSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    videoIdResponse.refetch().catch((err) => {
      console.log("videoIdResponse err", err);
    });

    if (!videoIdResponse.data.isValid) {
      setThanksMessage(`Error: ${videoIdResponse.error}`);
    } else {
      // If not already in orderedVideos, add it
      if (!orderedVideos.includes(formData.videoId)) {
        setOrderedVideos([...orderedVideos, formData.videoId]);
        fetchVideoIdResponse.refetch().catch((err) => {
          console.log("fetchVideoIdResponse err", err);
        });
      }
      setThanksMessage("Thank you for your order!");
    }
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
            Order
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
