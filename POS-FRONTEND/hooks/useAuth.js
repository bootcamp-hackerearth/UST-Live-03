"use client";

import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";

export default function useAuth() {
  const router = useRouter();
  const currentPath = usePathname();
  const [authorized, setAuthorized] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      const res = await fetch("/api/authCheck");

      if (!res.ok) {
        if (currentPath !== "/register") {
          router.push("/login");
        }
        return;
      }

      setAuthorized(true);
    };

    checkAuth();
  }, [currentPath]);

  return authorized;
}