"use client";

import { useEffect, useState } from "react";

export default function Home() {
  const [username, setUsername] = useState("");

  useEffect(() => {
    const fullUsername = localStorage.getItem("username") || "";
    setUsername(fullUsername.split("@")[0]);
  }, []);

  return (
    <div className="h-screen w-full overflow-hidden flex items-center justify-center bg-gray-200">
      <div className="bg-slate-600 text-white px-8 py-6 rounded-xl shadow-lg text-center w-full max-w-md">
        <h2 className="text-2xl font-bold mb-2">
          Welcome {username}
        </h2>

        <p className="text-gray-200">
          This is your dashboard
        </p>
      </div>
    </div>
  );
}