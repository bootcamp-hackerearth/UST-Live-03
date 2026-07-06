"use client";
import PropTypes from "prop-types";

const STATUS_META = {
  403: "Unauthorized",
  404: "Not Found",
  500: "Server Error",
};

export default function ErrorModal({ status, message, onClose }) {
  if (!message) return null;
  const title = STATUS_META[status] || "Something Went Wrong";

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 px-4">
      <div className="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl">
        <h3 className="text-lg font-bold text-rose-600">{title}</h3>
        <p className="mt-2 text-sm text-slate-600">{message}</p>
        <div className="mt-6 flex justify-end">
          <button
            type="button"
            onClick={onClose}
            className="inline-flex items-center justify-center rounded-xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-700"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

ErrorModal.propTypes = {
  status: PropTypes.number,
  message: PropTypes.string,
  onClose: PropTypes.func.isRequired,
};

ErrorModal.defaultProps = {
  status: null,
  message: "",
};
