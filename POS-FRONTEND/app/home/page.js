import React from "react";

export default function Home() {
  return (
    <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f0f7f4] font-sans flex overflow-hidden">
      <div className="flex-1 bg-white flex flex-col justify-between p-0 overflow-hidden">
        <div className="flex-1 flex flex-col items-center justify-center px-10 pt-12 pb-6 text-center">
          <h1 className="text-[34px] font-bold text-[#1a1a1a] mb-3">
            Welcome to <span className="text-brand">RetailPOS</span>
          </h1>
          <p className="text-base text-gray-600 max-w-[480px]">
            Fast. Simple. Smart retail management — built for modern businesses.
          </p>
        </div>

        <div className="flex justify-center gap-5 px-10 pb-10 flex-wrap">
          <div className="bg-[#f4fbf7] border border-solid border-[#c8e6c9] py-7 px-6 rounded-xl w-[220px] text-center shrink-0">
            <span className="text-[32px] mb-3 block">📦</span>
            <p className="text-[15px] font-bold text-brand mb-2">Inventory Control</p>
            <p className="text-shrink text-xs text-gray-500 leading-normal">Track stock in real time with zero complexity.</p>
          </div>

          <div className="bg-[#f4fbf7] border border-solid border-[#c8e6c9] py-7 px-6 rounded-xl w-[220px] text-center shrink-0">
            <span className="text-[32px] mb-3 block">📊</span>
            <p className="text-[15px] font-bold text-brand mb-2">Sales Insights</p>
            <p className="text-shrink text-xs text-gray-500 leading-normal">Understand your business in one clear dashboard.</p>
          </div>

          <div className="bg-[#f4fbf7] border border-solid border-[#c8e6c9] py-7 px-6 rounded-xl w-[220px] text-center shrink-0">
            <span className="text-[32px] mb-3 block">⚡</span>
            <p className="text-[15px] font-bold text-brand mb-2">Fast Billing</p>
            <p className="text-shrink text-xs text-gray-500 leading-normal">Quick, accurate, and efficient POS workflow.</p>
          </div>
        </div>

        <div className="bg-brand py-3.5 px-10 text-center shrink-0">
          <p className="text-white/85 text-xs font-medium m-0">
            🛒 RetailPOS &middot; Built for modern retail teams
          </p>
        </div>
      </div>
    </div>
  );
}