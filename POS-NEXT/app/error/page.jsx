"use client";

import { useSearchParams } from "next/navigation";
import Link from "next/link";

export default function ErrorPage() {
  const params = useSearchParams();

  const status = params.get("status");
  const message = params.get("message");
  const returnTo = params.get("returnTo") || "/";

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50 px-4">

      <h1 className="text-7xl font-bold text-violet-600">
        {status}
      </h1>

      <h2 className="mt-4 text-2xl font-semibold text-gray-700">
        {status === "403"
          ? "Access Denied"
          : "Resource Not Found"}
      </h2>

      <p className="mt-3 text-gray-500 text-center">
        {message || "Something went wrong"}
      </p>

      <Link
  href={returnTo}
  className="mt-8 px-6 py-3 bg-violet-600 text-white rounded-lg"
>
  Back to List
</Link>

    </div>
  );
}