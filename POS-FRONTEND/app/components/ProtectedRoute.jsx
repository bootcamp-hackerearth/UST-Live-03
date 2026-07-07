"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import PropTypes from "prop-types";

export default function ProtectedRoute({
  children,
}) {
  const router = useRouter();
  const [authorized, setAuthorized] =
    useState(false);

  useEffect(() => {
    const token =
      localStorage.getItem("token");

    if (!token || token === "null" || token === "undefined") {
      router.push("/login");
      return;
    }

    setAuthorized(true);
  }, [router]);

  if (!authorized) {
    return null;
  }

  return children;
}

ProtectedRoute.propTypes = {
  children: PropTypes.node.isRequired,
};