import PropTypes from "prop-types";

export default function Alert({
  type = "error",
  message,
}) {

  if (!message) {
    return null;
  }

  const styles = {

    error:
      "border-red-200 bg-red-50 text-red-600",

    success:
      "border-green-200 bg-green-50 text-green-600",

    warning:
      "border-yellow-200 bg-yellow-50 text-yellow-700",
  };

  return (

    <div
      className={`mb-6 border px-5 py-4 rounded-xl ${styles[type]}`}
    >

      {message}
    </div>
  );
}

Alert.propTypes = {
  type: PropTypes.oneOf(["error", "success", "warning"]),
  message: PropTypes.string,
};