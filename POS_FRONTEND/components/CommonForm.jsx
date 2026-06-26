"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";
import { ChevronDown } from "lucide-react";
import NotFound from "@/components/NotFound";
import AccessDenied from "@/components/AccessDenied";

const isEmptyValue = (value) => {
  return value === undefined || value === null || value === "" || (Array.isArray(value) && value.length === 0);
};

const isPhoneField = (field) => {
  const fieldName = field.name?.toLowerCase() || "";
  return field.type === "tel" || fieldName.includes("phone") || fieldName.includes("contactnumber");
};

const normalizeFieldData = (data) => {
  const normalizedData = { ...data };

  Object.keys(normalizedData).forEach((key) => {
    if (Array.isArray(normalizedData[key])) {
      normalizedData[key] = normalizedData[key].map((item) =>
        typeof item === "object" ? item.identifier || item.name : item
      );
    }
  });

  return normalizedData;
};

const validateRule = (rule, value) => {
  if (rule.type === "minLength") {
    return String(value || "").length < rule.value ? rule.message : "";
  }

  if (rule.type === "maxLength") {
    return String(value || "").length > rule.value ? rule.message : "";
  }

  if (rule.type === "pattern") {
    return value && !rule.value.test(String(value)) ? rule.message : "";
  }

  if (rule.type === "custom") {
    return rule.validator?.(value) || "";
  }

  return "";
};

const validateField = (field, value) => {
  if (field.required && isEmptyValue(value)) {
    return `${field.label} is required`;
  }

  if (isPhoneField(field) && value && !/^\d{10}$/.test(value)) {
    return `${field.label} must contain exactly 10 digits`;
  }

  if (!field.rules?.length) {
    return "";
  }

  for (const rule of field.rules) {
    const error = validateRule(rule, value);
    if (error) {
      return error;
    }
  }

  return "";
};

const toggleMultiSelectValue = (currentValues, valueToToggle) => {
  return currentValues.includes(valueToToggle)
    ? currentValues.filter((value) => value !== valueToToggle)
    : [...currentValues, valueToToggle];
};

