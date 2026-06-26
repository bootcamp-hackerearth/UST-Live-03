"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Layout from "@/app/components/Layout";

const Home = () => {
  const router = useRouter();

  const [isLoading, setIsLoading] = useState(true);
  const [username, setUsername] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    console.log("TOKEN:", token);
    const storedUsername = localStorage.getItem("username");

    if (!token || !storedUsername) {
      router.push("/");
      return;
    }

    setUsername(storedUsername);

    const timer = setTimeout(() => setIsLoading(false), 300);
    return () => clearTimeout(timer);
  }, [router]);

  return (
    <Layout username={username}>
      <div className="space-y-6">

        {isLoading ? (
          <div className="bg-white rounded-xl p-6 text-sm text-gray-500 shadow-sm">
            Loading dashboard...
          </div>
        ) : (
          <>
            {/*  HERO SECTION (MATCHES THEME) */}
            <div className="rounded-2xl p-8 text-white bg-gradient-to-r from-[#0a1f66] via-[#1e3a8a] to-[#2563eb] shadow-md">
              <h1 className="text-3xl font-bold">
                Welcome, {username} 
              </h1>

              <p className="text-blue-100 mt-2">
                Your RetailPOS dashboard is ready. Manage everything in one place.
              </p>
            </div>

          </>
        )}
      </div>
    </Layout>
  );
};

export default Home;