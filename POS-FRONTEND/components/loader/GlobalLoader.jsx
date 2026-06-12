// components/loader/GlobalLoader.jsx

"use client";

import React from "react";
import PropTypes from "prop-types";

export default function GlobalLoader({ isLoading }) {
  if (!isLoading) return null;

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
      <div className="flex flex-col items-center gap-4">
        <div className="w-12 h-12 border-4 border-[#006E74]/20 border-t-[#006E74] rounded-full animate-spin"></div>
        <p className="text-white text-sm font-medium">Loading...</p>
      </div>
    </div>
  );
}

GlobalLoader.propTypes = {
  isLoading: PropTypes.bool.isRequired,
};