"use client";

import PropTypes from "prop-types";

export default function ShippingAddressLabel({ onChange }) {
  return (
    <span
      style={{
        display: "flex",
        alignItems: "center",
        gap: 8,
      }}
    >
      <span>Shipping Address</span>

      <label
        style={{
          display: "flex",
          alignItems: "center",
          gap: 4,
          fontSize: 12,
          fontWeight: 400,
          color: "#6b7280",
          cursor: "pointer",
        }}
      >
        <input
          type="checkbox"
          onChange={onChange}
          style={{
            width: 13,
            height: 13,
            accentColor: "#2563eb",
          }}
        />
        <span>Same as Billing</span>
      </label>
    </span>
  );
}

ShippingAddressLabel.propTypes = {
  onChange: PropTypes.func.isRequired,
};