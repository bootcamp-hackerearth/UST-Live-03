"use client";

import React from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Layout from "@/app/components/Layout";

export default function ErrorPage() {
  const router = useRouter();
  const params = useSearchParams();

  const status = params.get("status") || "403";

  const config = {
    401: {
      code: "401",
      title: "Unauthorized",
      message: "You must log in to access this page.",
      colorBg: "bg-blue-100",
      colorText: "text-blue-600",
    },
    403: {
      code: "403",
      title: "Access Denied",
      message: "You do not have permission to access this page.",
      colorBg: "bg-amber-100",
      colorText: "text-amber-600",
    },
    500: {
      code: "500",
      title: "Server Error",
      message: "Something went wrong on the server. Please try again later.",
      colorBg: "bg-red-100",
      colorText: "text-red-600",
    },
  };

  const current = config[status] || config["403"];

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6">
      <div className="w-full max-w-md bg-white rounded-2xl border border-slate-200 shadow-lg p-10 text-center">
        <div
          className={`w-16 h-16 mx-auto flex items-center justify-center rounded-full text-3xl font-bold mb-6 ${current.colorBg} ${current.colorText}`}
        >
          {current.code}
        </div>

        <h1 className="text-2xl font-semibold text-slate-900 mb-2">
          {current.title}
        </h1>

        <p className="text-slate-500 text-sm mb-8">{current.message}</p>

        <div className="flex flex-col gap-3">
          <button
            onClick={() => router.push("/dashboard")}
            className="w-full bg-slate-900 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-slate-800 transition"
          >
            Go to Dashboard
          </button>

          <button
            onClick={() => router.back()}
            className="w-full border border-slate-300 text-slate-700 py-2.5 rounded-lg text-sm font-medium hover:bg-slate-100 transition"
          >
            Go Back
          </button>
        </div>
      </div>
    </div>
  );
}
