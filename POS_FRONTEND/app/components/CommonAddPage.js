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
  redirectRoute,
  fields = [],
  initialValues = {},
  submitButtonText = "Save",
}) {
  const router = useRouter();

  const [formData, setFormData] = useState(initialValues);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  /* ✅ HANDLE CHANGE */
  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  /* ✅ VALIDATION */
  const validate = () => {
    const newErrors = {};

    fields.forEach((f) => {
      const val = formData[f.name];

      if (f.type === "dropdown" && f.multiple) {
        if (!val || val.length === 0) {
          newErrors[f.name] = "Required";
        }
        return;
      }

      if (!val && !f.readOnly) {
        newErrors[f.name] = "Required";
      }
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  /* ✅ SUBMIT */
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      setLoading(true);

      if (typeof submitApi === "function") {
        await submitApi(formData);
      } else {
        await api.post(submitApi, formData);
      }

      if (redirectRoute) router.push(redirectRoute);
    } finally {
      setLoading(false);
    }
  };

  /* ✅ FIELD RENDERERS (NO DUPLICATION) */

  const renderInput = (f, val) => (
    <input
      type={f.type}
      name={f.name}
      value={val}
      onChange={handleChange}
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
    <CommonDropDown {...f} value={val} onChange={handleChange} />
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
  };

  const renderField = (f) => {
    const val = formData[f.name] ?? (f.multiple ? [] : "");
    const renderer = fieldRenderers[f.type];
    return renderer ? renderer(f, val) : null;
  };

  /* ✅ UI */
  return (
    <Layout>
      <div className="flex justify-center py-6">
        <div className="w-full max-w-3xl bg-white rounded-2xl shadow-sm border border-blue-50">

          {/* HEADER */}
          <div className="px-6 py-5 border-b border-blue-50 flex justify-between items-center">
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

          {/* FORM */}
          <form onSubmit={handleSubmit} className="p-6">

            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

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

            {/* ACTIONS */}
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

/* ✅ PROP TYPES */
CommonAddPage.propTypes = {
  title: PropTypes.string,
  submitApi: PropTypes.oneOfType([PropTypes.string, PropTypes.func]),
  redirectRoute: PropTypes.string,
  fields: PropTypes.array,
  initialValues: PropTypes.object,
  submitButtonText: PropTypes.string,
};
