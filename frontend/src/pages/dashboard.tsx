import { type NextPage } from "next";
import Navbar from "~/components/Navbar";

const Dashboard: NextPage = () => {
  return (
    <>
      <Navbar />
      <main className="mt-10">Dashboard</main>
    </>
  );
};

export default Dashboard;
