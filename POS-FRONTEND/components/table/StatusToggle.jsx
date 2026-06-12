"use client";

import PropTypes from "prop-types";

export default function StatusToggle({

  checked = false,
  onChange,

}) {

  return (

    <button
      type="button"
      onClick={onChange}
      className={`relative inline-flex h-7 w-14 items-center rounded-full transition-all duration-300 ease-in-out

      ${
        checked
          ? "bg-green-500"
          : "bg-gray-300"
      }`}
    >

      <span
        className={`inline-block h-5 w-5 transform rounded-full bg-white shadow-md transition-all duration-300 ease-in-out
        ${
          checked
            ? "translate-x-8"
            : "translate-x-1"
        }`}
      />
    </button>
  );
}

StatusToggle.propTypes = {
  checked: PropTypes.bool,
  onChange: PropTypes.func,
};