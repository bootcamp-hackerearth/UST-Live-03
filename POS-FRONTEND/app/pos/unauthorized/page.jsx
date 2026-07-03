// app/unauthorized/page.jsx

import Link from "next/link";

export default function Unauthorized() {
  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-slate-50">
      <div className="flex flex-col items-center text-center px-6 max-w-md">

        <div className="w-16 h-16 rounded-full bg-[#006E74]/10 flex items-center justify-center -mt-4 mb-6">
          <svg
            width="32" height="32" viewBox="0 0 24 24"
            fill="none" stroke="#006E74" strokeWidth="1.8"
            strokeLinecap="round" strokeLinejoin="round"
          >
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            <line x1="12" y1="8" x2="12" y2="12" />
            <line x1="12" y1="16" x2="12.01" y2="16" />
          </svg>
        </div>

        <h1 className="text-2xl font-bold text-[#231F20] mb-2">
          Access Denied
        </h1>
        <p className="text-sm text-gray-500 mb-8 leading-relaxed">
          You don&apos;t have permission to access this page. 
          <p>
            Please contact your administrator.
          </p>
        </p>

        {/* Navigation Action */}
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
          Back to Home
        </Link>
      </div>
    </div>
  );
}