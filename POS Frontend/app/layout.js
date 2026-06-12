import PropTypes from "prop-types";
import "./globals.css";
import DashboardLayout from "../components/DashboardLayout";

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <DashboardLayout>{children}</DashboardLayout>
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};
