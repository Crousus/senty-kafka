import { type NextPage } from "next";
import React, { ChangeEvent, FormEvent, useState } from "react";
import axios from "axios";

const Home: NextPage = () => {
  const [count, setCount] = useState(0);
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

  const handleFormSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const response = await axios.post("./api/cart/order", formData, {
        headers: { "Content-Type": "application/json" },
      });

      setCount(count + 1);
      setThanksMessage(
        `Thank you for order #${count}<br>Trace id: <b>${response.data.traceId}</b>`
      );
    } catch (error) {
      setThanksMessage(
        `Error placing order<br>Error: <b>${error.response.data.error}</b>`
      );
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
      <section className="flex h-screen max-h-[600px] flex-col items-center justify-center bg-gradient-to-br from-white to-[#007700] bg-cover bg-center px-4 text-center">
        <h1 className="mb-4 text-[3rem] font-bold text-black">Senty</h1>
        <p className="mb-8 text-[1.5rem] text-black">
          Get actionable insights from your social media data.
        </p>
        <img
          src="https://p-john.com/wp-content/uploads/2022/09/wallpaper.png"
          alt="Senty Image"
          className="mt-8 mb-20 w-[50%] max-w-[600px]"
        />
      </section>
      <div className="flex justify-center">
        <form
          id="order-form"
          onSubmit={handleFormSubmit}
          className="w-full space-y-4 md:max-w-2xl"
        >
          <h2 className="mt-0.5 text-center text-[2rem]">Order Form</h2>

          <label htmlFor="company-name" className="block">
            Company Name
          </label>
          <input
            type="text"
            id="company-name"
            name="companyName"
            required
            value={formData.companyName}
            onChange={handleInputChange}
            className="w-full rounded-md border border-gray-300 px-4 py-2"
          />

          <label htmlFor="email" className="block">
            Email
          </label>
          <input
            type="email"
            id="email"
            name="email"
            required
            value={formData.email}
            onChange={handleInputChange}
            className="w-full rounded-md border border-gray-300 px-4 py-2"
          />

          <label htmlFor="password" className="block">
            Password
          </label>
          <input
            type="password"
            id="password"
            name="password"
            required
            value={formData.password}
            onChange={handleInputChange}
            className="w-full rounded-md border border-gray-300 px-4 py-2"
          />

          <label htmlFor="video-id" className="block">
            Video ID
          </label>
          <input
            type="text"
            id="video-id"
            name="videoId"
            required
            value={formData.videoId}
            onChange={handleInputChange}
            className="w-full rounded-md border border-gray-300 px-4 py-2"
          />

          <label htmlFor="tokens" className="block">
            Tokens
          </label>
          <select
            id="tokens"
            name="tokens"
            required
            value={formData.tokens}
            onChange={(e) => {
              handleInputChange(e);
              toggleVoucherField();
            }}
            className="w-full rounded-md border border-gray-300 px-4 py-2"
          >
            <option value="">Select Tokens</option>
            <option value="10">10</option>
            <option value="100">100</option>
            <option value="1000">1000</option>
            <option value="pay-as-you-go">Pay as you go</option>
            <option value="unlimited">
              Unlimited (
              <i>Hey Siri, play Never Gonna Give You Up by Rick Astley</i>)
            </option>
            <option value="voucher">Voucher</option>
          </select>

          <div id="voucher-code-field" style={{ display: "none" }}>
            <label htmlFor="voucher" className="block">
              Voucher Code
            </label>
            <input
              type="text"
              id="voucher"
              name="voucher"
              value={formData.voucher}
              onChange={handleInputChange}
              className="w-full rounded-md border border-gray-300 px-4 py-2"
            />
          </div>
          <label htmlFor="platform" className="block">
            Social Media Platform
          </label>
          <select
            id="platform"
            name="platform"
            required
            value={formData.platform}
            onChange={handleInputChange}
            className="w-full rounded-md border border-gray-300 px-4 py-2"
          >
            <option value="">Select Platform</option>
            <option value="YouTube">YouTube</option>
          </select>
          <button
            type="submit"
            className="mt-4 w-full rounded-md bg-blue-600 py-2 text-white hover:bg-blue-800"
          >
            Order
          </button>
          <div id="thanks" className="mt-4">
            {thanksMessage}
          </div>
        </form>
      </div>
    </div>
  );
};

export default Home;
