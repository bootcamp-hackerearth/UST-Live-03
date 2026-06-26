"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "@/app/services/api";
import CommonDropDown from "@/app/components/CommonDropDown";
import Layout from "@/app/components/Layout";

export default function CommonAddPage({
  title = "Add",
  submitApi,
  redirectRoute,
  onCancel,
  fields = [],
  initialValues = {},
  submitButtonText = "Save",
  onFormChange,
}) {
  const router = useRouter();

  const [formData, setFormData] = useState(initialValues);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    const updated = { ...formData, [name]: value };
    setFormData(updated);
    if (onFormChange) onFormChange(updated);
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    let newErrors = {};

    fields.forEach((f) => {
      const val = formData[f.name];

      if (f.type === "dropdown" && f.multiple) {
        if (!val || val.length === 0)
          newErrors[f.name] = "Required";
      } else if (f.required && !val && !f.readOnly) {
        newErrors[f.name] = "Required";
      }
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleCloseOrCancel = () => {
    if (onCancel) {
      onCancel();
    } else if (redirectRoute && redirectRoute !== "#") {
      router.push(redirectRoute);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      setLoading(true);

      let success = true;

if (typeof submitApi === "function") {
  success = await submitApi(formData);
} else {
  await api.post(submitApi, formData);
}

if (success && redirectRoute && redirectRoute !== "#") {
  router.push(redirectRoute);
}
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const renderField = (f) => {
    const val = formData[f.name] ?? (f.multiple ? [] : "");

    if (["text", "number"].includes(f.type)) {
      return (
        <input
          name={f.name}
          value={val}
          onChange={handleChange}
          type={f.type}
          className="w-full border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-blue-200"
        />
      );
    }

    if (f.type === "dropdown") {
      return (
        <CommonDropDown
          {...f}
          value={val}
          onChange={handleChange}
        />
      );
    }

    if (f.type === "radio") {
      return (
        <div className="flex gap-4">
          {f.options.map((opt) => (
            <label key={`${f.name}-${opt.value}`} className="flex items-center gap-2">
              <input
                type="radio"
                name={f.name}
                value={opt.value}
              />
              {opt.label}
            </label>
          ))}
        </div>
      );
    }
  };

  return (
    <Layout>
      <div className="fixed inset-0 bg-black/30 flex items-center justify-center z-50">
        <div className="bg-white w-full max-w-2xl rounded-2xl shadow-xl p-6 animate-fadeIn">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-semibold text-gray-800">
              {title}
            </h2>
            <button
              type="button"
              onClick={handleCloseOrCancel}
              className="text-gray-500 hover:text-gray-700"
            >
              ✕
            </button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {fields.map((f) => (
                <div key={f.name}>
                  <label className="block text-sm mb-1 text-gray-700 font-medium">
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

            <div className="flex justify-end gap-2 mt-6">
              <button
                type="button"
                onClick={handleCloseOrCancel}
                className="px-4 py-2 rounded-lg border border-gray-300 text-gray-600 hover:bg-gray-100"
              >
                Cancel
              </button>

              <button
                type="submit"
                disabled={loading}
                className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
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
  submitApi: PropTypes.oneOfType([PropTypes.string, PropTypes.func]),
  redirectRoute: PropTypes.string.isRequired,
  onCancel: PropTypes.func,
  fields: PropTypes.array,
  initialValues: PropTypes.object,
  submitButtonText: PropTypes.string,
  onFormChange: PropTypes.func,
};