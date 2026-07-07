"use client";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

const DASHBOARD_ROUTE = "/dashboard";

const ERROR_CONFIG = {
  404: {
    title: "Not Found",
    accent: "#f59e0b",
    background: "#fffbeb",
    border: "#fde68a",
  },
  403: {
    title: "Access Denied",
    accent: "#dc2626",
    background: "#fef2f2",
    border: "#fecaca",
  },
  500: {
    title: "Something Went Wrong",
    accent: "#dc2626",
    background: "#fef2f2",
    border: "#fecaca",
  },
};

const DEFAULT_CONFIG = {
  title: "Error",
  accent: "#dc2626",
  background: "#fef2f2",
  border: "#fecaca",
};

function ErrorIcon({ status, color }) {
  if (status === 403) {
    return (
      <svg
        width="28"
        height="28"
        viewBox="0 0 24 24"
        fill="none"
        stroke={color}
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <rect x="5" y="11" width="14" height="9" rx="2" />
        <path d="M8 11V7a4 4 0 0 1 8 0v4" />
      </svg>
    );
  }
  if (status === 404) {
    return (
      <svg
        width="28"
        height="28"
        viewBox="0 0 24 24"
        fill="none"
        stroke={color}
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <circle cx="11" cy="11" r="7" />
        <line x1="21" y1="21" x2="16.65" y2="16.65" />
        <line x1="8.5" y1="11" x2="13.5" y2="11" />
      </svg>
    );
  }
  return (
    <svg
      width="28"
      height="28"
      viewBox="0 0 24 24"
      fill="none"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0Z" />
      <line x1="12" y1="9" x2="12" y2="13" />
      <line x1="12" y1="17" x2="12.01" y2="17" />
    </svg>
  );
}

ErrorIcon.propTypes = {
  status: PropTypes.number,
  color: PropTypes.string.isRequired,
};

export default function ErrorModal({ open, status, message, onClose }) {
  const router = useRouter();

  if (!open) return null;

  const config = ERROR_CONFIG[status] || DEFAULT_CONFIG;

  const handleClose = () => {
    if (onClose) {
      onClose();
    }
    router.push(DASHBOARD_ROUTE);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
      <div className="w-full max-w-md rounded-[28px] bg-white px-8 py-8 shadow-2xl">
        <div
          className="mx-auto flex h-14 w-14 items-center justify-center rounded-full"
          style={{
            backgroundColor: config.background,
            border: `1px solid ${config.border}`,
          }}
        >
          <ErrorIcon status={status} color={config.accent} />
        </div>

        <div className="mt-5 text-center">
          <h2 className="text-lg font-semibold text-[#101828]">
            {config.title}
            {status ? ` (${status})` : ""}
          </h2>
          <p className="mt-2 text-sm text-[#667085]">{message}</p>
        </div>

        <div className="mt-7 flex justify-center">
          <button
            type="button"
            onClick={handleClose}
            className="h-12 rounded-2xl bg-[#2563eb] px-8 text-[15px] font-medium text-white shadow-lg shadow-blue-500/20 transition-all hover:bg-[#1d4ed8]"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

ErrorModal.propTypes = {
  open: PropTypes.bool,
  status: PropTypes.number,
  message: PropTypes.string,
  onClose: PropTypes.func,
};
