import PropTypes from "prop-types";

export default function Toast({
  message,
}) {

  if (!message) {
    return null;
  }

  return (
    <div className="fixed top-6 right-6 z-50 bg-[#111827] text-white px-6 py-4 rounded-2xl shadow-2xl">

      {message}

    </div>
  );
}

Toast.propTypes = {
  message: PropTypes.string,
};