"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function AccessDenied() {
  const router = useRouter();
  const [countdown, setCountdown] = useState(4);

   useEffect(() => {
    if (countdown === 0) {
      router.push("/home"); 
    }
  }, [countdown, router]);

  useEffect(() => {
    const interval = setInterval(() => {
      setCountdown((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="flex h-[60vh] items-center justify-center">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-red-600">
          Access Denied
        </h1>
        <p className="mt-2 text-gray-600">
          You do not have permission to access this page.
        </p>

        <p className="mt-4 text-sm text-gray-500">
          Redirecting to home in <span className="font-semibold">{countdown}</span> seconds...
        </p>
      </div>
    </div>
  );
}
