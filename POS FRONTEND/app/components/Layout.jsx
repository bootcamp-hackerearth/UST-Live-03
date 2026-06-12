"use client";
import PropTypes from "prop-types";
import { usePathname } from "next/navigation";
import Sidebar from "./Sidebar";
import Header from "./Header";
import Footer from "./Footer";

const noLayoutPaths = new Set(["/Login", "/Register"]);

function Layout({ children }) {
  const pathname = usePathname();

  if (noLayoutPaths.has(pathname)) {
    return <>{children}</>;
  }

  return (
    <div className="flex min-h-screen bg-[linear-gradient(180deg,#f8fafc_0%,#f6f0e8_100%)] text-slate-900">
      <Sidebar />

      <div className="flex flex-1 flex-col">
        <Header />

        <main className="flex flex-1 items-start justify-center px-4 py-6 sm:px-6 lg:px-8">
          {children}
        </main>

        <Footer />
      </div>
    </div>
  );
}

Layout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default Layout;