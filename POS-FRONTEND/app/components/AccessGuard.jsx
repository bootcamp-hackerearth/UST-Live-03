"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";

export default function AccessGuard({
  children,
  requiredPath,
}) {
  const router = useRouter();
  const [authorized, setAuthorized] = useState(false);

  useEffect(() => {
    const nodes = JSON.parse(
      localStorage.getItem("authorizedNodes") || "[]"
    );

    const hasAccess = nodes.some(
      (node) => node.path === requiredPath
    );

    if (hasAccess) {
      setAuthorized(true);
    } else {
      router.push("/dashboard1");
    }
  }, [requiredPath, router]);

  if (!authorized) {
    return null;
  }

  return children;
}

AccessGuard.propTypes = {
  children: PropTypes.node.isRequired,
  requiredPath: PropTypes.string.isRequired,
};