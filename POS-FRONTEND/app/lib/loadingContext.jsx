// app/lib/loadingContext.jsx
"use client";

import React, { createContext, useContext, useState, useCallback, useMemo } from "react";
import PropTypes from "prop-types";

const LoadingContext = createContext();

export function LoadingProvider({ children }) {
  const [isLoading, setIsLoading] = useState(false);

  const showLoader = useCallback(() => setIsLoading(true), []);
  const hideLoader = useCallback(() => setIsLoading(false), []);

  const contextValue = useMemo(() => ({
    isLoading,
    showLoader,
    hideLoader
  }), [isLoading, showLoader, hideLoader]);

  return (
    <LoadingContext.Provider value={contextValue}>
      {children}
    </LoadingContext.Provider>
  );
}

LoadingProvider.propTypes = {
  children: PropTypes.node.isRequired,
};

export function useLoading() {
  const context = useContext(LoadingContext);
  if (!context) {
    throw new Error("useLoading must be used within LoadingProvider");
  }
  return context;
}