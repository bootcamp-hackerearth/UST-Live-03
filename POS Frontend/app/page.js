"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import logger from "@/lib/logger";
import { STORAGE_KEYS, PATHS } from "@/config/constants";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const token = logger.getStorageItem(STORAGE_KEYS.TOKEN);
    if (token) {
      router.replace(PATHS.HOME);
    } else {
      router.replace(PATHS.LOGIN);
    }
  }, [router]);

  return (
    <div style={{ display: "flex", alignItems: "center", justifyContent: "center", height: "100vh" }}>
      <p>Redirecting...</p>
    </div>
  );
}
