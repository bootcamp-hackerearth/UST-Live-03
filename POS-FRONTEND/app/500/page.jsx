"use client";
import React from "react";
import { useRouter } from "next/navigation";
import Layout from "../Components/Layout";

export default function InternalServerError() {
  const router = useRouter();

  return (
    <Layout>
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6 text-center">
        <div className="max-w-md w-full bg-white border border-slate-200 rounded-2xl shadow-xl p-8 flex flex-col items-center">
          <div className="w-16 h-16 bg-rose-50 rounded-2xl border border-rose-100 flex items-center justify-center mb-6">
            <svg
              className="w-8 h-8 text-rose-600"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
          </div>

          <h1 className="text-6xl font-extrabold text-slate-950 tracking-tight mb-2">
            500
          </h1>
          <h2 className="text-xl font-bold text-slate-800 tracking-tight mb-3">
            Internal Server Error
          </h2>
          <p className="text-sm text-slate-500 mb-8 leading-relaxed">
            The server encountered an unexpected error configuration and could
            not complete your list view request. Please try your operation again
            or contact an administrator.
          </p>

          <div className="flex flex-col sm:flex-row gap-3 w-full">
            <button
              onClick={() => globalThis.location.reload()}
              className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-medium text-sm px-4 py-2.5 rounded-lg shadow-sm transition-colors duration-150"
            >
              Retry Request
            </button>
            
            {/* Hard-routed back to Dashboard to prevent an infinite error redirect loop */}
            <button
              onClick={() => router.push("/Dashboard")}
              className="flex-1 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 font-medium text-sm px-4 py-2.5 rounded-lg shadow-sm transition-colors duration-150"
            >
              Go to Dashboard
            </button>
          </div>
        </div>
      </div>
    </Layout>
  );
}