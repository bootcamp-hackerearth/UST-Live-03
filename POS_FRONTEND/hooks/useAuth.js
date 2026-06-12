"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function useAuth() {
  const router = useRouter();
  const [authorized, setAuthorized] = useState(false);

  useEffect(() => {
    let mounted = true;

    fetch("/api/me", { credentials: "include" })
      .then((res) => res.json())
      .then((data) => {
        if (!mounted) return;

        if (!data.authenticated) {
          router.replace("/login");
          return;
        }

        setAuthorized(true);
      })
      .catch(() => {
        if (mounted) {
          router.replace("/login");
        }
      });

    return () => {
      mounted = false;
    };
  }, [router]);

  return authorized;
}