"use client";

import PropTypes from "prop-types";
import React, { useState } from "react";
import SingleDropdown from "./SingleDropdown";
import { useRouter } from "next/navigation";

function Add({
  title,
  apiPath,
  extraFields = [],
  extraData = {},
  showDescription = true,
  identifierDropdownApi = null,
  hideIdentifier = false,
  onClose,
  onSuccess,
}) {
  const router = useRouter();
  const [identifier, setIdentifier] = useState("");
  const [description, setDescription] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [customFormData, setCustomFormData] = useState({});

  const handleInputChange = (key, value) => {
    setCustomFormData((prev) => ({ ...prev, [key]: value }));
  };

  const isMissing = (field, value) => {
    if (!field.required) return false;
    if (Array.isArray(value)) return value.length === 0;
    return value === undefined || value === null || String(value).trim() === "";
  };

  const validateField = (field, value) => {
    if (isMissing(field, value))
      return `${field.label || field.key} is required.`;

    if (field.key === "phoneNo" && value) {
      if (!/^\d{10}$/.test(String(value).trim()))
        return "Phone number must be exactly 10 digits.";
    }

    if (field.key === "password" && value) {
      const password = String(value);
      const passwordPolicy =
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+\-=])[A-Za-z\d@$!%*?&#^()_+\-=]{8,}$/;
      if (!passwordPolicy.test(password)) {
        return "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.";
      }
      if (/\s/.test(password)) {
        return "Password must not contain spaces.";
      }
    }

    if (field.key === "username" && value) {
      if (!String(value).trim().endsWith("@gmail.com"))
        return "Username must be a valid Gmail address (e.g. example@gmail.com).";
    }
    return "";
  };

  const validate = (rawDomData) => {
    if (!identifierDropdownApi && !hideIdentifier && !identifier.trim())
      return "Identifier is required.";

    for (const field of extraFields) {
      const value =
        field.type === "custom"
          ? customFormData[field.key]
          : rawDomData[field.key];
      const err = validateField(field, value);
      if (err) return err;
    }

    return "";
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    const baseFormData = {
      ...(hideIdentifier ? {} : { identifier }),
      ...(showDescription ? { description } : {}),
      ...extraData,
    };

    const rawDomData = {};
    extraFields.forEach((field) => {
      if (field.type !== "custom") {
        rawDomData[field.key] = document.getElementById(field.key)?.value;
      }
    });

    const validationError = validate(rawDomData);
    if (validationError) {
      setError(validationError);
      return;
    }

    const finalPayload = { ...baseFormData, ...rawDomData, ...customFormData };

    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`http://localhost:8080/api/${apiPath}/add`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(finalPayload),
      });

      if (response.ok) {
        const data = await response.json();
        const didAddSuccessfully =
          data.success === true || data.success == null;
        if (didAddSuccessfully) {
          setSuccess(`${title} added successfully`);
          setTimeout(() => {
            if (onSuccess) onSuccess();
            else router.back();
          }, 1500);
        } else {
          setError(data.message || "Failed to add");
        }
      } else {
        const errorText = await response.text();
        throw new Error(
          errorText || `Server returned status code ${response.status}`,
        );
      }
    } catch (err) {
      console.error("Submission Error Details:", err);
      setError(err.message || "Unable to connect to server.");
    }
  };

  const handleCancel = () => {
    if (onClose) onClose();
    else router.back();
  };

  const inputStyle = {
    width: "100%",
    border: "1px solid #d1d5db",
    borderRadius: "6px",
    padding: "10px 14px",
    fontSize: "14px",
    outline: "none",
    boxSizing: "border-box",
  };

  const renderField = (field) => {
    if (field.type === "custom") {
      if (typeof field.component === "function") {
        return field.component(customFormData, (val) =>
          handleInputChange(field.key, val),
        );
      }
      return field.component;
    }

    return (
      <input
        id={field.key}
        type={field.type || "text"}
        placeholder={`Enter ${field.label}`}
        style={inputStyle}
        required={field.required}
        readOnly={field.readOnly}
      />
    );
  };

  return (
    <div
      style={{
        padding: "20px",
        fontFamily: "Arial",
        backgroundColor: "#f5f5f5",
        minHeight: "calc(100vh - 70px)",
        boxSizing: "border-box",
      }}
    >
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "20px",
        }}
      >
        <div>
          <h1 style={{ color: "#111827", margin: 0 }}>Add {title}</h1>
          <p
            style={{ color: "#6b7280", margin: "4px 0 0 0", fontSize: "14px" }}
          >
            Fill all details carefully
          </p>
        </div>
      </div>

      <div
        style={{
          backgroundColor: "white",
          borderRadius: "8px",
          boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
          padding: "24px",
          maxWidth: "600px",
        }}
      >
        {error && (
          <div
            style={{
              backgroundColor: "#fee2e2",
              color: "#dc2626",
              padding: "10px",
              borderRadius: "6px",
              marginBottom: "16px",
              fontSize: "14px",
              wordBreak: "break-word",
            }}
          >
            <strong>Error:</strong> {error}
          </div>
        )}
        {success && (
          <div
            style={{
              backgroundColor: "#dcfce7",
              color: "#16a34a",
              padding: "10px",
              borderRadius: "6px",
              marginBottom: "16px",
              fontSize: "14px",
            }}
          >
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          {!hideIdentifier && (
            <div style={{ marginBottom: "16px" }}>
              {identifierDropdownApi ? (
                <SingleDropdown
                  value={identifier}
                  label="Identifier"
                  apiPath={identifierDropdownApi}
                  required
                  onChange={(value) => setIdentifier(value)}
                />
              ) : (
                <>
                  <label
                    htmlFor="identifier"
                    style={{
                      display: "block",
                      fontWeight: "bold",
                      marginBottom: "6px",
                      fontSize: "14px",
                    }}
                  >
                    Identifier
                  </label>
                  <input
                    id="identifier"
                    type="text"
                    placeholder="Enter identifier"
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                    style={inputStyle}
                    required
                  />
                </>
              )}
            </div>
          )}

          {showDescription && (
            <div style={{ marginBottom: "16px" }}>
              <label
                htmlFor="description"
                style={{
                  display: "block",
                  fontWeight: "bold",
                  marginBottom: "6px",
                  fontSize: "14px",
                }}
              >
                Description
              </label>
              <textarea
                id="description"
                placeholder="Enter description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                style={{ ...inputStyle, resize: "vertical", minHeight: "90px" }}
              />
            </div>
          )}

          {extraFields.map((field) => (
            <div key={field.key} style={{ marginBottom: "16px" }}>
              {field.type !== "custom" && (
                <label
                  htmlFor={field.key}
                  style={{
                    display: "block",
                    fontWeight: "bold",
                    marginBottom: "6px",
                    fontSize: "14px",
                  }}
                >
                  {field.label}
                </label>
              )}
              {renderField(field)}
            </div>
          ))}

          <div style={{ display: "flex", gap: "12px", marginTop: "24px" }}>
            <button
              type="button"
              onClick={handleCancel}
              style={{
                flex: 1,
                padding: "10px",
                backgroundColor: "#e5e7eb",
                color: "#111827",
                border: "none",
                borderRadius: "6px",
                cursor: "pointer",
                fontWeight: "bold",
                fontSize: "14px",
              }}
            >
              Cancel
            </button>
            <button
              type="submit"
              style={{
                flex: 1,
                padding: "10px",
                backgroundColor: "black",
                color: "white",
                border: "none",
                borderRadius: "6px",
                cursor: "pointer",
                fontWeight: "bold",
                fontSize: "14px",
              }}
            >
              Add {title}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

Add.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  extraFields: PropTypes.arrayOf(PropTypes.object),
  extraData: PropTypes.object,
  showDescription: PropTypes.bool,
  identifierDropdownApi: PropTypes.string,
  hideIdentifier: PropTypes.bool,
  onClose: PropTypes.func,
  onSuccess: PropTypes.func,
};

export default Add;
