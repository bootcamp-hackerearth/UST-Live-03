"use client";
import React from "react";
import { useRouter } from "next/navigation";
import Layout from "../Components/Layout";

export default function Forbidden() {
  const router = useRouter();

  return (
    <Layout>
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6 text-center">
        <div className="max-w-md w-full bg-white border border-slate-200 rounded-2xl shadow-xl p-8 flex flex-col items-center">
          
          <div className="w-16 h-16 bg-amber-50 rounded-2xl border border-amber-100 flex items-center justify-center mb-6">
            <svg
              className="w-8 h-8 text-amber-600"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
              />
            </svg>
          </div>

          <h1 className="text-6xl font-extrabold text-slate-950 tracking-tight mb-2">
            403
          </h1>
          <h2 className="text-xl font-bold text-slate-800 tracking-tight mb-3">
            Access Forbidden
          </h2>
          <p className="text-sm text-slate-500 mb-8 leading-relaxed">
            You don't have permission to access this resource. Please make sure 
            you are logged into the correct account or contact your system 
            administrator to request access.
          </p>

          <div className="flex flex-col sm:flex-row gap-3 w-full">
            <button
              onClick={() => router.push("/Dashboard")}
              className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-medium text-sm px-4 py-2.5 rounded-lg shadow-sm transition-colors duration-150"
            >
              Go to Dashboard
            </button>
            
            <button
              onClick={() => router.push("/Login")}
              className="flex-1 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 font-medium text-sm px-4 py-2.5 rounded-lg shadow-sm transition-colors duration-150"
            >
              Switch Account
            </button>
          </div>
        </div>
      </div>
    </Layout>
  );
}