"use client";

import { useState, useEffect } from "react";

const Footer = () => {
  const [time, setTime] = useState(null);

  useEffect(() => {
    const timer = setInterval(() => {
      setTime(new Date().toLocaleTimeString());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  return (
    <footer className="bg-white border-t border-slate-200 px-8 py-3.5 flex justify-between items-center text-xs text-slate-500 flex-shrink-0">
      
      <div className="font-normal">
        © 2026 RetailPOS • <span className="text-slate-400">Version 1.0.0</span>
      </div>

      <div className="flex items-center gap-6">
        
        <div className="flex gap-4">
          {["Help", "Support", "Privacy"].map((item) => (
            <span
              key={item}
              className="cursor-pointer transition-colors duration-150 hover:text-blue-600"
            >
              {item}
            </span>
          ))}
        </div>

        <span className="w-px h-3 bg-slate-300"></span>

        <span className="text-slate-600 font-mono font-medium">
          {time}
        </span>

      </div>
    </footer>
  );
};

export default Footer;