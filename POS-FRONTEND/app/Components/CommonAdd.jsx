"use client";
import PropTypes from "prop-types";
import { useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../api/axiosInstance";
import SingleSelectDropdown from "../Components/SingleSelectDropdown";
import MultiSelectDropdown from "../Components/MultiSelectDropdown";

function CommonAdd({
  title,
  apiPath,
  extraFields = [],
  extraData = {},
  onSuccessPath,
  showIdentifierDescription = true,
  addEndpoint,
}) {

  const router = useRouter();

  const [identifier, setIdentifier] = useState("");
  const [description, setDescription] = useState("");
  const [values, setValues] = useState({});
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (key, value) => {
    setValues((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  const buildPayload = () => {

    const payload = {
      ...(showIdentifierDescription ? { identifier, description } : {}),
      ...values,
      ...extraData,
    };

    extraFields.forEach((field) => {

      if (field.valueFormat === "csv" && Array.isArray(payload[field.key])) {
        payload[field.key] = payload[field.key].join(",");
      }

      if (field.valueType === "boolean") {
        payload[field.key] =
          payload[field.key] === true ||
          payload[field.key] === "true";
      }

      if (field.type === "number" && payload[field.key] !== "" && payload[field.key] !== null && payload[field.key] !== undefined) {
        const parsedNumber = Number(payload[field.key]);
        if (Number.isFinite(parsedNumber)) {
          payload[field.key] = parsedNumber;
        }
      }

    });

    return payload;
  };

  const handleSubmit = async (event) => {

    event.preventDefault();

    setError("");
    setLoading(true);

    try {

      console.log("[CommonAdd] token:", localStorage.getItem("token"));
      console.log("PAYLOAD :", buildPayload());

      const endpoint = addEndpoint || `/${apiPath}/add`;

      const response = await axiosInstance.post(endpoint, buildPayload());

      console.log("RESPONSE :", response.data);

      if (response.data.success === false) {

        setError(
          response.data.message ||
          `Failed to add ${title}`
        );

        return;
      }

      router.push(onSuccessPath || "/");

    } catch (err) {

      console.error(err);

      const status = err?.response?.status;

      const message =
        err?.response?.data?.message ||
        err.message ||
        "Unable to save.";

      const endpoint = addEndpoint || `/${apiPath}/add`;

      setError(
        status
          ? `${message} (HTTP ${status} on ${endpoint})`
          : message
      );

    } finally {

      setLoading(false);

    }
  };

  const renderField = (field) => {

    if (field.type === "custom") {
      return field.component;
    }

    if (field.type === "select") {

      return (
        <SingleSelectDropdown
          label={field.label}
          name={field.key}
          apiPath={field.apiPath}
          options={field.options}
          valueField={field.valueField}
          labelField={field.labelField}
          labelFields={field.labelFields}
          value={values[field.key] || ""}
          placeholder={
            field.placeholder ||
            `Select ${field.label}`
          }
          required={field.required}
          onChange={handleChange}
        />
      );
    }

    if (field.type === "multiselect") {

      return (
        <MultiSelectDropdown
          label={field.label}
          name={field.key}
          apiPath={field.apiPath}
          options={field.options}
          valueField={field.valueField}
          labelField={field.labelField}
          labelFields={field.labelFields}
          value={values[field.key] || []}
          required={field.required}
          helperText="Hold Ctrl to select multiple"
          onChange={handleChange}
        />
      );
    }

    const isNumericField = field.type === "number";
    const inputType = isNumericField ? "text" : field.type || "text";
    const inputMode = field.inputMode || (isNumericField ? "decimal" : undefined);
    const step = field.step || (isNumericField ? "any" : undefined);

    return (
    
      <div className="flex flex-col gap-1">

        <label className="text-sm font-semibold text-black">
          {field.label}
        </label>

        <input
          type={inputType}
          inputMode={inputMode}
          step={step}
          placeholder={
            field.placeholder ||
            `Enter ${field.label}`
          }
          value={values[field.key] ?? ""}
          onChange={(event) =>
            handleChange(
              field.key,
              event.target.value
            )
          }
          required={field.required}
          className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-black outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition"
        />

      </div>
    );
  };

  return (

    <div className="min-h-screen flex items-center justify-center px-4 py-10">
      <div className="w-full max-w-3xl mx-auto">

      <div className="mb-6">

        <h2 className="text-2xl font-bold text-black">
          Add {title}
        </h2>

        <p className="mt-1 text-sm text-black">
          Fill in the details to add a new{" "}
          {title.toLowerCase()}.
        </p>

      </div>

      <form
        onSubmit={handleSubmit}
        className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-8"
      >

        {error && (

          <div className="mb-5 rounded-xl bg-rose-50 px-4 py-3 text-sm font-medium text-rose-600">
            {error}
          </div>

        )}

        <div className="grid gap-5 md:grid-cols-2">

          {showIdentifierDescription && (
            <>
              <div className="flex flex-col gap-1">

                <label htmlFor="identifier" className="text-sm font-semibold text-black">
                  Identifier
                </label>

                <input
                  id="identifier"
                  type="text"
                  placeholder="Enter identifier"
                  value={identifier}
                  onChange={(event) =>
                    setIdentifier(event.target.value)
                  }
                  required
                  className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-black outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition"
                />

              </div>

              <div className="flex flex-col gap-1 md:col-span-2">

                <label htmlFor="description" className="text-sm font-semibold text-black">
                  Description
                </label>

                <textarea
                  id="description"
                  placeholder="Enter description"
                  value={description}
                  onChange={(event) =>
                    setDescription(event.target.value)
                  }
                  rows={3}
                  className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-black outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition resize-none"
                />

              </div>
            </>
          )}

          {extraFields.map((field) => (

            <div key={field.key}>
              {renderField(field)}
            </div>

          ))}

        </div>

        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            gap: "12px",
            marginTop: "32px",
            paddingTop: "20px",
            borderTop: "1px solid #e2e8f0",
            background: "#ffffff",
            position: "sticky",
            bottom: 0,
          }}
        >

          <button
            type="button"
            onClick={() => router.back()}
            style={{
              border: "none",
              borderRadius: "12px",
              background: "#f1f5f9",
              color: "#1e293b",
              cursor: "pointer",
              fontSize: "14px",
              fontWeight: 700,
              padding: "12px 24px",
            }}
          >
            Cancel
          </button>

          <button
            type="submit"
            disabled={loading}
            style={{
              border: "none",
              borderRadius: "12px",
              background: loading ? "#94a3b8" : "#059669",
              color: "#ffffff",
              cursor: loading ? "not-allowed" : "pointer",
              fontSize: "14px",
              fontWeight: 700,
              padding: "12px 24px",
            }}
          >
            {loading
              ? "Saving..."
              : `Save ${title}`}
          </button>

        </div>

      </form>

    </div>
  </div>
  );
}

const fieldShape = PropTypes.shape({
  apiPath: PropTypes.string,
  component: PropTypes.node,
  key: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  labelField: PropTypes.string,
  labelFields: PropTypes.arrayOf(PropTypes.string),
  options: PropTypes.arrayOf(
    PropTypes.shape({
      label: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      value: PropTypes.oneOfType([PropTypes.string, PropTypes.number, PropTypes.bool]),
    })
  ),
  placeholder: PropTypes.string,
  required: PropTypes.bool,
  type: PropTypes.string,
  valueField: PropTypes.string,
  valueFormat: PropTypes.string,
  valueType: PropTypes.string,
});

CommonAdd.propTypes = {
  addEndpoint: PropTypes.string,
  apiPath: PropTypes.string.isRequired,
  extraData: PropTypes.object,
  extraFields: PropTypes.arrayOf(fieldShape),
  onSuccessPath: PropTypes.string,
  showIdentifierDescription: PropTypes.bool,
  title: PropTypes.string.isRequired,
};

export default CommonAdd;
