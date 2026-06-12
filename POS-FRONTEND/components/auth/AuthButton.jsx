import PropTypes from "prop-types";

export default function AuthButton({
  text,
  loadingText = "Loading...",
  loading = false,
  disabled = false,
  type = "submit",
}) {

  return (

    <button
      type={type}
      disabled={loading || disabled}
      className="

        w-full
        bg-[#0066ff]
        hover:bg-[#0052cc]
        disabled:bg-blue-300
        disabled:cursor-not-allowed
        disabled:opacity-70

        text-white
        text-xl
        font-semibold

        py-5
        rounded-xl

        transition-all
        duration-200

      "
    >
      { loading? loadingText : text}

    </button>

  );

}

AuthButton.propTypes = {
  text: PropTypes.string.isRequired,
  loadingText: PropTypes.string,
  loading: PropTypes.bool,
  disabled: PropTypes.bool,
  type: PropTypes.string,
};