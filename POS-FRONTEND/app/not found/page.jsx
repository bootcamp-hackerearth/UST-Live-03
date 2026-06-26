// src/app/not-found/page.jsx

import Link from "next/link";

export default function NotFound() {
  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-slate-50">
      <div className="flex flex-col items-center text-center px-6 max-w-md">

        <span className="text-[120px] font-extrabold leading-none text-[#006E74]/10 select-none">
          404
        </span>

        <div className="w-16 h-16 rounded-full bg-[#006E74]/10 flex items-center justify-center -mt-4 mb-6">
          <svg
            width="32" height="32" viewBox="0 0 24 24"
            fill="none" stroke="#006E74" strokeWidth="1.8"
            strokeLinecap="round" strokeLinejoin="round"
          >
            <circle cx="11" cy="11" r="8" />
            <line x1="21" y1="21" x2="16.65" y2="16.65" />
            <line x1="11" y1="8"  x2="11" y2="11" />
            <line x1="11" y1="14" x2="11.01" y2="14" />
          </svg>
        </div>

        <h1 className="text-2xl font-bold text-[#231F20] mb-2">
          Page not found
        </h1>
        <p className="text-sm text-gray-500 mb-8 leading-relaxed">
          The page you&apos;re looking for doesn&apos;t exist or has been moved.
          Check the URL or head back to the dashboard.
        </p>

        <Link
          href="/pos/home"
          className="inline-flex items-center gap-2 px-5 py-2.5 rounded-lg bg-[#006E74] text-white text-sm font-semibold hover:bg-[#005a5f] transition-colors focus:outline-none focus:ring-2 focus:ring-[#006E74]/40"
        >
          <svg
            width="16" height="16" viewBox="0 0 24 24"
            fill="none" stroke="currentColor" strokeWidth="2.2"
            strokeLinecap="round" strokeLinejoin="round"
          >
            <polyline points="15 18 9 12 15 6" />
          </svg>
          Back to Dashboard
        </Link>
      </div>
    </div>
  );
}