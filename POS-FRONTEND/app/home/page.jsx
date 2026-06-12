'use client';

import Sidebar from "../../components/layout/Sidebar";
import Header from "../../components/layout/Header";

const HomePage = () => {
  return (
    <Sidebar>
      <div className="flex flex-col min-h-screen bg-white">
        <div className="fixed top-0 left-64 right-0 z-50 bg-white">
          <Header />
        </div>
        <div
          className="pt-16 p-10"
          style={{ fontFamily: "'Poppins', sans-serif" }}
        >
          <div className="bg-gradient-to-r from-blue-400 to-indigo-600 rounded-3xl p-12 shadow-2xl text-white">
            <h1 className="text-3xl font-bold mb-4">
              Welcome
            </h1>
            <p className="text-base opacity-90">
              Manage Sales, Products and Users efficiently.
            </p>
          </div>
        </div>
      </div>
    </Sidebar>
  );
};

export default HomePage;