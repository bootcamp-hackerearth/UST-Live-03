"use client";
import React from "react";
import { useRouter } from "next/navigation";
import Layout from "@/app/components/Layout";
 
export default function NotFoundPage() {
  const router = useRouter();
 
  return (
    <Layout>
      <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-6 text-center select-none font-sans">
        <div className="w-full max-w-md bg-white rounded-2xl border border-slate-200/80 p-10 shadow-sm">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-rose-50 text-rose-600 text-3xl font-bold mb-6">
            404
          </div>
 
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight mb-2">
            An Error Occurred
          </h1>
 
          <p className="text-sm text-slate-500 mb-8 max-w-sm mx-auto">
            The data request failed or the system record you are trying to
            access could not be found.
          </p>
 
          <div className="flex flex-col gap-3">
            <button
              onClick={() => router.refresh()}
              className="w-full bg-blue-600 text-white font-medium text-sm py-2.5 rounded-lg hover:bg-blue-700 shadow-sm shadow-blue-500/10 transition-colors"
            >
              Try Again / Refresh Page
            </button>
 
            <button
              onClick={() => router.push("/home")}
              className="w-full bg-slate-100 text-slate-600 font-medium text-sm py-2.5 rounded-lg hover:bg-slate-200 transition-colors"
            >
              Go Back
            </button>
          </div>
        </div>
      </div>
    </Layout>
  );
}
 
 