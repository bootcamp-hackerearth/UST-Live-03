import PropTypes from "prop-types";

export default function AuthInput({
  label,
  type = "text",
  value,
  onChange,
  placeholder,
  disabled = false,
  error = "",
}) {

  return (

    <div>
      <label
        htmlFor={`auth-input-${label}`}
        className="block text-lg font-medium text-gray-700 mb-3"
      >

        {label}

      </label>

      <input
        id={`auth-input-${label}`}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        disabled={disabled}
        className={`

          w-full
          border
          rounded-xl
          px-5
          py-5
          text-lg
          outline-none
          transition-all
          bg-white

          ${error
            ? "border-red-500"
            : "border-gray-300 focus:border-blue-600"
          }

          ${disabled
            ? "bg-gray-100 cursor-not-allowed opacity-70"
            : ""
          }

        `}
      />
      
      {
        error && (
          <p className="text-red-500 text-sm mt-2">

            {error}

          </p>
        )
      }
    </div>
  );
}

AuthInput.propTypes = {
  label: PropTypes.string.isRequired,
  type: PropTypes.string,
  value: PropTypes.string,
  onChange: PropTypes.func,
  placeholder: PropTypes.string,
  disabled: PropTypes.bool,
  error: PropTypes.string,
};