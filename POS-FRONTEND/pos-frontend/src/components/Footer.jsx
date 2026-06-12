import React from "react";

function Footer() {

  return (
    <footer
      className="mt-auto border-t border-gray-200 bg-white px-6 py-3
      flex items-center justify-between text-xs text-gray-400"
    >

      <span>
        © {new Date().getFullYear()} UST GLOBAL
      </span>

      <span>
        Pos Application
      </span>

    </footer>
  );
}

export default Footer;