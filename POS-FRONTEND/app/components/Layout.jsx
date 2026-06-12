"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { usePathname, useRouter } from "next/navigation";
import Sidebar from "./Sidebar";
import Header from "./Header";
import Footer from "./Footer";

const noLayoutPaths = new Set(["/login", "/register"]);

function RootLayout({ children }) {
  const pathname = usePathname();
  const router = useRouter();
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (noLayoutPaths.has(pathname)) {
      setIsReady(true);
      return;
    }

    if (!token) {
      router.replace("/login");
      return;
    }

    setIsReady(true);
  }, [pathname, router]);

  if (!isReady) {
    return null;
  }

  if (noLayoutPaths.has(pathname)) {
    return <>{children}</>;
  }

  return (
    <div className="flex min-h-screen bg-[#f6f8fb] text-slate-900">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <Header />
        <main className="flex flex-1 items-start justify-center px-5 py-6 sm:px-8 lg:px-10">
          {children}
        </main>
        <Footer />
      </div>
    </div>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default RootLayout;