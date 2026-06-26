"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import PropTypes from "prop-types";

export default function RouteGuard({ children }) {
  const pathname = usePathname();
  const router = useRouter();

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAccess = async () => {
      const publicRoutes = ["/", "/login", "/register"];

      if (publicRoutes.includes(pathname) || pathname === "/home") {
        setLoading(false);
        return;
      }

      const token = localStorage.getItem("token");

      if (!token) {
        router.replace("/login");
        return;
      }

      try {
        const response = await fetch(
          "http://localhost:8080/api/node/checkaccess",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
              path: pathname,
            }),
          }
        );

        const allowed = await response.json();

        if (!allowed) {
          router.replace("/home");
          return;
        }

        setLoading(false);
      } catch {
        router.replace("/login");
      }
    };

    checkAccess();
  }, [pathname, router]);

  if (loading) {
    return null;
  }

  return children;
}

RouteGuard.propTypes = {
  children: PropTypes.node.isRequired,
};