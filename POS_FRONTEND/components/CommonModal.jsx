"use client";

import { useEffect } from "react";
import PropTypes from "prop-types";
import { X } from "lucide-react";

export default function CommonModal({
  isOpen,
  onClose,
  title,
  children,
  width = "max-w-md"
}) {

  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (event) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    globalThis.addEventListener("keydown", handleKeyDown);

    return () => {
      globalThis.removeEventListener("keydown", handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-sm">
      <button
        type="button"
        aria-label="Close modal"
        className="absolute inset-0 h-full w-full"
        onClick={onClose}
      />

      <div className={`relative z-10 w-full rounded-xl border border-gray-200 bg-white shadow-xl ${width}`}>
        <div className="flex items-center justify-between border-b p-4">
          <h2 className="text-sm font-bold">{title}</h2>

          <button
            onClick={onClose}
            className="p-1 text-gray-400 transition hover:text-black"
            type="button"
          >
            <X size={18} />
          </button>
        </div>

        <div className="p-5">{children}</div>
      </div>
    </div>
  );
}

CommonModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  title: PropTypes.node,
  children: PropTypes.node,
  width: PropTypes.string
};