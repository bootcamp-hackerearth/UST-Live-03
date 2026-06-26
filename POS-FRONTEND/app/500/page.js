"use client";
import React from "react";
import { useRouter } from "next/navigation";
import Layout from "@/app/components/Layout";
 
export default function InternalServerErrorPage() {
  const router = useRouter();
 
  return (
    <Layout>
      <div className="min-h-screen bg-stone-50 flex flex-col items-center justify-center p-6 text-center select-none font-sans antialiased text-black">
        
        <div className="w-full max-w-md bg-white border border-black p-10 rounded-none shadow-sm">
          
          <div className="inline-flex items-center justify-center w-16 h-16 bg-black text-white text-2xl font-bold tracking-tight mb-6 rounded-none">
            500
          </div>
 
          <h1 className="text-xl font-bold tracking-tight uppercase mb-2">
            Server Error
          </h1>
 
          <p className="text-xs uppercase tracking-wider text-stone-500 mb-8 max-w-xs mx-auto leading-relaxed">
            Something went wrong on our end. Please try syncing the page or contact administration if the issue persists.
          </p>
 
          <div className="flex flex-col gap-2">
            <button
              onClick={() => router.refresh()}
              className="w-full bg-black text-white font-semibold text-xs py-3 uppercase tracking-widest border border-black hover:bg-stone-800 transition duration-150 rounded-none"
            >
              Try Again / Refresh Page
            </button>
 
            <button
              onClick={() => router.push("/home")}
              className="w-full bg-white text-black font-semibold text-xs py-3 uppercase tracking-widest border border-stone-200 hover:border-black transition duration-150 rounded-none"
            >
              Return Home
            </button>
          </div>
        </div>
      </div>
    </Layout>
  );
}