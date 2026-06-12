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
      <div className="p-6 bg-slate-50 min-h-screen">

        {isLoading ? (
          <div className="bg-white border rounded-xl p-6 text-sm text-slate-500">
            Loading dashboard...
          </div>
        ) : (
          <div className="max-w-2xl mx-auto space-y-6">

            <div className="bg-white border rounded-2xl p-8 text-center shadow-sm">
              <h1 className="text-2xl font-semibold text-slate-900">
                Welcome, {username}
              </h1>

              <p className="text-sm text-slate-500 mt-2">
                RetailPOS dashboard is ready to use.
              </p>
            </div>

            <div className="bg-slate-900 text-white rounded-2xl p-6 text-center">
              <p className="text-sm text-slate-300">
                Manage inventory, billing & reports from sidebar.
              </p>
            </div>

          </div>
        )}

      </div>
    </Layout>
  );
};

export default Home;