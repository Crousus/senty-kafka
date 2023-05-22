import { type NextPage } from "next";
import { useState } from "react";
import Order from "~/components/Order";
import Summarize from "~/components/Summarize";
import Title from "~/components/Title";
import Track from "~/components/Track";
import { FaExpandArrowsAlt, FaRedo } from "react-icons/fa";

const Home: NextPage = () => {
  const DEBUG = true;
  const [isLargerView, setIsLargerView] = useState(true);

  const toggleLargerView = () => {
    setIsLargerView(!isLargerView);
  };

  const refreshPage = () => {
    // TODO: change this to not reload but just refresh states
    window.location.reload();
  };

  return (
    <>
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 gap-10 md:grid-cols-2 lg:grid-cols-6">
          {/* 1st column */}
          <div className={`${DEBUG ? "border-x" : ""} col-span-1`}>
            <Title title="Order" />
            <br />
            <Order />
          </div>

          {/* 2nd column */}
          <div
            className={`${isLargerView ? "col-span-3" : "col-span-2"} ${
              DEBUG ? "border-x" : ""
            }`}
          >
            <Title title="Track" />
            <br />
            <Track />
          </div>

          {/* 3rd column */}
          <div
            className={`${isLargerView ? "col-span-2" : "col-span-3"} ${
              DEBUG ? "border-x" : ""
            }`}
          >
            <Title title="Summarize" />
            <br />
            <Summarize />
          </div>
        </div>

        {/* Footer */}
        <footer className="border-grey fixed inset-x-0 bottom-0 w-full border-t py-3 text-center">
          <div className="mx-auto flex max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
            <p className="text-4xl font-thin">Senty</p>
            <div className="space-x-4">
              <button onClick={toggleLargerView} className="text-4xl">
                <FaExpandArrowsAlt />
              </button>
              <button onClick={refreshPage} className="text-4xl">
                <FaRedo />
              </button>
            </div>
          </div>
        </footer>
      </div>
    </>
  );
};

export default Home;
