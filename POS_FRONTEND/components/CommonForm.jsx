"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";

const hasFieldValue = (field, value) => {
  if (field.type === "select") {
    if (Array.isArray(value)) {
      return value.length > 0;
    }

    return Boolean(value);
  }

  return String(value ?? "").trim().length > 0;
};

const applyRule = (rule, field, value, form) => {
  if (rule.type === "pattern" && rule.value && !rule.value.test(String(value ?? ""))) {
    return rule.message || `${field.label || field.name} is invalid`;
  }

  if (rule.type === "minLength" && String(value ?? "").length < rule.value) {
    return rule.message || `${field.label || field.name} must be at least ${rule.value} characters`;
  }

  if (rule.type === "maxLength" && String(value ?? "").length > rule.value) {
    return rule.message || `${field.label || field.name} must be at most ${rule.value} characters`;
  }

  if (rule.type === "custom" && typeof rule.validator === "function") {
    return rule.validator(value, form) || "";
  }

  return "";
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

  const getListRoute = () => {
    if (!api) return "/dashboard";

    const parts = api.split("/").filter(Boolean);
    const filtered = parts.filter(p => p !== "api");
    const module = filtered[filtered.length - 1] || "dashboard";

    return `/dashboard/${module}`;
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
        .then(res => setForm(res.data || {}))
        .catch(console.log);
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

            let data = res.data?.content || res.data;
            temp[f.name] = Array.isArray(data) ? data : [];
          } catch {
            temp[f.name] = [];
          }
        }
      }

      setDropdownData(temp);
    };

    load();
  }, [fields]);

  const handleChange = (name, value) => {
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const normalizeArray = (value) => {
    if (!value) return [];
    if (Array.isArray(value)) return value;
    return [value];
  };

  const validateField = (field, value) => {
    if (field.required && !hasFieldValue(field, value)) {
      return `${field.label || field.name} is required`;
    }

    const rules = field.rules || [];

    for (const rule of rules) {
      const ruleMessage = applyRule(rule, field, value, form);

      if (ruleMessage) {
        return ruleMessage;
      }
    }

    if (field.name === "phoneNo" && value && !/^\d{10}$/.test(String(value))) {
      return "Phone must be 10 digits";
    }

    if (field.name === "password" && value && String(value).length < 6) {
      return "Password must be at least 6 characters";
    }

    return "";
  };

  const validate = () => {
    const temp = {};

    fields.forEach((field) => {
      const message = validateField(field, form[field.name]);

      if (message) {
        temp[field.name] = message;
      }
    });

    setErrors(temp);
    return Object.keys(temp).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError("");

    if (!validate()) return;

    try {
      setLoading(true);

      const url =
        mode === "edit"
          ? `${api}/update`
          : `${api}/add`;

      const res = await axios.post(url, form);

      if (res.data?.success === false) {
        setServerError(res.data.message);
        return;
      }

      if (
        api === "/user" &&
        mode === "edit"
      ) {
        const oldUsername =
          localStorage.getItem("username");

        if (
          form.username &&
          oldUsername !== form.username
        ) {
          alert(
            "Email updated successfully. Please login with your new email."
          );

          localStorage.clear();

          router.push("/login");

          return;
        }
      }

      alert(
        mode === "add"
          ? "Created successfully"
          : "Updated successfully"
      );

      router.push(getListRoute());

    } catch (err) {
      console.log(err);
      setServerError("Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  const renderField = (field) => {
    const value = form[field.name] ?? (field.multiple ? [] : "");

    if (field.type === "text") {
      return (
        <>
          <input
            id={field.name}
            value={value || ""}
            onChange={(e) => handleChange(field.name, e.target.value)}
            readOnly={field.readOnly || (mode === "edit" && field.name === "identifier")}
            className={`w-full px-3 py-2 border rounded-lg ${field.readOnly || (mode === "edit" && field.name === "identifier")
              ? "bg-gray-100 text-gray-700 cursor-not-allowed"
              : "bg-white text-black"
              }`}
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
      const options = field.options || dropdownData[field.name] || [];
      const isMultiple = Boolean(field.multiple);
      const isOpen = openDropdown === field.name;
      const selected = normalizeArray(form[field.name]);

      if (!isMultiple) {
        return (
          <>
            <select
              id={field.name}
              value={form[field.name] ?? ""}
              onChange={(e) => handleChange(field.name, e.target.value)}
              className="w-full px-3 py-2 border rounded-lg bg-white text-black"
            >
              <option value="">Select</option>
              {options.map((opt) => {
                const value = opt.identifier || opt.name || "";
                return (
                  <option key={value} value={value}>
                    {opt.name || opt.identifier || value}
                  </option>
                );
              })}
            </select>
            {errors[field.name] && (
              <p className="text-red-500 text-sm mt-1">
                {errors[field.name]}
              </p>
            )}
          </>
        );
      }

      const toggle = (val) => {
        const updated = selected.includes(val)
          ? selected.filter(v => v !== val)
          : [...selected, val];

        handleChange(field.name, updated);
      };

      return (
        <div className="relative">

          <button
            id={field.name}
            type="button"
            onClick={() => setOpenDropdown(isOpen ? null : field.name)}
            className="w-full border rounded-lg px-3 py-2 bg-white cursor-pointer flex justify-between text-left"
          >
            <span>
              {selected.length > 0 ? selected.join(", ") : "Select"}
            </span>
            <span>▾</span>
          </button>

          {isOpen && (
            <div className="absolute z-50 mt-2 w-full bg-white border rounded-lg shadow-lg max-h-60 overflow-auto">

              {options.map((opt) => {
                const val = opt.identifier || opt.name;
                const checked = selected.includes(val);

                return (
                  <button
                    key={val || opt.name || opt.identifier}
                    type="button"
                    onClick={() => toggle(val)}
                    className="w-full px-3 py-2 hover:bg-gray-100 flex gap-2 cursor-pointer text-left"
                  >
                    <input type="checkbox" checked={checked} readOnly />
                    <span>{opt.name || opt.identifier}</span>
                  </button>
                );
              })}

            </div>
          )}

          {errors[field.name] && (
            <p className="text-red-500 text-sm mt-1">
              {errors[field.name]}
            </p>
          )}
        </div>
      );
    }

    if (field.type === "password") {
      return (
        <>
          <input
            id={field.name}
            type="password"
            value={value || ""}
            onChange={(e) => handleChange(field.name, e.target.value)}
            className="w-full px-3 py-2 border rounded-lg bg-white text-black"
          />

          {errors[field.name] && (
            <p className="text-red-500 text-sm mt-1">
              {errors[field.name]}
            </p>
          )}
        </>
      );
    }
    return null;
  };

  return (
    <form onSubmit={handleSubmit} className="grid grid-cols-2 gap-5">

      {serverError && (
        <div className="col-span-2 p-3 bg-red-50 border border-red-200 text-red-600 rounded-lg">
          {serverError}
        </div>
      )}

      {fields.map((f) => (
        <div key={f.name || f.label} className="flex flex-col gap-1">
          <label htmlFor={f.name} className="text-sm text-gray-600">
            {f.label}
          </label>
          {renderField(f)}
        </div>
      ))}

      <div className="col-span-2 flex justify-end gap-3 mt-6 pt-4 border-t">

        <button
          type="button"
          onClick={() => router.push(getListRoute())}
          className="px-5 py-2 rounded-lg bg-gray-200"
        >
          Cancel
        </button>

        <button
          type="submit"
          disabled={loading}
          className="px-5 py-2 rounded-lg bg-slate-900 text-white disabled:opacity-50"
        >
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
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string,
      type: PropTypes.string,
      required: PropTypes.bool,
      readonly: PropTypes.bool,
      multiple: PropTypes.bool,
      api: PropTypes.string,
      options: PropTypes.array,
      rules: PropTypes.arrayOf(
        PropTypes.shape({
          type: PropTypes.oneOf(["pattern", "minLength", "maxLength", "custom"]).isRequired,
          value: PropTypes.oneOfType([PropTypes.number, PropTypes.instanceOf(RegExp)]),
          message: PropTypes.string,
          validator: PropTypes.func,
        })
      ),
    })
  ),
};