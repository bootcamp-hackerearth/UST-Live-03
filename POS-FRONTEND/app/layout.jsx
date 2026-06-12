import PropTypes from "prop-types";

export const metadata = {
  title: "POS Application",
  description: "Enterprise POS System",
};

import "./globals.css";

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        {children}
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};