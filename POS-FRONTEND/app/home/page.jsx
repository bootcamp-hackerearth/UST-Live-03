"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import POSLayout from "../components/PosLayout";
import axiosInstance from "../services/axiosInstance";

function Home() {
  const [user, setUser] = useState(null);
  const [nodes, setNodes] = useState([]);

  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem("token");
    const username = localStorage.getItem("username");

    if (!token) {
      router.push("/login");
      return;
    }

    axiosInstance
      .get("/home")
      .then((res) => {
        const data = res.data;

        if (Array.isArray(data)) {
          setNodes(data);
          setUser({ name: username });
        } else {
          setNodes(data.nodes || []);
          setUser(data.user || { name: username });
        }
      })
      .catch(() => {
        localStorage.clear();
        router.push("/login");
      });
  }, [router]);

  const handleLogout = () => {
    localStorage.clear();
    router.push("/login");
  };

  return (
    <POSLayout user={user} nodes={nodes} handleLogout={handleLogout}>
      <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
        <h1 className="text-lg font-semibold text-gray-800 mb-2">
          Welcome back, {user?.name}
        </h1>

        <p className="text-sm text-gray-500">
          You're logged into the POS system.
        </p>
      </div>
    </POSLayout>
  );
}

export default Home;