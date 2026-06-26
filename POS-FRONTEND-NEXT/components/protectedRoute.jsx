"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

const ProtectedRoute = ({ children }) => {
  const router = useRouter();
  const [isAuthorized, setIsAuthorized] = useState(false);
  const [loading, setLoading] = useState(true);

  ProtectedRoute.propTypes = {
    children: PropTypes.node.isRequired,
  };

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token) {
      setIsAuthorized(true);
    } else {
      router.replace("/login");
    }

    setLoading(false);
  }, [router]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthorized) {
    return null;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
