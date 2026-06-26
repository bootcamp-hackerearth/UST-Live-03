"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";

export default function CommonForm({
  title = "Item",
  mode,
  fields,
  data,
  onSubmit,
  onClose,
  validate,
}) {
  const [options, setOptions] = useState({});
  const isEdit = mode === "edit";

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    defaultValues: {},
  });

  const getNestedValue = (obj, path) => {
    if (!obj || !path) return "";
    return path.split(".").reduce((acc, part) => acc?.[part], obj) ?? "";
  };

  useEffect(() => {
    if (!fields) return;

    const init = {};

    if (data?.id != null) {
      init.id = data.id;
    }

    fields.forEach((f) => {
      if (f.hidden) return;

      const rawValue = data && f.name.includes(".") ? getNestedValue(data, f.name) : data?.[f.name];

      if (f.type === "multiselect") {
        init[f.name] = Array.isArray(rawValue)
          ? rawValue.map((x) => (typeof x === "object" ? x.identifier : x))
          : [];
      } else if (f.type === "select") {
        init[f.name] = typeof rawValue === "object" ? rawValue?.identifier : rawValue ?? "";
      } else {
        init[f.name] = rawValue ?? "";
      }
    });

    reset(init);
  }, [data, fields, reset]);

  useEffect(() => {
    const loadOptions = async () => {
      for (const field of fields || []) {
        if (field.type === "select" || field.type === "multiselect") {
          if (field.options) continue;

          try {
            const res = await fetch(field.apiUrl, {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem("token")}`,
              },
              body: JSON.stringify({ page: 0, sizePerPage: 100 }),
            });

            if (!res.ok) throw new Error("Failed to fetch options from API");

            const contentType = res.headers.get("content-type") || "";
            if (!contentType.includes("application/json")) throw new Error("Invalid content type in response");

            const result = await res.json();
            const list = result.content || result.dtoList || result.data || result || [];

            setOptions((prev) => ({
              ...prev,
              [field.name]: Array.isArray(list) ? list : [],
            }));
          } catch (error) {
            console.error(error);
          }
        }
      }
    };

    loadOptions();
  }, [fields]);

  const formatDate = (date) => {
    if (!date) return "-";
    return new Date(date).toLocaleString("en-GB", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 font-sans">
      <div className="w-105 bg-white rounded-2xl relative flex flex-col max-h-[90vh] shadow-2xl border border-neutral-100 overflow-hidden text-neutral-900">

        <div className="px-6 py-4 border-b border-neutral-100 flex items-center justify-between bg-neutral-50/50 flex-shrink-0 text-left">
          <div>
            <h3 className="text-base font-bold text-neutral-900 tracking-tight">
              {mode === "add" ? `Add New ${title}` : `Modify ${title} Context`}
            </h3>
            <p className="text-[11px] text-neutral-400 font-medium mt-0.5">Please populate the tracking entries correctly</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="w-7 h-7 flex items-center justify-center rounded-lg border border-neutral-200 text-neutral-400 hover:text-neutral-600 transition-colors text-lg font-medium"
          >
            &times;
          </button>
        </div>

        <div className="overflow-y-auto flex-1 custom-scrollbar text-left">
          {isEdit && (
            <div className="border-b border-neutral-200/60 bg-neutral-50/50 px-6 py-4 sticky top-0 z-10 space-y-2">
              <h4 className="text-[10px] font-bold uppercase tracking-wider text-neutral-400">Audit Footprint Data</h4>
              <div className="grid grid-cols-2 gap-x-4 gap-y-1.5 text-xs text-neutral-600">
                <div>
                  <span className="font-medium text-neutral-400">Created By:</span>{" "}
                  <span className="font-semibold text-neutral-700">{data?.createdBy ?? data?.created_by ?? "-"}</span>
                </div>
                <div>
                  <span className="font-medium text-neutral-400">Created On:</span>{" "}
                  <span className="font-mono text-neutral-700 font-medium">{formatDate(data?.createdOn ?? data?.created_by ?? data?.created_on)}</span>
                </div>
                <div>
                  <span className="font-medium text-neutral-400">Modified By:</span>{" "}
                  <span className="font-semibold text-neutral-700">{data?.modifiedBy ?? data?.modified_by ?? "-"}</span>
                </div>
                <div>
                  <span className="font-medium text-neutral-400">Modified On:</span>{" "}
                  <span className="font-mono text-neutral-700 font-medium">{formatDate(data?.modifiedOn ?? data?.modified_by ?? data?.modified_on)}</span>
                </div>
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-4">
            {fields.map((f) => {
              if (f.hidden) return null;

              const disabled = isEdit && (f.disableOnEdit || f.name === "identifier");
              const inputClass = "w-full h-10 rounded-xl border border-neutral-200 bg-neutral-50/50 px-3.5 text-xs text-neutral-800 outline-none focus:border-neutral-900 focus:bg-white transition-all focus:ring-2 focus:ring-neutral-900/5 placeholder-neutral-400";
              const fieldId = `common-form-${f.name}`;

              const fieldValidationRules = {
                required: f.required ? "This field is required" : false,
                ...(validate?.[f.name]),
              };

              if (f.type === "select") {
                const selectOptions = f.options || options[f.name] || [];

                return (
                  <div key={f.name} className="space-y-1">
                    <label htmlFor={fieldId} className="block text-[10px] font-bold uppercase tracking-tight text-neutral-400 ml-1">
                      {f.placeholder || f.label}
                    </label>

                    <select
                      id={fieldId}
                      disabled={disabled}
                      className={inputClass + " font-medium" + (disabled ? " bg-neutral-100 text-neutral-400 cursor-not-allowed" : "")}
                      {...register(f.name, fieldValidationRules)}
                    >
                      <option value="">Select Option</option>
                      {selectOptions.map((opt) => {
                        const optionValue = opt.value ?? opt.identifier ?? opt;
                        return (
                          <option key={optionValue} value={optionValue}>
                            {opt.label ?? opt.identifier ?? opt}
                          </option>
                        );
                      })}
                    </select>

                    {errors[f.name] && <p className="text-rose-600 text-[10px] ml-1 font-semibold">{errors[f.name]?.message}</p>}
                  </div>
                );
              }

              if (f.type === "multiselect") {
                return (
                  <div key={f.name} className="space-y-1">
                    <label htmlFor={fieldId} className="block text-[10px] font-bold uppercase tracking-tight text-neutral-400 ml-1">
                      {f.placeholder || f.label}
                    </label>

                    <select
                      id={fieldId}
                      multiple
                      disabled={disabled}
                      className={inputClass + " h-24 font-medium py-2" + (disabled ? " bg-neutral-100 text-neutral-400 cursor-not-allowed" : "")}
                      {...register(f.name, fieldValidationRules)}
                    >
                      {(options[f.name] || []).map((opt) => {
                        const optionValue = opt.value ?? opt.identifier ?? opt;
                        return (
                          <option key={optionValue} value={optionValue}>
                            {opt.label ?? opt.identifier ?? opt}
                          </option>
                        );
                      })}
                    </select>

                    {errors[f.name] && <p className="text-rose-600 text-[10px] ml-1 font-semibold">{errors[f.name]?.message}</p>}
                  </div>
                );
              }

              return (
                <div key={f.name} className="space-y-1">
                  <label htmlFor={fieldId} className="block text-[10px] font-bold uppercase tracking-tight text-neutral-400 ml-1">
                    {f.label || f.placeholder}
                  </label>

                  <input
                    id={fieldId}
                    type={f.type || "text"}
                    disabled={disabled}
                    placeholder={f.placeholder || f.label}
                    className={inputClass + " font-semibold" + (disabled ? " bg-neutral-100 text-neutral-400 cursor-not-allowed" : "")}
                    {...register(f.name, fieldValidationRules)}
                  />

                  {errors[f.name] && <p className="text-rose-600 text-[10px] ml-1 font-semibold">{errors[f.name]?.message}</p>}
                </div>
              );
            })}

            <div className="flex justify-end gap-2 pt-4 border-t border-neutral-100 bg-white sticky bottom-0 z-10 mt-5">
              <button
                type="button"
                onClick={onClose}
                className="px-4 h-9 border border-neutral-200 hover:bg-neutral-50 text-neutral-600 font-semibold rounded-xl text-xs transition-all active:scale-[0.98]"
              >
                Cancel
              </button>

              <button
                type="submit"
                className="px-5 h-9 bg-neutral-900 hover:bg-neutral-800 text-white font-semibold rounded-xl text-xs transition-all active:scale-[0.98] shadow-sm"
              >
                {mode === "add" ? "Save Record" : "Apply Updates"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

CommonForm.propTypes = {
  title: PropTypes.string,
  mode: PropTypes.string.isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      placeholder: PropTypes.string,
      type: PropTypes.string,
      hidden: PropTypes.bool,
      disableOnEdit: PropTypes.bool,
      options: PropTypes.array,
      apiUrl: PropTypes.string,
      label: PropTypes.string,
      required: PropTypes.oneOfType([PropTypes.bool, PropTypes.func]),
    })
  ).isRequired,
  data: PropTypes.object,
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
  validate: PropTypes.object,
};