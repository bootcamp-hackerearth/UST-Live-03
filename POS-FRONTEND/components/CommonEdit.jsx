"use client";

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useForm } from "react-hook-form";
import {
  Squares2X2Icon,
  LinkIcon,
  ShieldCheckIcon,
} from "@heroicons/react/24/outline";
import { CommonAddFetch } from "@/fetch/CommonAddFetch";
import { CommonListFetch } from "@/fetch/CommonListFetch";

const CommonEdit = ({
  title,
  fields,
  apiRoute,
  dropdownApis,
  method,
  identifier,
  closeModal,
}) => {
  const { register, reset, handleSubmit } = useForm();
  const [dropdownData, setDropdownData] = useState({});
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [submitError, setSubmitError] = useState("");
  const [submitSuccess, setSubmitSuccess] = useState("");

  useEffect(() => {
    setSubmitError("");
    setSubmitSuccess("");
    
    if (method === "add") {
      setData({});
    } else if (method === "update" && identifier) {
      const fetchData = async () => {
        try {
          const response = await CommonAddFetch(
            `http://localhost:8080/api/${apiRoute}/get`,
            identifier,
            "text/plain"
          );

          if (response?.roles) {
            response.roles = response.roles.map((role) =>
              typeof role === "object" ? role.identifier : role
            );
          }

          if (response?.superCategory) {
            response.superCategory = response.superCategory.map((sc) =>
              typeof sc === "object" ? sc.identifier : sc
            );
          }

          setData(response);
        } catch (err) {
          console.log(err);
        }
      };
      fetchData();
    }
  }, [method, identifier, apiRoute]);

  useEffect(() => {
    if (!dropdownApis || Object.keys(dropdownApis).length === 0) return;

    const fetchDropdowns = async () => {
      try {
        const results = {};
        for (const key in dropdownApis) {
          const res = await CommonListFetch(dropdownApis[key], 0, 200);
          results[key] = res.dtoList || res || [];
        }
        setDropdownData(results);
      } catch (err) {
        console.log(err);
      }
    };

    fetchDropdowns();
  }, [dropdownApis]);

  useEffect(() => {
    if (
      data &&
      (Object.keys(dropdownApis).length === 0 ||
        Object.keys(dropdownData).length > 0)
    ) {
      reset(data);
    }
  }, [data, dropdownData, dropdownApis, reset]);

  const getApiUrl = (currentMethod, currentRoute) => {
    return currentMethod === "add"
      ? `http://localhost:8080/api/${currentRoute}/add`
      : `http://localhost:8080/api/${currentRoute}/update`;
  };

  const onSubmit = async (formData) => {
  try {
    setLoading(true);
    setSubmitError("");
    setSubmitSuccess("");

    const submissionPayload = { ...formData };

    if (submissionPayload.roles) {
      submissionPayload.roles = Array.isArray(submissionPayload.roles)
        ? submissionPayload.roles
        : [submissionPayload.roles];
    }

    if (Array.isArray(fields)) {
      fields.forEach((f) => {
        if (
          f.type === "select" &&
          f.name !== "roles" &&
          !f.multiple
        ) {
          const val = submissionPayload[f.name];

          if (
            val !== undefined &&
            val !== null &&
            val !== ""
          ) {
            submissionPayload[f.name] = Array.isArray(val)
              ? val[0]
              : val;
          }
        }
      });
    }

    console.log(
      "Payload being sent:",
      JSON.stringify(submissionPayload, null, 2)
    );

    const token = localStorage.getItem("token");
    const url = getApiUrl(method, apiRoute);

    const response = await fetch(url, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(submissionPayload),
    });

    const responseText = await response.text();

    console.log("Response:", responseText);

    let resData = {};

    if (responseText) {
      try {
        resData = JSON.parse(responseText);
      } catch {
        resData = { message: responseText };
      }
    }

    if (!response.ok) {
      setSubmitError(
        resData?.message ||
          responseText ||
          "Operation rejected by backend schema rules."
      );
      return;
    }

    setSubmitSuccess("Saved successfully.");

    globalThis.dispatchEvent(
      new Event("nodeDataChanged")
    );

    if (closeModal) {
      setTimeout(() => {
        closeModal();
      }, 800);
    }
  } catch (err) {
    console.log("Submit Error:", err);
    setSubmitError(
      err.message ||
        "Something went wrong while saving."
    );
  } finally {
    setLoading(false);
  }
};
  const getIcon = (name) => {
    switch (name) {
      case "identifier":
        return <Squares2X2Icon className="input-icon" />;
      case "path":
        return <LinkIcon className="input-icon" />;
      case "roles":
        return <ShieldCheckIcon className="input-icon position-select-icon" />;
      default:
        return null;
    }
  };

  return (
    <>
      <style>{`
        .edit-wrapper {
          width: 100%;
          background: #ffffff;
          border: 1px solid #ebebf5;
          border-radius: 16px;
          box-sizing: border-box;
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        }

        .edit-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 24px 32px;
          border-bottom: 1px solid #ebebf5;
        }

        .header-title {
          font-size: 18px;
          font-weight: 600;
          color: #2d2d6e;
          text-transform: capitalize;
        }

        .header-title span {
          color: #6c63ff;
          margin-left: 4px;
        }

        .edit-form {
          padding: 32px;
          display: flex;
          flex-direction: column;
          gap: 20px;
        }

        .form-field {
          display: flex;
          flex-direction: column;
          gap: 6px;
        }

        .field-label {
          font-size: 13px;
          font-weight: 500;
          color: #8888a0;
          text-transform: capitalize;
        }

        .input-container {
          position: relative;
          width: 100%;
        }

        .input-icon {
          position: absolute;
          left: 14px;
          top: 50%;
          transform: translateY(-50%);
          width: 18px;
          height: 18px;
          color: #b0b0c8;
          pointer-events: none;
          z-index: 10;
        }

        .input-icon.position-select-icon {
          top: 24px;
          transform: none;
        }

        .form-input {
          width: 100%;
          height: 44px;
          background: #f8f8fc;
          border: 1.5px solid #ebebf5;
          border-radius: 8px;
          padding: 0 14px;
          font-size: 14px;
          color: #2d2d6e;
          outline: none;
          box-sizing: border-box;
          transition: all 0.15s ease;
        }

        .form-input.has-icon {
          padding-left: 44px;
        }

        .form-input:focus {
          border-color: #6c63ff;
          background: #ffffff;
        }

        .form-input::placeholder {
          color: #b0b0c8;
        }

        .form-input:read-only {
          background: #f4f5fa;
          color: #8888a0;
        }

        select.form-input {
          appearance: none;
          background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23b0b0c8' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
          background-repeat: no-repeat;
          background-position: right 14px center;
          background-size: 16px;
          padding-right: 40px;
        }

        select.form-input[multiple] {
          height: auto;
          min-height: 130px;
          padding: 14px 14px 14px 44px;
          background-image: none;
        }

        .action-row {
          display: flex;
          align-items: center;
          justify-content: flex-end;
          gap: 12px;
          margin-top: 12px;
          border-top: 1px solid #ebebf5;
          padding-top: 24px;
        }

        .cancel-btn {
          height: 40px;
          padding: 0 16px;
          background: #ffffff;
          border: 1px solid #ebebf5;
          border-radius: 8px;
          color: #4b4b75;
          font-size: 13px;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.15s ease;
        }

        .cancel-btn:hover {
          border-color: #b0b0c8;
          color: #2d2d6e;
        }

        .submit-btn {
          height: 40px;
          padding: 0 20px;
          background: #6c63ff;
          border: none;
          border-radius: 8px;
          color: #ffffff;
          font-size: 13px;
          font-weight: 500;
          cursor: pointer;
          transition: background-color 0.15s ease;
        }

        .submit-btn:hover {
          background-color: #5850ec;
        }

        .submit-btn:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }
      `}</style>

      <div className="edit-wrapper">
        <div className="edit-header">
          <h2 className="header-title">
            {method}
            <span>{title}</span>
          </h2>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="edit-form">
          {fields?.map((field) => {
            const icon = getIcon(field.name);
            const hasIcon = icon !== null;

            return (
              <div key={field.name} className="form-field">
                <label className="field-label">{field.placeholder || field.name}</label>
                
                <div className="input-container">
                  {icon}
                  
                  {(field.type === "text" ||
                    field.type === "email" ||
                    field.type === "number" ||
                    field.type === "password") && (
                    <input
                      type={field.type}
                      {...register(field.name, { required: field.required })}
                      placeholder={field.placeholder}
                      readOnly={field.readOnly || false}
                      className={`form-input ${hasIcon ? "has-icon" : ""}`}
                    />
                  )}

                  {field.type === "select" && (
                    <select
                      {...register(field.name)}
                      multiple={field.multiple || false}
                      className={`form-input ${hasIcon ? "has-icon" : ""}`}
                    >
                      {!field.multiple && <option value="">Select Option</option>}
                      {field.hardCoded === "true"
                        ? field.hardCodedArray.map((item) => (
                            <option key={item} value={item}>
                              {item}
                            </option>
                          ))
                        : dropdownData[field.dataKey]?.map((item) => (
                            <option key={item.id} value={item.identifier}>
                              {item.identifier}
                            </option>
                          ))}
                    </select>
                  )}
                </div>
              </div>
            );
          })}

          {submitSuccess && (
            <div style={{ backgroundColor: "#f2fdf5", border: "1px solid #d3f9df", color: "#10b981", fontSize: "13px", borderRadius: "8px", padding: "10px 12px", textAlign: "center" }}>
              {submitSuccess}
            </div>
          )}
          {submitError && (
            <div style={{ backgroundColor: "#fff2f2", border: "1px solid #ffd6d6", color: "#e55555", fontSize: "13px", borderRadius: "8px", padding: "10px 12px", textAlign: "center" }}>
              {submitError}
            </div>
          )}

          <div className="action-row">
            <button type="button" onClick={closeModal} className="cancel-btn">
              Cancel
            </button>
            <button type="submit" disabled={loading} className="submit-btn">
              {loading ? "Submitting..." : "Save Changes"}
            </button>
          </div>
        </form>
      </div>
    </>
  );
};

CommonEdit.propTypes = {
  title: PropTypes.string,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      type: PropTypes.string.isRequired,
      placeholder: PropTypes.string,
      required: PropTypes.bool,
      readOnly: PropTypes.bool,
      multiple: PropTypes.bool,
      hardCoded: PropTypes.oneOf(["true", "false"]),
      hardCodedArray: PropTypes.arrayOf(PropTypes.string),
      dataKey: PropTypes.string,
    })
  ),
  apiRoute: PropTypes.string.isRequired,
  dropdownApis: PropTypes.object,
  method: PropTypes.oneOf(["add", "update"]).isRequired,
  identifier: PropTypes.string,
  closeModal: PropTypes.func.isRequired,
};

CommonEdit.defaultProps = {
  title: "",
  fields: [],
  dropdownApis: {},
  identifier: "",
};

export default CommonEdit;