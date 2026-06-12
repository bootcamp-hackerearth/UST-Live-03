// components/loader/GlobalLoaderRenderer.jsx

"use client";

import React from "react";
import { useLoading } from "../../app/lib/loadingContext";
import GlobalLoader from "@/components/loader/GlobalLoader";

export default function GlobalLoaderRenderer() {
  const { isLoading } = useLoading();
  return <GlobalLoader isLoading={isLoading} />;
}