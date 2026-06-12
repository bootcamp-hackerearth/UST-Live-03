"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Dashboard() {
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      router.replace("/login");
    }
  }, [router]);

  return (
    <div>
      <h1 className="text-3xl font-black tracking-tight text-slate-950">
        Welcome to POS Dashboard
      </h1>

      <p className="mt-4 max-w-2xl text-lg font-medium text-slate-500">
        Manage your inventory, products, users, and sales efficiently.
      </p>
    </div>
  );
}