"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import PropTypes from "prop-types";
import api from "../services/api";
import CommonDropDown from "./CommonDropDown";
import Layout from "./Layout";

export default function CommonEditPage({
  title = "Edit",
  fetchApi,
  method = "put",
  updateApi,
  redirectRoute,
  auditData = null,
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
  const [fetchError, setFetchError] = useState(false); // Track fetch errors
  const [serverError, setServerError] = useState(null);
  const [toast, setToast] = useState({ visible: false, message: "" });

  const triggerToast = (msg) => {
    setToast({ visible: true, message: msg });
    setTimeout(() => setToast({ visible: false, message: "" }), 3000);
  };

  const getValue = (obj, path) => {
    return path.split(".").reduce((acc, key) => acc?.[key], obj);
  };

  const handleChange = (e) => {
    let { name, value } = e.target;
    if (name === "phoneNo") value = value.replaceAll(/\D/g, "").slice(0, 10);

    const keys = name.split(".");
    setFormData((prev) => {
      const updated = { ...prev };
      let temp = updated;
      keys.forEach((key, index) => {
        if (index === keys.length - 1) {
          temp[key] = value;
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

  const loadData = async (identifier) => {
    try {
      setPageLoading(true);
      const res = typeof fetchApi === "function" 
        ? await fetchApi(identifier) 
        : await api.get(fetchApi, { params: { identifier } });
      
      setFormData(res?.data ?? res ?? {});
    } catch (err) {
      const status = err.response?.status;
      const errorMessages = {
        403: "Access Denied: You do not have permission.",
        404: "Record not found.",
        500: "Server error occurred."
      };
      
      triggerToast(errorMessages[status] || "Failed to load data.");
      setFetchError(true); 
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

    const rawIdentifier = params?.[identifierParam];
    const identifier = typeof rawIdentifier === 'string' ? rawIdentifier.trim() : rawIdentifier;
    
    if (identifier) loadData(identifier);
    else setPageLoading(false);
  }, [params, initialData]);

  const validate = () => {
    const newErrors = {};
    fields.forEach((f) => {
      if (f.type === "checkbox-action" || f.readOnly) return;
      let val = getValue(formData, f.name);
      const isEmpty = val === undefined || val === null || val === "";
      if (isEmpty) { newErrors[f.name] = `${f.label || "This field"} is required`; return; }
      if (f.validation?.pattern && !f.validation.pattern.test(String(val))) {
        newErrors[f.name] = f.validation.message || "Invalid format";
      }
      if (f.validation?.validate && !f.validation.validate(val)) {
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
      const response = typeof updateApi === "function" 
        ? await updateApi(formData) 
        : await api[method.toLowerCase()](updateApi, formData);
        
      if (response?.data?.success === false) {
        setServerError(response.data.message || "Update failed.");
        setLoading(false);
        return;
      }
      if (onSuccess) return onSuccess();
      if (redirectRoute) return router.push(redirectRoute);
    } catch (err) {
      setServerError(err.response?.data?.message || "An unexpected error occurred.");
    } finally { setLoading(false); }
  };

  const renderFormField = (f, val) => {
    if (f.type === "checkbox-action") {
      return (
        <div className="py-2">
          <label className="flex items-center gap-2 text-sm font-bold text-blue-700 cursor-pointer">
            <input type="checkbox" className="w-4 h-4" onChange={(e) => {
              if (e.target.checked) setFormData((prev) => ({ ...prev, shippingAddress: { ...prev.billingAddress } }));
            }} />
            {f.label}
          </label>
        </div>
      );
    }
    if (f.type === "dropdown") return <CommonDropDown {...f} value={val} onChange={handleChange} />;
    if (f.type === "radio") {
      return (
        <div className="flex gap-4 mt-2">
          {f.options.map((opt) => (
            <label key={opt.value} className="flex items-center gap-2">
              <input type="radio" name={f.name} value={opt.value} checked={String(val) === String(opt.value)} onChange={handleChange} />
              {opt.label}
            </label>
          ))}
        </div>
      );
    }
    return <input type={f.type} name={f.name} value={val} onChange={handleChange} disabled={f.readOnly} className="w-full px-3 py-2.5 rounded-lg border border-gray-300" />;
  };

  const formatAuditDate = (dateString) => {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return Number.isNaN(date.getTime()) ? dateString : date.toLocaleString();
  };

  if (pageLoading) return <Layout><div className="flex justify-center py-20">Loading...</div></Layout>;
  
  if (fetchError || !formData) return (
    <Layout>
      <div className="flex flex-col items-center justify-center py-20">
        <p className="text-gray-600 mb-4">Record not found or failed to load.</p>
        <button onClick={() => router.back()} className="text-blue-600 underline">Go Back</button>
      </div>
    </Layout>
  );

  const hasAuditDetails = formData && (formData.createdBy || formData.createdOn || formData.modifiedBy || formData.modifiedOn);

  return (
    <Layout>
      {toast.visible && (
        <div className="fixed top-4 right-4 z-50 bg-red-600 text-white px-6 py-3 rounded-lg shadow-xl">
          {toast.message}
        </div>
      )}
      <div className="flex justify-center py-6">
        <div className="w-full max-w-3xl bg-white rounded-2xl shadow-sm border">
          <div className="px-6 py-5 border-b flex justify-between">
            <h2 className="text-xl font-bold">{title}</h2>
            <button onClick={() => onSuccess ? onSuccess() : redirectRoute && router.push(redirectRoute)}>✕</button>
          </div>
          <form onSubmit={handleSubmit} className="p-6">
            {serverError && <div className="mb-4 p-4 bg-red-50 text-red-700 rounded-lg text-sm">{serverError}</div>}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
              {fields.map((f) => (
                <div key={f.name}>
                  <label className="text-sm font-medium">{f.label}</label>
                  {renderFormField(f, getValue(formData, f.name) ?? (f.multiple ? [] : ""))}
                  {errors[f.name] && <p className="text-red-500 text-xs mt-1">{errors[f.name]}</p>}
                </div>
              ))}
            </div>
            
            {hasAuditDetails && (
              <div className="mt-8 pt-4 border-t border-gray-100 bg-gray-50/50 rounded-lg p-4 text-xs text-gray-500 grid grid-cols-1 sm:grid-cols-2 gap-y-2 gap-x-4">
                <div><span className="font-semibold text-gray-600">Created By:</span> {formData.createdBy}</div>
                <div><span className="font-semibold text-gray-600">Created On:</span> {formatAuditDate(formData.createdOn)}</div>
                <div><span className="font-semibold text-gray-600">Modified By:</span> {formData.modifiedBy}</div>
                <div><span className="font-semibold text-gray-600">Modified On:</span> {formatAuditDate(formData.modifiedOn)}</div>
              </div>
            )}

            <div className="flex justify-end gap-3 mt-6">
              <button type="submit" disabled={loading} className="px-5 py-2 bg-blue-700 text-white rounded">
                {loading ? "Updating..." : submitButtonText}
              </button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
}
CommonEditPage.propTypes = {
  title: PropTypes.string,
  fetchApi: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.func,
  ]),
  method: PropTypes.string,
  updateApi: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.func,
  ]),
  redirectRoute: PropTypes.string,
  auditData: PropTypes.object,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string,
      type: PropTypes.string,
      readOnly: PropTypes.bool,
      multiple: PropTypes.bool,
      options: PropTypes.array,
      validation: PropTypes.shape({
        pattern: PropTypes.instanceOf(RegExp),
        message: PropTypes.string,
        validate: PropTypes.func,
      }),
    })
  ),
  identifierParam: PropTypes.string,
  submitButtonText: PropTypes.string,
  initialData: PropTypes.object,
  onSuccess: PropTypes.func,
};
