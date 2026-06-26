"use client";

import { useEffect } from "react";
import {
  useRouter,
  usePathname,
} from "next/navigation";

export default function AuthGuard({
  children,
}) {
  const router = useRouter();
  const pathname =
    usePathname();

  useEffect(() => {
    const token =
      localStorage.getItem(
        "token"
      );

    if (!token) {
      router.replace("/login");
      return;
    }

    const nodes =
      JSON.parse(
        localStorage.getItem("nodes")
      ) || [];

    const currentPath =
      pathname.toLowerCase();

    if (
      currentPath === "/home"
    ) {
      return;
    }

    const allowed =
      nodes.some((node) => {
        const nodePath =
          node.path.startsWith("/")
            ? node.path.toLowerCase()
            : "/" +
            node.path.toLowerCase();

        return (
          nodePath === currentPath
        );
      });

    if (!allowed) {
      alert(
        "Access Denied"
      );
      router.replace("/home");
    }
  }, [pathname, router]);

  return children;
}