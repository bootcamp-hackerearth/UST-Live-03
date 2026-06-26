"use client";

import { useState } from "react";
import Select from "react-select";
import "./CommonList.css";
import reactSelectStyles from "@/app/components/reactSelectStyles";

const FormModal = ({
  mode,
  title,
  fields = [],
  item = {},
  setItem,
  handleSubmit,
  closeModal
}) => {

  const [modalError, setModalError] = useState("");
  const [submitted, setSubmitted] = useState(false);

  const billingFields = fields.filter(
    f => f.name.startsWith("billing.")
  );

  const shippingFields = fields.filter(
    f => f.name.startsWith("shipping.")
  );

  const normalFields = fields.filter(
    f =>
      !f.name.startsWith("billing.") &&
      !f.name.startsWith("shipping.")
  );

  const updateField = (name, value) => {
    setItem({
      ...item,
      [name]: value
    });
  };

  const getFieldError = (field) => {
    const value = item?.[field.name];

    if (
      field.required !== false &&
      (
        value === undefined ||
        value === null ||
        value === "" ||
        (Array.isArray(value) && value.length === 0)
      )
    ) {
      return `${field.label} is required`;
    }

    if (
      value &&
      field.pattern &&
      !field.pattern.test(value)
    ) {
      return field.errorMessage;
    }

    return "";
  };

  const isFieldInvalid = (field) => {
    return !!getFieldError(field);
  };

  const renderFieldError = (field) => {
    if (!submitted || !isFieldInvalid(field)) {
      return null;
    }

    return (
      <small style={{ color: "red" }}>
        {getFieldError(field)}
      </small>
    );
  };

  const renderField = (field) => {
    if (mode === "view") {

      let displayValue = item?.[field.name];

      if (field.type === "select") {

        if (field.multiple) {
          displayValue = (field.options || [])
            .filter(option =>
              (item?.[field.name] || [])
                .includes(option.value)
            )
            .map(option => option.label)
            .join(", ");
        } else {
          displayValue =
            (field.options || []).find(
              option =>
                option.value === item?.[field.name]
            )?.label || item?.[field.name];
        }
      }

      return (
        <div
          key={field.name}
          className="viewField"
        >
          <strong>{field.label}</strong>

          <p>{displayValue || "-"}</p>
        </div>
      );
    }

    if (field.type === "select") {
      return (
        <>
          <Select
            styles={reactSelectStyles}
            key={field.name}
            options={field.options || []}
            isSearchable
            isMulti={field.multiple}
            placeholder={field.label}
            menuPortalTarget={
              typeof globalThis === "undefined"
                ? null
                : globalThis.document?.body ?? null
            }
            menuPosition="fixed"
            value={
              field.multiple
                ? (field.options || []).filter(
                  option =>
                    (item?.[field.name] || [])
                      .includes(option.value)
                )
                : (field.options || []).find(
                  option =>
                    option.value === item?.[field.name]
                ) || null
            }
            onChange={(selected) => {
              const value = field.multiple
                ? selected?.map(x => x.value) ?? []
                : selected?.value ?? "";

              updateField(field.name, value);
            }}
          />

          {renderFieldError(field)}
        </>
      );
    }

    return (
      <>
        <input
          key={field.name}
          type={field.type || "text"}
          className="inputField"
          disabled={field.disabled}
          placeholder={field.label}
          value={item?.[field.name] || ""}
          onChange={(e) =>
            updateField(
              field.name,
              e.target.value
            )
          }
        />

        {renderFieldError(field)}
      </>
    );
  };

  const renderFields = (fieldList) =>
    fieldList.map((field) => (
      <div key={field.name}>
        {renderField(field)}
      </div>
    ));

  const submit = async () => {

    setSubmitted(true);
    setModalError("");

    const invalid = fields.some(
      field => getFieldError(field)
    );

    if (invalid) {
      return;
    }

    try {

      const response = await handleSubmit();

      if (response?.success === false) {
        setModalError(response.message);
        return;
      }

      closeModal();

    } catch (error) {

      setModalError(
        error.message || "Operation failed"
      );

    }
  };

  let modalAction = "Add";

  if (mode === "view") {
    modalAction = "View";
  } else if (mode === "edit") {
    modalAction = "Edit";
  }

  const formatDate = (date) =>
    date ? new Date(date).toLocaleString() : "-";

  return (
    <div className="modalOverlay">

      <div className="modalBox">

        <h2 className="modalTitle">
          {modalAction} {title}
        </h2>

        {modalError && (
          <div className="errorMessage">
            {modalError}
          </div>
        )}

        {renderFields(normalFields)}

        {billingFields.length > 0 && (
          <details className="address-section">

            <summary>
              Billing Address
            </summary>

            {renderFields(billingFields)}

          </details>
        )}


        {shippingFields.length > 0 && (
          <details className="address-section">

            <summary>
              Shipping Address
            </summary>

            {renderFields(shippingFields)}

          </details>
        )}

        {mode === "view" && (
          <div className="auditSection">

            <h3 className="auditTitle">
              Audit Information
            </h3>

            <p>
              <strong>Created By:</strong>{" "}
              {item?.createdBy || "-"}
            </p>

            <p>
              <strong>Created On:</strong>{" "}
              {formatDate(item?.createdOn)}
            </p>

            <p>
              <strong>Modified By:</strong>{" "}
              {item?.modifiedBy || "-"}
            </p>

            <p>
              <strong>Modified On:</strong>{" "}
              {formatDate(item?.modifiedOn)}
            </p>

          </div>
        )}

        <div className="modalActions">

          {mode !== "view" && (
            <button
              className="updateBtn"
              onClick={submit}
            >
              {mode === "edit" ? "Update" : "Add"}
            </button>
          )}

          <button
            className="cancelBtn"
            onClick={closeModal}
          >
            {mode === "view" ? "Close" : "Cancel"}
          </button>

        </div>

      </div>

    </div>
  );
};

FormModal.propTypes = {
  mode: require("prop-types").string.isRequired,
  title: require("prop-types").string.isRequired,
  fields: require("prop-types").array.isRequired,
  item: require("prop-types").object,
  setItem: require("prop-types").func.isRequired,
  handleSubmit: require("prop-types").func.isRequired,
  closeModal: require("prop-types").func.isRequired,
};

export default FormModal;