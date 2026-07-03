import Link from "next/link";

export default async function ErrorPage({ searchParams }) {
  const params = await searchParams;

  const status = params.status || "404";
  const message = params.message || "Something went wrong";
  const returnTo = params.returnTo || "/";

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
        {message}
      </p>

      <Link
        href={returnTo}
        className="mt-8 px-6 py-3 bg-violet-600 text-white rounded-lg hover:bg-violet-700 transition-colors"
      >
        Back to List
      </Link>
    </div>
  );
}
