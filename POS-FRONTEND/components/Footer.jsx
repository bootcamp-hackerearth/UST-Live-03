// components/Footer.jsx

"use client";

export default function Footer() {
  return (
    <footer className="h-12 bg-white border-t border-[#231F20]/10 flex items-center justify-between px-8 text-[11px] text-[#231F20]/60 select-none flex-shrink-0">
      
      <div>
        © {new Date().getFullYear()}{" "}
        <span className="font-semibold text-[#231F20]">
          UST Global
        </span>{" "}
        • All rights reserved
      </div>

      <div className="text-[10px] text-[#231F20]/40">
        v3.1.0 • Credits JAVA POD-1
      </div>
      
    </footer>
  );
}