"use client";

import React, { useEffect, useState, useMemo, useCallback } from "react";
import PropTypes from "prop-types";
import { useForm } from "react-hook-form";
import {
  Squares2X2Icon,
  LinkIcon,
  ShieldCheckIcon,
  UserIcon,
  ClockIcon,
} from "@heroicons/react/24/outline";
import { CommonAddFetch } from "@/fetch/CommonAddFetch";
import { CommonListFetch } from "@/fetch/CommonListFetch";

const API_BASE = process.env.NEXT_PUBLIC_BASE_URL

const CommonEdit = ({
  title = "",
  fields = [],
  apiRoute,
  dropdownApis = {},
  method,
  identifier = "",
  closeModal,
}) => {
  const { register, reset, handleSubmit, formState: { errors } } = useForm({
    mode: "onChange",
    defaultValues: {}
  });

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
      return;
    }

    if (method === "update" && identifier) {
      const fetchData = async () => {
        try {
          const response = await CommonAddFetch(`${API_BASE}/${apiRoute}/get`, identifier, "text/plain");
          if (response) {
            const cleanResponse = { ...response };
            if (Array.isArray(cleanResponse.roles)) {
              cleanResponse.roles = cleanResponse.roles.map(r => typeof r === "object" ? r.identifier : r);
            }
            if (Array.isArray(cleanResponse.superCategory)) {
              cleanResponse.superCategory = cleanResponse.superCategory.map(s => typeof s === "object" ? s.identifier : s);
            }
            setData(cleanResponse);
          }
        } catch (err) {
          console.error("Failed parsing profile nodes:", err);
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
        const keys = Object.keys(dropdownApis);
        for (const key of keys) {
          const res = await CommonListFetch(dropdownApis[key], 0, 200);
          const rawList = res?.dtoList || res || [];
          results[key] = Array.isArray(rawList)
            ? rawList.filter(item => !item || !("status" in item) || item.status === true || String(item.status) === "true")
            : [];
        }
        setDropdownData(results);
      } catch (err) {
        console.error("Dropdown synchronizer pipeline failure:", err);
      }
    };
    fetchDropdowns();
  }, [dropdownApis]);

  useEffect(() => {
    if (!data || !Array.isArray(fields)) return;

    const flattenedValues = {};

    fields.forEach((field) => {
      let value;

      if (field.name.includes(".")) {
        const [parent, child] = field.name.split(".");
        value = data[parent]?.[child];
      } else {
        value = data[field.name];
      }

      if (field.type === "select") {
        if (field.multiple) {
          value = Array.isArray(value)
            ? value.map((v) =>
              typeof v === "object" ? v.identifier : String(v)
            )
            : [];
        } else {
          if (value && typeof value === "object") {
            value = value.identifier;
          }
          value = value ?? "";
        }
      }

      flattenedValues[field.name] = value;
    });

    if (data.id !== undefined) flattenedValues.id = data.id;
    if (data.version !== undefined) flattenedValues.version = data.version;

    reset(flattenedValues);
  }, [data, fields, dropdownData, reset]);

  const getSubmissionPayload = (formData) => {
    const submissionPayload = {};

    if (method === "update" && data) {
      if (data.id !== undefined) submissionPayload.id = data.id;
      if (data.version !== undefined) submissionPayload.version = data.version;
    }

    Object.keys(formData).forEach((key) => {
      if (key === "id" || key === "version") return;
      if (key.includes(".")) {
        const [parent, child] = key.split(".");
        if (!submissionPayload[parent]) submissionPayload[parent] = {};
        submissionPayload[parent][child] = formData[key];
      } else {
        submissionPayload[key] = formData[key];
      }
    });

    if (submissionPayload.roles) {
      submissionPayload.roles = Array.isArray(submissionPayload.roles) ? submissionPayload.roles : [submissionPayload.roles];
    }

    return submissionPayload;
  };

  const normalizeSelectFields = (submissionPayload) => {
    if (!Array.isArray(fields)) return;

    fields.forEach((f) => {
      if (f.type !== "select" || f.name === "roles" || f.multiple) return;
      const hasDot = f.name.includes(".");
      const [parent, child] = hasDot ? f.name.split(".") : [null, null];
      const val = hasDot ? submissionPayload[parent]?.[child] : submissionPayload[f.name];

      if (val !== undefined && val !== null && val !== "") {
        const normalizedValue = Array.isArray(val) ? val[0] : val;
        if (hasDot) {
          submissionPayload[parent][child] = normalizedValue;
        } else {
          submissionPayload[f.name] = normalizedValue;
        }
      }
    });
  };

  const onSubmit = async (formData) => {
    try {
      setLoading(true);
      setSubmitError("");
      setSubmitSuccess("");

      const submissionPayload = getSubmissionPayload(formData);
      normalizeSelectFields(submissionPayload);

      const token = localStorage.getItem("token");
      const url = `${API_BASE}/${apiRoute}/${method === "add" ? "add" : "update"}`;

      const response = await fetch(url, {
        method: method === "add" ? "POST" : "PUT",
        headers: { Authorization: `Bearer ${token}`, "Content-Type": "application/json" },
        body: JSON.stringify(submissionPayload),
      });

      const responseText = await response.text();
      let resData = {};
      let isExplicitFailure = false;

      if (responseText) {
        try {
          resData = JSON.parse(responseText);
          if (resData === false || resData?.status === false || resData?.success === false) isExplicitFailure = true;
        } catch {
          resData = { message: responseText };
          const textLower = responseText.toLowerCase();
          if (responseText === "false" || textLower.includes("fail") || textLower.includes("already exist")) isExplicitFailure = true;
        }
      }

      if (!response.ok || isExplicitFailure) {
        setSubmitError(resData?.message || (typeof resData === "string" ? resData : null) || responseText || `Operation rejected by registry schema variables.`);
        return;
      }

      setSubmitSuccess("Saved successfully.");
      globalThis.dispatchEvent(new Event("nodeDataChanged"));
      if (closeModal) setTimeout(closeModal, 800);
    } catch (err) {
      setSubmitError(err.message || "Something went wrong while saving.");
    } finally {
      setLoading(false);
    }
  };

  const getIcon = useCallback((name) => {
    if (name === "identifier") return <Squares2X2Icon className="absolute left-3.5 top-1/2 -translate-y-1/2 w-[18px] h-[18px] text-[#b0b0c8] pointer-events-none z-10" />;
    if (name === "path") return <LinkIcon className="absolute left-3.5 top-1/2 -translate-y-1/2 w-[18px] h-[18px] text-[#b0b0c8] pointer-events-none z-10" />;
    if (name === "roles") return <ShieldCheckIcon className="absolute left-3.5 top-6 w-[18px] h-[18px] text-[#b0b0c8] pointer-events-none z-10" />;
    return null;
  }, []);

  const systemAuditSection = useMemo(() => {
    if (method !== "update" || !data) return null;
    const auditDate = (d) => d ? new Date(d).toLocaleString() : "—";
    return (
      <div className="bg-[#f8f8fc] border border-[#ebebf5] rounded-xl p-6 space-y-4">
        <h3 className="text-xs font-bold text-[#2d2d6e] uppercase tracking-wider border-b border-[#ebebf5] pb-2">System Audit Records</h3>
        <div className="space-y-3">
          <div className="flex gap-3 text-xs"><UserIcon className="w-4 h-4 text-[#b0b0c8] mt-0.5" /><div><span className="block font-semibold text-[#8888a0]">Created By</span><span className="text-[#2d2d6e] font-mono">{data.createdBy || data.created_by || "System Generated"}</span></div></div>
          <div className="flex gap-3 text-xs"><ClockIcon className="w-4 h-4 text-[#b0b0c8] mt-0.5" /><div><span className="block font-semibold text-[#8888a0]">Created Date</span><span className="text-[#2d2d6e] font-mono">{auditDate(data.createdOn || data.created_on || data.createdDate)}</span></div></div>
          <div className="border-t border-[#ebebf5] my-2 pt-2" />
          <div className="flex gap-3 text-xs"><UserIcon className="w-4 h-4 text-[#b0b0c8] mt-0.5" /><div><span className="block font-semibold text-[#8888a0]">Modified By</span><span className="text-[#2d2d6e] font-mono">{data.modifiedBy || data.modified_by || "—"}</span></div></div>
          <div className="flex gap-3 text-xs"><ClockIcon className="w-4 h-4 text-[#b0b0c8] mt-0.5" /><div><span className="block font-semibold text-[#8888a0]">Last Modified Date</span><span className="text-[#2d2d6e] font-mono">{auditDate(data.modifiedOn || data.modified_on || data.modifiedDate)}</span></div></div>
        </div>
      </div>
    );
  }, [method, data]);

  return (
    <div className="w-full bg-white border border-[#ebebf5] rounded-2xl font-sans box-border">
      <div className="flex items-center justify-between px-8 py-6 border-b border-[#ebebf5]">
        <h2 className="text-lg font-semibold text-[#2d2d6e] capitalize">{method}<span className="text-[#6c63ff] ml-1">{title}</span></h2>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="p-8 flex flex-col gap-5">
        <div className={`grid grid-cols-1 ${method === "update" ? "md:grid-cols-2 gap-5" : "max-w-xl"} items-start`}>
          <div className="flex flex-col gap-5 w-full max-h-[60vh] overflow-y-auto pr-2">
            {Array.isArray(fields) && fields.map((field) => {
              const icon = getIcon(field.name);
              const isFieldReadOnly = field.readOnly || (method === "update" && field.name === "identifier");
              const fieldError = errors[field.name];

              return (
                <div key={field.name} className="flex flex-col gap-1.5">
                  <label className="text-xs font-medium text-[#8888a0] capitalize">{field.placeholder || field.name}</label>
                  <div className="relative w-full">
                    {icon}
                    {(field.type === "text" || field.type === "number" || field.type === "email" || field.type === "password") && (
                      <div className="flex flex-col gap-1 w-full">
                        <input
                          type={field.type === "number" ? "text" : field.type}
                          {...register(field.name, field.validation || { required: field.required })}
                          placeholder={field.placeholder}
                          readOnly={isFieldReadOnly}
                          onInput={(e) => { if (field.type === "number") e.target.value = e.target.value.replaceAll(/[^0-9.]/g, ""); }}
                          className={`w-full h-11 bg-[#f8f8fc] border rounded-lg px-3.5 text-sm text-[#2d2d6e] outline-none transition-all focus:bg-white placeholder-[#b0b0c8] read-only:bg-[#f4f5fa] read-only:text-[#8888a0] read-only:cursor-not-allowed ${fieldError ? "border-rose-400 focus:border-rose-500" : "border-[#ebebf5] focus:border-[#6c63ff]"} ${icon ? "pl-11" : ""}`}
                        />
                        {fieldError && <span className="text-[11px] text-rose-500 font-medium pl-1">{fieldError.message}</span>}
                      </div>
                    )}

                    {field.type === "select" && (
                      <div className="flex flex-col gap-1 w-full">
                        <select
                          {...register(field.name, field.validation || { required: field.required })}
                          multiple={field.multiple || false}
                          disabled={isFieldReadOnly}
                          className={`w-full bg-[#f8f8fc] border rounded-lg text-sm text-[#2d2d6e] outline-none transition-all focus:bg-white disabled:bg-[#f4f5fa] disabled:text-[#8888a0] disabled:cursor-not-allowed ${field.multiple
                              ? "h-auto min-h-[130px] p-3.5"
                              : "h-11 px-3.5 pr-10 appearance-none bg-[url('data:image/svg+xml;charset=UTF-8,%3csvg xmlns=%27http://www.w3.org/2000/svg%27 viewBox=%270 0 24 24%27 fill=%27none%27 stroke=%27%23b0b0c8%27 stroke-width=%272%27 stroke-linecap=%27round%27 stroke-linejoin=%27round%27%3e%3cpolyline points=%276 9 12 15 18 9%27%3e%3c/polyline%3e%3c/svg%3e')] bg-no-repeat bg-[position:right_14px_center] bg-[size:16px]"
                            } ${fieldError
                              ? "border-rose-400 focus:border-rose-500"
                              : "border-[#ebebf5] focus:border-[#6c63ff]"
                            } ${icon ? "pl-11" : ""}`}
                        >
                          {!field.multiple && <option value="">Select Option</option>}

                          {(field.hardCoded === "true"
                            ? field.hardCodedArray
                            : dropdownData[field.dataKey] || []
                          ).map((item) => {
                            const value =
                              field.hardCoded === "true"
                                ? item
                                : item.identifier;

                            return (
                              <option key={value} value={value}>
                                {value}
                              </option>
                            );
                          })}
                        </select>
                        {fieldError && <span className="text-[11px] text-rose-500 font-medium pl-1">{fieldError.message}</span>}
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
          <div>{systemAuditSection}</div>
        </div>

        {submitSuccess && <div className="bg-[#f2fdf5] border border-[#d3f9df] text-[#10b981] text-xs rounded-lg p-3 text-center">{submitSuccess}</div>}
        {submitError && <div className="bg-[#fff2f2] border border-[#ffd6d6] text-[#e55555] text-xs rounded-lg p-3 text-center">{submitError}</div>}

        <div className="flex items-center justify-end gap-3 mt-3 border-t border-[#ebebf5] pt-6">
          <button type="button" onClick={closeModal} className="h-10 px-4 bg-white border border-[#ebebf5] rounded-lg text-[#4b4b75] text-xs font-medium cursor-pointer transition-all hover:border-[#b0b0c8] hover:text-[#2d2d6e]">Cancel</button>
          <button type="submit" disabled={loading} className="h-10 px-5 bg-[#6c63ff] border-none rounded-lg text-white text-xs font-medium cursor-pointer transition-all hover:bg-[#5850ec] disabled:opacity-60 disabled:cursor-not-allowed">{loading ? "Submitting..." : "Save Changes"}</button>
        </div>
      </form>
    </div>
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
      validation: PropTypes.object
    })
  ),
  apiRoute: PropTypes.string.isRequired,
  dropdownApis: PropTypes.object,
  method: PropTypes.oneOf(["add", "update"]).isRequired,
  identifier: PropTypes.string,
  closeModal: PropTypes.func.isRequired,
};

export default CommonEdit;