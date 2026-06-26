'use client';

import React from "react";
import PropTypes from "prop-types";

const Modal = ({
  formData,
  handleChange,
  fields,
  errors,
}) => {

  const formatDate = (value) => {
    if (!value) return "";

    return new Date(value)
      .toLocaleString("en-IN");
  };

  return (
    <div className="space-y-4 max-h-[70vh] overflow-y-auto pr-2">

      {fields.map((field) => (
        <div key={field.name}>

          <label htmlFor={field.name} className="block mb-1 font-medium">
            {field.label}
          </label>

          {field.component ? (

            field.component({
              id: field.name,
              value: formData[field.name],

              onChange: (value) =>
                handleChange({
                  target: {
                    name: field.name,
                    value,
                  },
                }),
            })

          ) : (

            <input
              id={field.name}
              type={field.type || "text"}
              name={field.name}
              value={
                formData[field.name] || ""
              }
              onChange={handleChange}
              readOnly={
                field.readOnly ||
                field.name === "identifier"
              }

              className={`
                w-full
                border
                rounded-lg
                px-3
                py-2
                ${
                  field.readOnly ||
                  field.name === "identifier"
                    ? "bg-gray-100 cursor-not-allowed"
                    : ""
                }
              `}
            />

          )}

          {errors?.[field.name] && (
            <p className="text-red-500 text-sm mt-1">
              {errors[field.name]}
            </p>
          )}

        </div>
      ))}
      <div className="border-t pt-5">

        <h3 className="font-semibold mb-4">
          Audit Details
        </h3>

        <div className="space-y-3">

          <div>
            <label htmlFor="createdBy" className="block mb-1">
              Created By
            </label>

            <input
              id="createdBy"
              value={
                formData.createdBy || ""
              }
              readOnly
              className="
                w-full
                border
                rounded-lg
                px-3
                py-2
                bg-gray-100
              "
            />
          </div>

          <div>
            <label htmlFor="createdOn" className="block mb-1">
              Created On
            </label>

            <input
              id="createdOn"
              value={
                formatDate(
                  formData.createdOn
                )
              }
              readOnly
              className="
                w-full
                border
                rounded-lg
                px-3
                py-2
                bg-gray-100
              "
            />
          </div>

          <div>
            <label htmlFor="modifiedBy" className="block mb-1">
              Modified By
            </label>

            <input
              id="modifiedBy"
              value={
                formData.modifiedBy || ""
              }
              readOnly
              className="
                w-full
                border
                rounded-lg
                px-3
                py-2
                bg-gray-100
              "
            />
          </div>

          <div>
            <label htmlFor="modifiedOn" className="block mb-1">
              Modified On
            </label>

            <input
              id="modifiedOn"
              value={
                formatDate(
                  formData.modifiedOn
                )
              }
              readOnly
              className="
                w-full
                border
                rounded-lg
                px-3
                py-2
                bg-gray-100
              "
            />
          </div>
        </div>
      </div>
    </div>
  );
};

Modal.propTypes = {
  formData: PropTypes.object.isRequired,
  handleChange: PropTypes.func.isRequired,
  fields: PropTypes.array.isRequired,
  errors: PropTypes.object,
};

export default Modal;