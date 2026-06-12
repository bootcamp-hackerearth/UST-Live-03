"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import PropTypes from "prop-types";
import axiosInstance from "../api/axiosInstance";
import SingleSelectDropdown from "./SingleSelectDropdown";
import MultiSelectDropdown from "./MultiSelectDropdown";

function CommonEdit({
  title,
  apiPath,
  extraFields = [],
  onSuccessPath,
  lookupParam = "identifier",
  identityField = "identifier",
  showDescription = true,
}) {

  const searchParams = useSearchParams();
  const identifier = searchParams.get("identifier");
  const router = useRouter();

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const [formData, setFormData] = useState({
    identifier: "",
    description: "",
  });

  useEffect(() => {
    if (!identifier) {
      setError("Missing identifier.");
      setLoading(false);
      return;
    }

    loadData();
  }, [identifier]);

  const loadData = async () => {

    try {

      const response = await axiosInstance.get(
        `/${apiPath}/get?${lookupParam}=${encodeURIComponent(identifier)}`
      );

      const data = response.data;

      const values = {
        id: data.id,
        identifier: data.identifier || "",
        username: data.username || "",
        description: data.description || "",
      };

      extraFields.forEach((field) => {
        values[field.key] = data[field.key];
      });

      setFormData(values);

    } catch (err) {

      console.error(err);
      setError("Unable to load data.");

    } finally {

      setLoading(false);

    }
  };

  const handleChange = (key, value) => {

    setFormData((prev) => ({
      ...prev,
      [key]: value,
    }));

  };

  const buildPayload = () => {

    const payload = { ...formData };

    extraFields.forEach((field) => {

      if (
        field.valueFormat === "csv" &&
        Array.isArray(payload[field.key])
      ) {
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

  const handleSubmit = async (e) => {

    e.preventDefault();

    setSaving(true);
    setError("");

    try {

      const response = await axiosInstance.post(
        `/${apiPath}/update`,
        buildPayload()
      );

      if (response.data.success === false) {

        setError(
          response.data.message ||
          `Failed to update ${title}`
        );

        return;
      }

      router.push(onSuccessPath);

    } catch (err) {

      console.error(err);

      setError(
        err?.response?.data?.message ||
        err.message ||
        "Unable to update record."
      );

    } finally {

      setSaving(false);

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
          value={formData[field.key] || ""}
          required={field.required}
          onChange={handleChange}
        />
      );
    }

    if (field.type === "multiselect") {
      let value = [];
      if (Array.isArray(formData[field.key])) {
        value = formData[field.key];
      } else if (formData[field.key]) {
        value = String(formData[field.key]).split(",").filter(Boolean);
      }

      return (
        <MultiSelectDropdown
          label={field.label}
          name={field.key}
          apiPath={field.apiPath}
          valueField={field.valueField}
          labelField={field.labelField}
          labelFields={field.labelFields}
          value={value}
          required={field.required}
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
          value={formData[field.key] ?? ""}
          onChange={(e) =>
            handleChange(field.key, e.target.value)
          }
          className="border border-gray-300 rounded-xl px-4 py-3 text-black"
        />

      </div>
    );
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (

    <div className="min-h-screen flex items-center justify-center px-4 py-10">
      <div className="w-full max-w-3xl mx-auto">

        <h2 className="text-2xl font-bold mb-6">
          Edit {title}
        </h2>

      {error && (
        <div className="mb-4 p-3 bg-red-100 text-red-600 rounded">
          {error}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded-3xl shadow"
      >

        <div className="grid gap-5 md:grid-cols-2">

          <div>

            <label htmlFor="identityField" className="block mb-1 font-semibold text-black">
              {identityField === "username" ? "Username" : "Identifier"}
            </label>

            <input
              id="identityField"
              value={formData[identityField] || ""}
              disabled
              className="w-full border rounded-xl px-4 py-3 bg-gray-100 text-black"
            />

          </div>

          {showDescription && (
            <div className="md:col-span-2">

              <label htmlFor="editDescription" className="block mb-1 font-semibold text-black">
                Description
              </label>

              <textarea
                id="editDescription"
                rows={3}
                value={formData.description}
                onChange={(e) =>
                  handleChange(
                    "description",
                    e.target.value
                  )
                }
                className="w-full border rounded-xl px-4 py-3 text-black"
              />

            </div>
          )}

          {extraFields.map((field) => (
            <div key={field.key}>
              {renderField(field)}
            </div>
          ))}

        </div>

        <div className="mt-8 flex justify-end gap-3">

          <button
            type="button"
            onClick={() => router.back()}
            className="px-5 py-3 rounded-xl bg-gray-200"
          >
            Cancel
          </button>

          <button
            type="submit"
            disabled={saving}
            className="px-5 py-3 rounded-xl bg-indigo-600 text-white"
          >
            {saving ? "Updating..." : `Update ${title}`}
          </button>

        </div>

      </form>

    </div>
  </div>
  );
}

CommonEdit.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  extraFields: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      type: PropTypes.string,
      apiPath: PropTypes.string,
      options: PropTypes.array,
      valueField: PropTypes.string,
      labelField: PropTypes.string,
      labelFields: PropTypes.arrayOf(PropTypes.string),
      required: PropTypes.bool,
      valueFormat: PropTypes.string,
      valueType: PropTypes.string,
    })
  ),
  onSuccessPath: PropTypes.string.isRequired,
  lookupParam: PropTypes.string,
  identityField: PropTypes.string,
  showDescription: PropTypes.bool,
};

CommonEdit.defaultProps = {
  extraFields: [],
  lookupParam: "identifier",
  identityField: "identifier",
  showDescription: true,
};

export default CommonEdit;