export default function CommonForm({
  api,
  mode = "add",
  identifier,
  identifierParam = "identifier",
  fields = []
}) {
  const router = useRouter();

  const [form, setForm] = useState({});
  const [dropdownData, setDropdownData] = useState({});
  const [openDropdown, setOpenDropdown] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [notFound, setNotFound] = useState(false);
  const [accessDenied, setAccessDenied] = useState(false);

  const getListRoute = () => {
    if (!api) return "/dashboard";
    const parts = api.split("/").filter(Boolean);
    return `/dashboard/${parts[parts.length - 1]}`;
  };

  useEffect(() => {
    const init = {};
    fields.forEach(f => {
      init[f.name] = f.multiple ? [] : "";
    });
    setForm(init);
  }, [fields]);

  useEffect(() => {
    if (mode === "edit" && identifier) {
      axios
        .get(`${api}/get?${identifierParam}=${identifier}`)
        .then((res) => {
          setForm(normalizeFieldData(res.data || {}));
          setNotFound(false);
        })
        .catch((err) => {
          if (err.response?.status === 404) {
            setNotFound(true);
          } else if (err.response?.status === 403) {
            setAccessDenied(true);
          } else {
            console.log(err);
          }
        });
    }
  }, [mode, identifier, api, identifierParam]);

  useEffect(() => {
    const load = async () => {
      const temp = {};
      for (const f of fields) {
        if (f.api) {
          try {
            const res = await axios.post(f.api, {
              page: 0,
              sizePerPage: 50
            });
            temp[f.name] = res.data?.content || [];
          } catch (err) {
            if (err.response?.status === 403) {
              setAccessDenied(true);
            }
            temp[f.name] = [];
          }
        }
      }
      setDropdownData(temp);
    };
    load();
  }, [fields]);

  const handleChange = (name, value) => {
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleMultipleSelectChange = (name, currentValues, valueToToggle) => {
    handleChange(name, toggleMultiSelectValue(currentValues, valueToToggle));
  };

  const validate = () => {
    const temp = {};

    fields.forEach((field) => {
      const error = validateField(field, form[field.name]);
      if (error) {
        temp[field.name] = error;
      }
    });

    setErrors(temp);
    return Object.keys(temp).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validate()) return;

    try {
      setLoading(true);

      const url =
        mode === "edit"
          ? `${api}/update`
          : `${api}/add`;

      const res =
        mode === "edit"
          ? await axios.put(url, form)
          : await axios.post(url, form);

      if (res.data?.success === false) {
        setServerError(res.data.message);
        return;
      }

      alert(
        mode === "edit"
          ? "Updated successfully"
          : "Created successfully"
      );

      router.push(getListRoute());

    } catch (err) {
      if (err.response?.status === 403) {
        setAccessDenied(true);
        return;
      }
      setServerError("Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  const renderField = (field) => {
    const value = form[field.name] ?? (field.multiple ? [] : "");

    if (field.type === "text" || field.type === "password" || field.type === "email" || field.type === "tel") {
      return (
        <>
          <input
            type={field.type}
            value={value}
            onChange={(e) => handleChange(field.name, e.target.value)}
            readOnly={field.readOnly}
            className="w-full px-4 py-3 border border-slate-300 rounded-xl outline-none focus:ring-2 focus:ring-black focus:border-black"
          />

          {errors[field.name] && (
            <p className="text-red-500 text-sm mt-1">
              {errors[field.name]}
            </p>
          )}
        </>
      );
    }

    if (field.type === "select") {
      const options = (field.options || dropdownData[field.name] || [])
        .filter(opt => opt.status === true);

      if (!field.multiple) {
        return (
          <select
            value={value}
            onChange={(e) => handleChange(field.name, e.target.value)}
            className="w-full px-3 py-2 border rounded-lg"
          >
            <option value="">Select</option>
            {options.map(opt => (
              <option key={opt.identifier} value={opt.identifier}>
                {opt.name || opt.identifier}
              </option>
            ))}
          </select>
        );
      }

      const selected = value || [];
      const isOpen = openDropdown === field.name;

      return (
        <div className="relative">
          <button
            type="button"
            onClick={() => setOpenDropdown(isOpen ? null : field.name)}
            className="w-full border rounded-lg px-3 py-2 bg-white flex justify-between"
          >
            <span>
              {selected.length ? selected.join(", ") : "Select"}
            </span>
            <span><ChevronDown size={18} /></span>
          </button>

          {isOpen && (
            <div className="absolute z-50 mt-2 w-full bg-white border rounded-lg shadow max-h-60 overflow-auto">
              {options.map(opt => {
                const val = opt.identifier;
                const checked = selected.includes(val);

                return (
                  <label key={val} className="flex gap-2 px-3 py-2 hover:bg-gray-100">
                    <input
                      type="checkbox"
                      checked={checked}
                      onChange={() => handleMultipleSelectChange(field.name, selected, val)}
                    />
                    {opt.name || opt.identifier}
                  </label>
                );
              })}
            </div>
          )}
        </div>
      );
    }

    return null;
  };

  if (notFound) {
    return <NotFound />;
  }

  if (accessDenied) {
    return <AccessDenied />;
  }

  return (
    <form onSubmit={handleSubmit} className="grid grid-cols-2 gap-5">
      {fields.map(f => (
        <div key={f.name} className="flex flex-col">
          <label className="text-sm">{f.label}</label>
          {renderField(f)}
        </div>
      ))}

      {serverError && (
        <div className="col-span-2 text-red-500">{serverError}</div>
      )}

      {mode === "edit" && (
        <div className="col-span-2 mt-6 bg-gray-50 p-4 rounded text-xs grid grid-cols-2 gap-4">
          <div>Created By: {form.createdBy || "-"}</div>
          <div>Created On: {form.createdOn ? new Date(form.createdOn).toLocaleString() : "-"}</div>
          <div>Modified By: {form.modifiedBy || "-"}</div>
          <div>Modified On: {form.modifiedOn ? new Date(form.modifiedOn).toLocaleString() : "-"}</div>
        </div>
      )}

      <div className="col-span-2 flex justify-end gap-3 mt-4">
        <button type="button" onClick={() => router.back()} className="px-4 py-2 bg-gray-200 rounded">
          Cancel
        </button>

        <button type="submit" disabled={loading} className="px-4 py-2 bg-black text-white rounded">
          {loading ? "Saving..." : "Save"}
        </button>
      </div>
    </form>
  );
}

CommonForm.propTypes = {
  api: PropTypes.string,
  mode: PropTypes.oneOf(["add", "edit"]),
  identifier: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  identifierParam: PropTypes.string,
  fields: PropTypes.array
};