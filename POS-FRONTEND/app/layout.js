"use client"
import "./globals.css";
import PropTypes from "prop-types";
import Navbar from "@/components/Navbar";
import SideBar from "@/components/SideBar";
import { usePathname} from "next/navigation";

export default function RootLayout({ children }) {

  const path = usePathname()
  const flag = !["/login", "/register"].includes(path)
  
  return (
    <html lang="en">
      <body className="min-h-full flex flex-col">
        {flag ? (<><Navbar /> <SideBar /></>) : ""}
        <div className={flag ? "pl-12 pt-16" : ""}>
          {children}
        </div>
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node,
};
