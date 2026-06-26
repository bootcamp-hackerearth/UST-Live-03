"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function NotFoundPage() {
  const router = useRouter();
  const [countdown, setCountdown] = useState(4);

  useEffect(() => {
    const interval = setInterval(() => {
      setCountdown((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (countdown === 0) {
      router.push("/home");
    }
  }, [countdown, router]);

  return (
    <div className="flex h-[60vh] items-center justify-center">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-orange-600">
          404 - Not Found
        </h1>
        <p className="mt-2 text-gray-600">
          The requested resource was not found.
        </p>

        <p className="mt-4 text-sm text-gray-500">
          Redirecting to home in <b>{countdown}</b> seconds...
        </p>
      </div>
    </div>
  );
}