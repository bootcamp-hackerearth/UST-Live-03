"use client";
 
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
 
function ProtectedRoute({
  children,
}) {
  const router = useRouter();
  const [authorized, setAuthorized] =
    useState(false);
 
  useEffect(() => {
    const token =
      globalThis?.localStorage?.getItem("token");
 
    if (!token) {
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
  children: PropTypes.node,
};

export default ProtectedRoute;
