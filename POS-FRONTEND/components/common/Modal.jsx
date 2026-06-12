'use client';

import React from "react";
import PropTypes from "prop-types";

const Modal = ({ formData, handleChange, fields, errors }) => {
  console.log("fields", fields);

  return (
    <div className="space-y-4 max-h-[70vh] overflow-y-auto pr-2">
      {fields.map((field) => (
        <div key={field.name}>
          <label className="block mb-1 font-medium">
            {field.label}
          </label>

          {field.component ? (
            field.component({
              value: formData[field.name],
              onChange: (value) =>
                handleChange({
                  target: { name: field.name, value },
                }),
            })
          ) : (
            <input
              type={field.type || "text"}
              name={field.name}
              value={formData[field.name] || ""}
              onChange={handleChange}
              readOnly={field.name === "identifier"}
              className={`w-full border rounded-lg px-3 py-2 ${
                field.name === "identifier"
                  ? "bg-gray-100 cursor-not-allowed"
                  : ""
              }`}
            />
          )}
          
          {errors?.[field.name] && (
            <p className="text-red-500 text-sm mt-1">
              {errors[field.name]}
            </p>
          )}
          </div>
      ))}
    </div>
  );
};

Modal.propTypes = {
  formData: PropTypes.object.isRequired,
  handleChange: PropTypes.func.isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      component: PropTypes.func,
    })
  ).isRequired,
  errors: PropTypes.object,
};

export default Modal;