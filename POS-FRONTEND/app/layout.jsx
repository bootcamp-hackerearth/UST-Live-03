// app/layout.jsx

import "../app/globals.css";
import PropTypes from "prop-types";
import { LoadingProvider } from "../app/lib/loadingContext";
import AuthProvider from "../components/auth/AuthProvider";
import GlobalLoaderRenderer from "@/components/loader/GlobalLoaderRenderer";

export const metadata = {
  title: "POS Retail",
  description: "Point of Sale Retail Management",
  icons: {
    icon: "/icon.svg",
  },
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <LoadingProvider>
          <AuthProvider>
            <GlobalLoaderRenderer />
            {children}
          </AuthProvider>
        </LoadingProvider>
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired
};