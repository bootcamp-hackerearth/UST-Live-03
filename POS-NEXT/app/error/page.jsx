import Link from "next/link";
import PropTypes from "prop-types";
import { AlertTriangle, ArrowLeft } from "lucide-react";
import { Poppins, Orbitron } from "next/font/google";

const poppins = Poppins({
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
});

const orbitron = Orbitron({
  subsets: ["latin"],
  weight: ["600", "700", "800"],
});

export default async function ErrorPage({ searchParams }) {
  const params = await searchParams;

  const status = params.status || "404";
  const message = params.message || "Something went wrong";
  const returnTo = params.returnTo || "/";

  const titles = {
    "403": "Access Denied",
    "404": "Page Not Found",
  };

  const title = titles[status] || "Something Went Wrong";

  return (
    <div
      className={`${poppins.className} flex justify-center pt-20 px-6 min-h-[calc(100vh-72px)]`}
    >
      <div className="w-full max-w-3xl translate-y-16 rounded-3xl border border-violet-100 bg-white shadow-xl p-12 text-center">        <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-violet-100">
        <AlertTriangle className="h-10 w-10 text-violet-600" />
      </div>

        <h1
          className={`${orbitron.className} mt-8 text-6xl font-extrabold text-violet-600`}
        >
          {status}
        </h1>

        <h2 className="mt-4 text-4xl font-bold text-gray-800">
          {title}
        </h2>

        <p className="mt-4 text-lg text-gray-500">
          {message}
        </p>

        <div className="my-10 h-px w-full bg-violet-200" />

        <div className="mt-8 flex justify-center">
          <Link
            href={returnTo}
            className="flex items-center gap-2 rounded-lg bg-violet-600 px-6 py-3 text-white font-medium hover:bg-violet-700 transition"
          >
            <ArrowLeft size={18} />
            Back to Previous Page
          </Link>
        </div>
      </div>
    </div>
  );
}

ErrorPage.propTypes = {
  searchParams: PropTypes.object,
};