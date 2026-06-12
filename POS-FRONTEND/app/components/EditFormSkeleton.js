"use client";
import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import axios from "axios";
import { useRouter, useParams } from "next/navigation";
import CommonDropdown from "@/app/components/CommonDropDown";
import Layout from "@/app/components/Layout";

export default function EditFormSkeleton({
  title,
  apiPath,
  fields = [],
  paramKey = "identifier",
  getParamKey,
}) {
  const router = useRouter();
  const params = useParams();

  const paramValue = decodeURIComponent(params[paramKey] || "");

  const BASE_URL = "http://localhost:8080/api";

  const [token, setToken] = useState("");
  const [tokenReady, setTokenReady] = useState(false);

  const [formData, setFormData] = useState({});
  const [dropdownOptions, setDropdownOptions] = useState({});
  const [dropdownLoading, setDropdownLoading] = useState({});
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const fieldsDependency = JSON.stringify(fields);

  const getPrefilledValue = (field, value) => {
    if (field.type === "select") {
      if (field.multiple) {
        return Array.isArray(value)
          ? value.map((v) =>
              String(
                typeof v === "object"
                  ? v[field.optionValue || "identifier"]
                  : v
              )
            )
          : [];
      }
      return String(
        typeof value === "object"
          ? value[field.optionValue || "identifier"]
          : value
      );
    }
    return value;
  };

  useEffect(() => {
    const storedToken = localStorage.getItem("token") || "";
    setToken(storedToken);
    setTokenReady(true);
  }, []);

  useEffect(() => {
    if (!tokenReady || !token) return;

    async function loadData() {
      try {
        const res = await axios.get(`${BASE_URL}/${apiPath}/get`, {
          params: {
            [getParamKey || paramKey]: paramValue,
          },
          headers: { Authorization: `Bearer ${token}` },
        });

        const data = res.data;
        const prefilled = {};

        fields.forEach((field) => {
          let value = data[field.name];

          if (!value && field.name === "username") {
            value = data.username || data.identifier;
          }

          if (value !== undefined && value !== null) {
            prefilled[field.name] = getPrefilledValue(field, value);
          }
        });

        setFormData(prefilled);
      } catch (err) {
        console.error("Failed to load data:", err);
        setError("Failed to load data.");
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, [paramKey, paramValue, apiPath, token, fieldsDependency, getParamKey, tokenReady]);

  const loadDropdownData = (field) => {
    setDropdownLoading((prev) => ({ ...prev, [field.name]: true }));

    const url = field.endpoint
      ? `${BASE_URL}/${field.api}/${field.endpoint}`
      : `${BASE_URL}/${field.api}/findByStatus`;

    axios
      .get(url, { headers: { Authorization: `Bearer ${token}` } })
      .then((res) => {
        const data = res.data?.data || res.data || [];
        setDropdownOptions((prev) => ({
          ...prev,
          [field.name]: data,
        }));
      })
      .catch(() => {
        setDropdownOptions((prev) => ({
          ...prev,
          [field.name]: [],
        }));
      })
      .finally(() => {
        setDropdownLoading((prev) => ({
          ...prev,
          [field.name]: false,
        }));
      });
  };

  useEffect(() => {
    if (!tokenReady || !token) return;

    fields.forEach((field) => {
      if (field.type === "select" && field.api) {
        loadDropdownData(field);
      }
    });
  }, [fieldsDependency, token, tokenReady]);

  function handleValueChange(field, e) {
    const { name, value, selectedOptions } = e.target;

    if (field.multiple) {
      const values = Array.from(selectedOptions).map((opt) =>
        String(opt.value)
      );
      setFormData((prev) => ({ ...prev, [name]: values }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  }
  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    setSaving(true);

    try {
      const payload = {
        ...formData,
        [paramKey]: paramValue,
      };

      const res = await axios.post(
        `${BASE_URL}/${apiPath}/update`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const data = res.data;

      if (data?.identifier && !data?.message) {
        setSuccess(`${title} updated successfully`);
        setTimeout(() => router.back(), 1200);
      } else {
        setError(data?.message || "Update failed.");
      }
    } catch {
      setError("Server error.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <Layout>
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6">
      <div className="w-full max-w-lg bg-white rounded-2xl border p-8 shadow-sm">

        <h2 className="text-xl font-bold mb-6">Edit {title}</h2>

        {error && <div className="text-red-500 text-sm mb-3">{error}</div>}
        {success && <div className="text-green-600 text-sm mb-3">{success}</div>}

        {loading ? (
          <p className="text-gray-400">Loading...</p>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="text-sm mb-1 block capitalize">
                {paramKey}
              </label>
              <input
                value={paramValue}
                disabled
                className="w-full border p-2 bg-gray-100 rounded text-gray-500"
              />
            </div>
            {fields.map((field) => {
              const renderField = () => {
                if (field.type === "select") {
                  if (dropdownLoading[field.name]) {
                    return <p className="text-sm text-gray-400">Loading...</p>;
                  }
                  return (
                    <CommonDropdown
                      label={field.label}
                      name={field.name}
                      options={dropdownOptions[field.name] || []}
                      value={
                        field.multiple
                          ? (formData[field.name] || []).map(String)
                          : String(formData[field.name] || "")
                      }
                      multiple={field.multiple || false}
                      optionLabel={field.optionLabel || "name"}
                      optionValue={field.optionValue || "identifier"}
                      onChange={(e) => handleValueChange(field, e)}
                    />
                  );
                }
                return (
                  <>
                    <label className="text-sm mb-1 block">
                      {field.label}
                    </label>
                    <input
                      name={field.name}
                      type={field.type || "text"}
                      value={formData[field.name] || ""}
                      onChange={(e) => handleValueChange(field, e)}
                      disabled={field.disabled || false}
                      className="w-full border p-2 rounded disabled:bg-gray-100 disabled:text-gray-500"
                    />
                  </>
                );
              };
              return <div key={field.name}>{renderField()}</div>;
            })}

            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => router.back()}
                className="flex-1 bg-gray-200 p-2 rounded"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={saving}
                className="flex-1 bg-blue-600 text-white p-2 rounded"
              >
                {saving ? "Updating..." : "Update"}
              </button>
            </div>

          </form>
        )}
      </div>
    </div>
    </Layout>
  );
}

EditFormSkeleton.propTypes = {  
  title: PropTypes.string.isRequired, 
  apiPath: PropTypes.string.isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({         
      name: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired, 
      type: PropTypes.string, 
      multiple: PropTypes.bool,
      api: PropTypes.string,  
      optionLabel: PropTypes.string,
      optionValue: PropTypes.string,
      endpoint: PropTypes.string, 
      disabled: PropTypes.bool,                           
    })
  ).isRequired,
  paramKey: PropTypes.string,
  getParamKey: PropTypes.func,
};