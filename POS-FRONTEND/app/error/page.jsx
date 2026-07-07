"use client";

import { useEffect, useState } from "react";

export default function ErrorPage() {
  const [error, setError] = useState(null);

  useEffect(() => {
    const storedError = sessionStorage.getItem("globalError");

    if (storedError) {
      setError(JSON.parse(storedError));
    }
  }, []);

  const handleGoHome = () => {
    sessionStorage.removeItem("globalError");
    globalThis.location.href = "/Layout"; 
  };

  if (!error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="bg-white shadow-lg rounded-xl p-8 text-center">
          <h1 className="text-4xl font-bold text-red-600">Unexpected Error</h1>

          <p className="mt-4 text-gray-600">
            No error information is available.
          </p>

          <button
            onClick={handleGoHome}
            className="mt-6 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            Back to Dashboard
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-2xl overflow-hidden">
        {/* Header */}
        <div className="bg-red-600 text-white px-8 py-6">
          <h1 className="text-5xl font-bold">{error.status}</h1>

          <p className="text-xl mt-2">An error has occurred</p>
        </div>

        {/* Body */}
        <div className="p-8 space-y-6">
          <div>
            <h2 className="text-sm font-semibold uppercase text-gray-500">
              Message
            </h2>

            <p className="mt-2 text-lg text-gray-800">{error.message}</p>
                  </div>
                  
          <div className="pt-6 border-t flex justify-end">
            <button
              onClick={handleGoHome}
              className="px-6 py-3 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition"
            >
              Back to Dashboard
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}