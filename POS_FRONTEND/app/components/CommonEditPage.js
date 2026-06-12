"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import PropTypes from "prop-types";
import api from "../services/api";
import CommonDropDown from "./CommonDropDown";
import Layout from "./Layout";

/* ✅ PropTypes */

CommonEditPage.propTypes = {
  title: PropTypes.string,
  fetchApi: PropTypes.oneOfType([PropTypes.string, PropTypes.func]).isRequired,
  updateApi: PropTypes.oneOfType([PropTypes.string, PropTypes.func]).isRequired,
  redirectRoute: PropTypes.string,
  fields: PropTypes.array,
  identifierParam: PropTypes.string,
  submitButtonText: PropTypes.string,
  initialData: PropTypes.object,
  onSuccess: PropTypes.func,
};

export default function CommonEditPage({
  title = "Edit",
  fetchApi,
  updateApi,
  redirectRoute,
  fields = [],
  identifierParam = "identifier",
  submitButtonText = "Update",
  initialData,
  onSuccess,
}) {
  const router = useRouter();
  const params = useParams();

  const [formData, setFormData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [pageLoading, setPageLoading] = useState(true);

  /* ✅ Normalize Dropdown */
  const normalizeDropdownValue = (field, val) => {
    const key = field.optionValue || "identifier";

    if (field.multiple) {
      if (!val) return [];
      if (Array.isArray(val)) {
        return val.map((item) =>
          typeof item === "object" ? item[key] : item
        );
      }
      return [typeof val === "object" ? val[key] : val];
    }

    if (val && typeof val === "object") return val[key];
    return val;
  };

  /* ✅ Load Data */
  const loadData = async (identifier) => {
    try {
      setPageLoading(true);

      const res =
        typeof fetchApi === "function"
          ? await fetchApi(identifier)
          : await api.get(fetchApi, { params: { identifier } });

      let data = res?.data?.data ?? res?.data ?? res ?? {};

      fields.forEach((f) => {
        if (f.type === "dropdown") {
          data[f.name] = normalizeDropdownValue(f, data[f.name]);
        }
      });

      setFormData(data);
    } catch (err) {
      console.error("Load failed:", err);
    } finally {
      setPageLoading(false);
    }
  };

  /* ✅ Initial Load */
  useEffect(() => {
    if (initialData) {
      setFormData(initialData);
      setPageLoading(false);
      return;
    }

    const identifier = params?.[identifierParam];
    if (identifier) loadData(identifier);
  }, [params, initialData]);

  /* ✅ Handle Change (FIXED) */
  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    setErrors((prev) => ({
      ...prev,
      [name]: "",
    }));
  };

  /* ✅ Validation */
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

  /* ✅ Submit */
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      setLoading(true);

      if (typeof updateApi === "function") {
        await updateApi(formData);
      } else {
        await api.post(updateApi, formData);
      }

      if (onSuccess) return onSuccess();
      if (redirectRoute) return router.push(redirectRoute);
    } finally {
      setLoading(false);
    }
  };

  /* ✅ FIELD RENDERER (FIX FOR SONAR) */
  const renderFormField = (f, val) => {
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
        <div className="flex gap-4 mt-2">
          {f.options.map((opt) => (
            <label
              key={opt.value ?? opt.label}
              className="flex items-center gap-2 text-gray-700"
            >
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
    }

    return (
      <input
        type={f.type}
        name={f.name}
        value={val}
        onChange={handleChange}
        disabled={f.readOnly}
        className="
          w-full px-3 py-2.5 rounded-lg text-sm text-gray-800
          bg-white border border-gray-300
          focus:outline-none focus:ring-2 focus:ring-blue-600
          focus:border-blue-600 transition
        "
      />
    );
  };

  /* ✅ Loading UI */
  if (pageLoading || !formData) {
    return (
      <Layout>
        <div className="flex items-center justify-center py-20">
          <div className="bg-white rounded-2xl shadow-sm border border-blue-50 px-8 py-6 text-gray-500">
            Loading...
          </div>
        </div>
      </Layout>
    );
  }

  /* ✅ UI */
  return (
    <Layout>
      <div className="flex justify-center py-6">
        <div className="w-full max-w-3xl bg-white rounded-2xl shadow-sm border border-blue-50">

          {/* HEADER */}
          <div className="px-6 py-5 border-b border-blue-50 flex justify-between">
            <h2 className="text-xl font-bold text-blue-900">{title}</h2>
            <button
              onClick={() =>
                onSuccess
                  ? onSuccess()
                  : redirectRoute && router.push(redirectRoute)
              }
              className="w-9 h-9 bg-gray-100 hover:bg-gray-200 rounded-lg"
            >
              ✕
            </button>
          </div>

          {/* FORM */}
          <form onSubmit={handleSubmit} className="p-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

              {fields.map((f) => {
                const val =
                  formData[f.name] ?? (f.multiple ? [] : "");

                return (
                  <div key={f.name}>
                    <label className="text-sm font-medium text-gray-700">
                      {f.label}
                    </label>

                    {renderFormField(f, val)}

                    {errors[f.name] && (
                      <p className="text-red-500 text-xs mt-1">
                        {errors[f.name]}
                      </p>
                    )}
                  </div>
                );
              })}

            </div>

            {/* ACTIONS */}
            <div className="flex justify-end gap-3 mt-6">
              <button
                type="button"
                onClick={() =>
                  redirectRoute && router.push(redirectRoute)
                }
                className="px-4 py-2 border rounded-lg"
              >
                Cancel
              </button>

              <button
                type="submit"
                disabled={loading}
                className="px-5 py-2 bg-blue-700 text-white rounded-lg"
              >
                {loading ? "Updating..." : submitButtonText}
              </button>
            </div>

          </form>
        </div>
      </div>
    </Layout>
  );
}