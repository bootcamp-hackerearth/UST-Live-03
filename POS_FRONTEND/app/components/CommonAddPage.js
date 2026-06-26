"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "../services/api";
import CommonDropDown from "./CommonDropDown";
import Layout from "./Layout";

export default function CommonAddPage({
  title = "Add",
  submitApi,
  method = "post",
  redirectRoute,
  fields = [],
  initialValues = {},
  submitButtonText = "Save",
}) {
  const router = useRouter();

  const [formData, setFormData] = useState(initialValues);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState(null);
  

  const handleChange = (e) => {
    let { name, value } = e.target; 

    if (name === "phoneNo") {
      value = value.replaceAll(/\D/g, "").slice(0, 10);
    }

    let parsedValue = value;
    if (value === "true") parsedValue = true;
    if (value === "false") parsedValue = false;

    const keys = name.split(".");

    setFormData((prev) => {
      const updated = { ...prev };
      let temp = updated;

      keys.forEach((key, index) => {
        if (index === keys.length - 1) {
          temp[key] = parsedValue;
        } else {
          if (!temp[key]) temp[key] = {}; 
          temp = temp[key];
        }
      });

      return updated;
    });
      setErrors((prev) => ({ ...prev, [name]: "" }));
    setServerError(null);
    };

  

  const validate = () => {
  const newErrors = {};

  fields.forEach((f) => {
    if (f.type === "checkbox-action" || f.readOnly) return;

    let val = getValue(formData, f.name);
    const isEmpty = val === undefined || val === null || val === "";
    
    if (isEmpty) {
      newErrors[f.name] = `${f.label || "This field"} is required`;
      return;
    }

    if (f.validation?.pattern && !f.validation.pattern.test(String(val))) {
      newErrors[f.name] = f.validation.message || "Invalid format";
    }
  });

  setErrors(newErrors);
  return Object.keys(newErrors).length === 0;
};
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      setLoading(true);
      setServerError(null);

      const response =
        typeof submitApi === "function"
          ? await submitApi(formData)
          : await api[method.toLowerCase()](submitApi, formData);

          if (response?.data?.success === false) {
        setServerError(response.data.message || "Operation failed.");
        setLoading(false);
        return;
      }

      console.log(" SUCCESS:", response?.data);

      if (redirectRoute) router.push(redirectRoute);
    } catch (err) {
      console.error(
        " SUBMIT ERROR:",
        err.response?.data || err.message
      );

      alert(err.response?.data?.message || "Failed to save.");
    } finally {
      setLoading(false);
    }
  };const renderCheckboxAction = (f) => (
  <div className="md:col-span-2 py-2">
    <label className="flex items-center gap-2 text-sm font-bold text-blue-700 cursor-pointer">
      <input
        type="checkbox"
        className="w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
        onChange={(e) => {
          if (e.target.checked) {
            setFormData((prev) => ({
              ...prev,
              shippingAddress: { ...prev.billingAddress }
            }));
          }
        }}
      />
      {f.label}
    </label>
  </div>
);


  const renderInput = (f, val) => (
    <input
      type={f.type}
      name={f.name}
      value={val}
      onChange={handleChange}
      disabled={f.readOnly}
      className="w-full px-3 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-600"
    />
  );

  const renderTextarea = (f, val) => (
    <textarea
      name={f.name}
      value={val}
      onChange={handleChange}
      className="w-full px-3 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-600"
    />
  );

  const renderDropdown = (f, val) => (
  <CommonDropDown
    key={f.name}
    {...f}
    value={val}
    onChange={handleChange}
  />
);

  const renderRadio = (f, val) => (
    <div className="flex gap-4 mt-2">
      {f.options.map((opt) => (
        <label key={opt.value} className="flex items-center gap-2">
          <input
            type="radio"
            name={f.name}
            value={opt.value}
            checked={String(val) === String(opt.value)}
            onChange={handleChange}
            className="accent-blue-600"
          />
          {opt.label}
        </label>
      ))}
    </div>
  );

  const fieldRenderers = {
    text: renderInput,
    number: renderInput,
    email: renderInput,
    password: renderInput,
    textarea: renderTextarea,
    dropdown: renderDropdown,
    radio: renderRadio,
    "checkbox-action": renderCheckboxAction,
  };

  const getValue = (obj, path) => {
    return path.split(".").reduce((acc, key) => acc?.[key], obj);
  };

  const renderField = (f) => {
    const val =
      getValue(formData, f.name) ?? (f.multiple ? [] : "");

    const renderer = fieldRenderers[f.type];
    return renderer ? renderer(f, val) : null;
  };

  return (
    <Layout>
      <div className="flex justify-center py-6">
        <div className="w-full max-w-3xl bg-white rounded-2xl shadow-sm border border-blue-50">

          <div className="px-6 py-5 border-b flex justify-between items-center">
            <h2 className="text-xl font-bold text-blue-900">
              {title}
            </h2>

            <button
              onClick={() => router.push(redirectRoute)}
              className="w-9 h-9 bg-gray-100 rounded-lg"
            >
              ✕
            </button>
          </div>

          <form onSubmit={handleSubmit} className="p-6">

            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {serverError && (
              <div className="mb-4 p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm">
                {serverError}
              </div>
            )}
              {fields.map((f) => (
                <div key={f.name}>
                  <label className="text-sm font-medium text-gray-700">
                    {f.label}
                  </label>


                  {renderField(f)}
                  

                  {errors[f.name] && (
                    <p className="text-red-500 text-xs mt-1">
                      {errors[f.name]}
                    </p>
                  )}
                </div>
              ))}
            </div>

            <div className="flex justify-end gap-3 mt-6">
              <button
                type="button"
                onClick={() => router.push(redirectRoute)}
                className="px-4 py-2 border rounded-lg"
              >
                Cancel
              </button>

              <button
                type="submit"
                disabled={loading}
                className="px-5 py-2 bg-blue-700 text-white rounded-lg"
              >
                {loading ? "Saving..." : submitButtonText}
              </button>
            </div>

          </form>
        </div>
      </div>
    </Layout>
  );
}

CommonAddPage.propTypes = {
  title: PropTypes.string,
  submitApi: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.func,
  ]),
  
method: PropTypes.oneOf([
  "get",
  "post",
  "put",
  "patch",
  "delete",
]),
  redirectRoute: PropTypes.string,
  fields: PropTypes.array,
  initialValues: PropTypes.object,
  submitButtonText: PropTypes.string,
};