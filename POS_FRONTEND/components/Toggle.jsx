"use client";

import PropTypes from "prop-types";

export default function Toggle({ active, onToggle }) {
  return (
    <button
      type="button"
      onClick={onToggle}
      aria-pressed={active}
      aria-label={active ? "Deactivate toggle" : "Activate toggle"}
      style={{
        width: "48px",
        height: "24px",
        background: active ? "green" : "gray",
        borderRadius: "20px",
        display: "flex",
        alignItems: "center",
        padding: "2px",
        cursor: "pointer",
        border: "none",
        justifyContent: active ? "flex-end" : "flex-start",
      }}
    >
      <div
        style={{
          width: "20px",
          height: "20px",
          background: "white",
          borderRadius: "50%",
        }}
      />
    </button>
  );
}

Toggle.propTypes = {
  active: PropTypes.bool.isRequired,
  onToggle: PropTypes.func.isRequired,
};
