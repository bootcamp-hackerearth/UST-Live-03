"use client";

import React from "react";
import PropTypes from "prop-types";
import { inputBaseStyle, focusHandler, blurHandler } from "../auth/authstyle";

export default function AuthInputField({
  id,
  name,
  type,
  placeholder,
  value,
  onChange,
  icon,
  rightElement,
  required = true,
}) {
  return (
    <div style={{ position: "relative" }}>
      {icon}
      <input
        id={id}
        name={name}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        required={required}
        style={{
          ...inputBaseStyle,
          paddingLeft: icon ? "38px" : "12px",
          paddingRight: rightElement ? "40px" : "12px",
        }}
        onFocus={focusHandler}
        onBlur={blurHandler}
      />
      {rightElement}
    </div>
  );
}

AuthInputField.propTypes = {
  id: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  type: PropTypes.string,
  placeholder: PropTypes.string,
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  icon: PropTypes.node,
  rightElement: PropTypes.node,
  required: PropTypes.bool,
};