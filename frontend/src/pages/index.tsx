import { type NextPage } from "next";
import { useState } from "react";
import Order from "~/components/Order";
import Summarize from "~/components/Summarize";
import Title from "~/components/Title";
import Track from "~/components/Track";
import { FaExpandArrowsAlt } from "react-icons/fa";

const Home: NextPage = () => {
  const DEBUG = false;
  const [viewMode, setViewMode] = useState(0);

  const toggleView = () => {
    setViewMode((prevMode) => (prevMode + 1) % 3);
  };

  return (
    <div className="max-w-8xl mx-auto pl-8 pr-8">
      <div className="min-h-screen border-slate-700">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <header className="grey fixed inset-x-0 top-0 z-10 mx-auto border-b border-slate-700 bg-slate-900 py-3 pl-8 pr-8 text-center">
            <div className="mx-auto flex max-w-7xl items-center justify-center px-4 sm:px-6 lg:px-8">
              <p className="text-4xl font-thin">Senty</p>
            </div>
          </header>
          <div className="grid grid-cols-1 gap-10  pt-24 pb-24 md:grid-cols-2 lg:grid-cols-6">
            {/* 1st column */}
            <div
              className={`${DEBUG ? "border-x" : ""} ${
                viewMode === 2 ? "hidden" : "col-span-1"
              }`}
            >
              <Title title="Order" />
              <br />
              <Order />
            </div>

            {/* 2nd column */}
            <div
              className={`${
                viewMode === 0
                  ? "col-span-3"
                  : viewMode === 1
                  ? "col-span-2"
                  : "col-span-3"
              } ${DEBUG ? "border-x" : ""}`}
            >
              <Title title="Track" />
              <br />
              <Track />
            </div>

            {/* 3rd column */}
            <div
              className={`${
                viewMode === 0
                  ? "col-span-2"
                  : viewMode === 1
                  ? "col-span-3"
                  : "col-span-3"
              } ${DEBUG ? "border-x" : ""}`}
            >
              <Title title="Summarize" />
              <br />
              <Summarize />
            </div>
          </div>
          <footer className="fixed inset-x-0 bottom-0 z-10 mx-auto py-4 pl-8 pr-8 text-center">
            <div className="mx-auto flex max-w-7xl items-center justify-center px-4 sm:px-6 lg:px-8">
              <div className="space-x-4">
                <button
                  onClick={toggleView}
                  className="transform rounded-full border border-slate-600 bg-slate-900 p-3 text-3xl shadow-md shadow-slate-500 transition duration-500 ease-in-out hover:rotate-180 hover:bg-slate-800"
                >
                  <FaExpandArrowsAlt />
                </button>
              </div>
            </div>
          </footer>
        </div>
      </div>
    </div>
  );
};

export default Home;
