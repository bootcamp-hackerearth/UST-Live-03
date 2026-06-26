"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";

export default function PosError({ error, reset }) {
  const router = useRouter();
  const isDevelopment = process.env.NODE_ENV === "development";

  useEffect(() => {
    if (isDevelopment) {
      console.error("[POS error boundary]", error);
    }
  }, [error, isDevelopment]);

  return (
    <div className="w-full h-full flex items-center justify-center px-6 py-8">
      <div className="flex flex-col items-center text-center max-w-sm w-full">
        <div className="w-16 h-16 rounded-full bg-red-50 flex items-center justify-center mb-5 shrink-0">
          <svg
            width="32" height="32" viewBox="0 0 24 24"
            fill="none" stroke="#ef4444" strokeWidth="1.8"
            strokeLinecap="round" strokeLinejoin="round"
          >
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="8"  x2="12" y2="12" />
            <line x1="12" y1="16" x2="12.01" y2="16" />
          </svg>
        </div>

        <h2 className="text-lg font-bold text-[#231F20] mb-1">
          Something went wrong
        </h2>
        <p className="text-sm text-gray-500 mb-6 leading-relaxed">
          An unexpected error occurred while loading this page.
          You can try again, or contact support if the problem persists.
        </p>

        <div className="flex gap-3 justify-center w-full">
          <button
            type="button"
            onClick={reset}
            className="px-4 py-2 rounded-lg bg-[#006E74] text-white text-sm font-semibold hover:bg-[#005a5f] transition-colors focus:outline-none focus:ring-2 focus:ring-[#006E74]/40"
          >
            Try again
          </button>
          <button
            type="button"
            onClick={() => router.push("/pos/home")}
            className="px-4 py-2 rounded-lg border border-[#006E74]/30 text-[#006E74] text-sm font-semibold hover:bg-[#006E74]/5 transition-colors focus:outline-none focus:ring-2 focus:ring-[#006E74]/20"
          >
            Go to Home
          </button>
        </div>

        {isDevelopment && (
          <div className="mt-6 w-full text-left max-h-60 overflow-y-auto border-t border-gray-100 pt-4">
            <details className="w-full">
              <summary className="text-xs text-gray-400 cursor-pointer select-none font-medium hover:text-gray-600 transition-colors">
                Error details (dev only)
              </summary>
              <pre className="mt-2 text-xs bg-gray-50 rounded-lg p-3 overflow-x-auto text-red-600 whitespace-pre-wrap break-all border border-red-100">
                {error?.message || "No error message provided"}
                {"\n\n"}
                {error?.stack || "No stack trace available"}
              </pre>
            </details>
          </div>
        )}
      </div>
    </div>
  );
}

PosError.propTypes = {
  error: PropTypes.shape({
    message: PropTypes.string,
    stack: PropTypes.string,
  }),
  reset: PropTypes.func.isRequired,
};