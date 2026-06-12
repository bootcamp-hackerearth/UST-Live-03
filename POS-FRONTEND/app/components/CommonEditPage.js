"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import PropTypes from "prop-types";
import api from "../services/api";
import CommonDropDown from "./CommonDropDown";
import Layout from "./Layout";


CommonDropDown.propTypes = {
  name: PropTypes.string.isRequired,
  value: PropTypes.any,
  onChange: PropTypes.func.isRequired,
  type: PropTypes.string,
  label: PropTypes.string,
  multiple: PropTypes.bool,
  readOnly: PropTypes.bool,
  options: PropTypes.arrayOf(
    PropTypes.shape({
      label: PropTypes.string,
      value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    })
  ),
  optionLabel: PropTypes.string,
  optionValue: PropTypes.string,
};

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

    if (val && typeof val === "object") {
      return val[key];
    }

    return val;
  };

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

  useEffect(() => {
    if (initialData) {
      setFormData(initialData);
      setPageLoading(false);
      return;
    }

    const identifier = params?.[identifierParam];
    if (identifier) {
      loadData(identifier);
    }
  }, [params, initialData]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

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

  const renderInputField = (f, val) => (
    <input
      type={f.type}
      name={f.name}
      value={val}
      onChange={handleChange}
      disabled={f.readOnly}
      className="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-200"
    />
  );

  const renderDropdownField = (f, val) => (
    <CommonDropDown {...f} value={val} onChange={handleChange} />
  );

  const renderRadioField = (f, val) => (
    <div className="flex gap-4">
      {f.options.map((opt) => (
        <label
          key={opt.value ?? opt.label}
          className="flex items-center gap-2"
        >
          <input
            type="radio"
            name={f.name}
            value={opt.value}
            checked={String(val) === String(opt.value)}
            onChange={handleChange}
          />
          {opt.label}
        </label>
      ))}
    </div>
  );

  const fieldRenderers = {
    text: renderInputField,
    number: renderInputField,
    dropdown: renderDropdownField,
    radio: renderRadioField,
  };

  const renderField = (f) => {
    const val = formData[f.name] ?? (f.multiple ? [] : "");
    const renderer = fieldRenderers[f.type];
    return renderer ? renderer(f, val) : null;
  };

  if (pageLoading || !formData) {
    return (
      <Layout>
        <div className="p-6 text-center">Loading...</div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="p-6 flex justify-center">
        <div className="bg-white p-6 rounded-xl shadow-lg w-full max-w-2xl">

          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-semibold text-gray-800">
              {title}
            </h2>

            <button
              onClick={() =>
                onSuccess
                  ? onSuccess()
                  : redirectRoute && router.push(redirectRoute)
              }
              className="text-gray-500 hover:text-gray-700 text-lg"
            >
              ✕
            </button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {fields.map((f) => (
                <div key={f.name}>
                  <label className="block text-sm mb-1 font-medium text-gray-700">
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
                onClick={() =>
                  onSuccess
                    ? onSuccess()
                    : redirectRoute && router.push(redirectRoute)
                }
                className="px-4 py-2 border rounded-lg text-gray-600 hover:bg-gray-100"
              >
                Cancel
              </button>

              <button
                type="submit"
                disabled={loading}
                className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
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
