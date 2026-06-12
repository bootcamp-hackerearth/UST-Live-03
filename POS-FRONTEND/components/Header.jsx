// components/Header.jsx

"use client";

import { useState, useEffect, useRef } from "react";
import PropTypes from "prop-types";
import { User } from "lucide-react";
import ProfileDropdownCard from "./ProfileDropdownCard";

export default function Header({ name }) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target)
      ) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () =>
      document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <header className="h-20 bg-white border-b border-[#231F20]/10 flex items-center justify-between px-10 shadow-sm relative z-50">
      
      <div>
        <h1 className="text-xl font-bold text-[#231F20] tracking-tight">
          POS Dashboard
        </h1>
        <p className="text-xs text-[#006E74] font-semibold uppercase tracking-wider mt-0.5">
          Retail Management Console
        </p>
      </div>

      <div className="flex items-center gap-5" ref={dropdownRef}>
        <div className="text-right hidden sm:block">
          <p className="text-[10px] text-[#231F20]/50 font-semibold uppercase tracking-wider">
            Logged In As
          </p>
          <p className="text-base font-bold text-[#231F20] mt-0.5">
            {name}
          </p>
        </div>

        <button
          type="button"
          onClick={() => setIsOpen((prev) => !prev)}
          title="User Menu"
          className={`w-11 h-11 rounded-xl flex items-center justify-center border transition-all duration-150 ${
            isOpen
              ? "bg-[#006E74] border-[#006E74] text-white shadow-md"
              : "bg-slate-50 border-[#231F20]/15 text-[#231F20]/80 hover:bg-slate-100 hover:text-[#231F20]"
          }`}
        >
          <User size={22} />
        </button>

        {isOpen && (
          <div className="absolute right-10 top-16">
            <ProfileDropdownCard onClose={() => setIsOpen(false)} />
          </div>
        )}
      </div>

    </header>
  );
}

Header.propTypes = {
  name: PropTypes.string.isRequired,
};