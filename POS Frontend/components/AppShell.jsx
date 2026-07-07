"use client";

import { useEffect, useMemo, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import PropTypes from "prop-types";

import DashboardLayout from "./DashboardLayout";
import { PATHS, STORAGE_KEYS } from "@/config/constants";

const PUBLIC_PATHS = new Set(["/", "/login", "/register", "/403", "/404", "/500"]);

export default function AppShell({ children }) {
  const pathname = usePathname();
  const router = useRouter();
  const [status, setStatus] = useState("loading");

  const isPublicRoute = useMemo(() => PUBLIC_PATHS.has(pathname), [pathname]);

  useEffect(() => {
    if (isPublicRoute) {
      setStatus("public");
      return;
    }

    const token = globalThis.window?.localStorage.getItem(STORAGE_KEYS.TOKEN) ?? null;
    const hasValidToken = typeof token === "string" && token.trim() !== "";

    if (!hasValidToken) {
      globalThis.window?.localStorage.removeItem(STORAGE_KEYS.TOKEN);
      globalThis.window?.localStorage.removeItem(STORAGE_KEYS.USERNAME);
      setStatus("redirecting");
      router.replace(PATHS.LOGIN);
      return;
    }

    setStatus("protected");
  }, [isPublicRoute, pathname, router]);

  if (status === "loading") return null;
  if (status === "redirecting") return null;
  if (isPublicRoute) return <>{children}</>;

  if (status === "protected") {
    return <DashboardLayout>{children}</DashboardLayout>;
  }

  return <>{children}</>;
}

AppShell.propTypes = {
  children: PropTypes.node.isRequired,
};
