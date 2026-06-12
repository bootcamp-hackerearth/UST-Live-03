import "./globals.css";
import PropTypes from "prop-types";

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}

/* ✅ FIX */
RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};