"use client";

import { useEffect } from "react";
import NotFoundView from "@/components/NotFoundView";

export default function NotFound() {
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      globalThis.location.href = "/login";
    }
  }, []);

  return <NotFoundView />;
}
