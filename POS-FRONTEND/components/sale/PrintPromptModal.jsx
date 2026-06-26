"use client";
import { useEffect, useState } from "react";
import PropTypes from "prop-types";

const DURATION = 4000;

export default function PrintPromptModal({ onYes, onNo }) {
  const [progress, setProgress] = useState(100);

  useEffect(() => {
    const interval = setInterval(() => {
      setProgress((prev) => {
        if (prev <= 0) {
          clearInterval(interval);
          return 0;
        }
        return prev - (100 / (DURATION / 100));
      });
    }, 100);

    const timeout = setTimeout(() => {
      onNo();
    }, DURATION);

    return () => {
      clearInterval(interval);
      clearTimeout(timeout);
    };
  }, [onNo]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <button
        type="button"
        className="absolute inset-0 bg-black/40 backdrop-blur-sm"
        onClick={onNo}
        aria-label="Close prompt"
      />
      <div className="relative bg-white rounded-3xl shadow-2xl w-full max-w-sm overflow-hidden">

        <div className="px-8 pt-8 pb-6 text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-3xl">✅</span>
          </div>
          <h2 className="text-xl font-bold text-gray-900 mb-1">Order Placed!</h2>
          <p className="text-gray-500 text-sm">Would you like to print the bill?</p>
        </div>

        <div className="px-8 pb-2">
          <div className="w-full h-1.5 bg-gray-100 rounded-full overflow-hidden">
            <div
              className="h-full bg-blue-500 rounded-full transition-all duration-100 ease-linear"
              style={{ width: `${progress}%` }}
            />
          </div>
          <p className="text-xs text-gray-400 text-center mt-1.5">Auto-closing in a moment...</p>
        </div>

        <div className="px-8 pb-8 pt-4 flex gap-3">
          <button
            type="button"
            onClick={onNo}
            className="flex-1 h-12 rounded-2xl border border-gray-200 text-gray-600 font-semibold hover:bg-gray-50 transition-colors"
          >
            No
          </button>
          <button
            type="button"
            onClick={onYes}
            className="flex-1 h-12 rounded-2xl bg-[#1570ef] hover:bg-[#1264d3] text-white font-semibold transition-colors"
          >
            🖨️ Yes, Print
          </button>
        </div>

      </div>
    </div>
  );
}

PrintPromptModal.propTypes = {
  onYes: PropTypes.func,
  onNo: PropTypes.func,
};