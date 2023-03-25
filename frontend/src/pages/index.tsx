import { type NextPage } from "next";
import Navbar from "~/components/Navbar";

const Home: NextPage = () => {
  return (
    <>
      <Navbar />
      <main className="mt-10">Home</main>
    </>
  );
};

export default Home;
