import PropTypes from "prop-types";

export default function ErrorModal({ isOpen, message, code, onClose }) {
  if (!isOpen) return null;

  let icon = "⚠️";

  if (code === 403) {
    icon = "🔒";
  } else if (code === 404) {
    icon = "🔍";
  }

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/50 p-4">
      <div className="bg-white rounded-2xl p-8 max-w-sm w-full shadow-2xl text-center">
        <div className="text-4xl mb-4">{icon}</div>
        <h2 className="text-xl font-bold mb-2">Error {code}</h2>
        <p className="text-gray-600 mb-6">{message}</p>
        <button
          onClick={onClose}
          className="w-full bg-blue-600 text-white py-2 rounded-lg"
        >
          Dismiss
        </button>
      </div>
    </div>
  );
}

ErrorModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  message: PropTypes.string,
  code: PropTypes.number,
  onClose: PropTypes.func.isRequired,
};