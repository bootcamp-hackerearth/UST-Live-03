"use client";

import { useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "../../components/Axios";
import MultiDropDown from "../../components/MultiDropDown";

const labelClass = "text-xs md:text-sm font-semibold text-gray-600";
const inputBaseClass = "py-2 px-3 border-[1.5px] rounded-lg text-sm outline-none bg-[#fafaf8] box-border w-full transition-all focus:border-brand";
const errorTextClass = "text-[11px] text-[#e53e3e] mt-0.5";
const colSpanClass = "flex flex-col gap-1.25 col-span-1 md:col-span-2";
const alertPanelClass = "border rounded-lg p-2.5 md:p-3.5 text-xs md:text-sm mb-4 text-center md:col-span-2";

function FormField({ id, label, error, children }) {
  return (
    <div className="flex flex-col gap-1.25">
      <label className={labelClass} htmlFor={id}>{label}</label>
      {children}
      {error && <span className={errorTextClass}>{error}</span>}
    </div>
  );
}

FormField.propTypes = {
  id: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  error: PropTypes.string,
  children: PropTypes.node.isRequired,
};

FormField.defaultProps = {
  error: "",
};

function TextInput({ name, fieldErrors, ...props }) {
  return (
    <input
      name={name}
      className={`${inputBaseClass} ${fieldErrors[name] ? "border-[#e53e3e]" : "border-gray-300"}`}
      {...props}
    />
  );
}

TextInput.propTypes = {
  name: PropTypes.string.isRequired,
  fieldErrors: PropTypes.objectOf(PropTypes.string).isRequired,
};

export default function AddUser() {
  const router = useRouter();
  const [form, setForm] = useState({ name: "", username: "", phoneNo: "", password: "" });
  const [roles, setRoles] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const MIN_SECRET_LENGTH = 4;
  const SECRET_REQUIRED_MSG = "This field is required.";
  const SECRET_LENGTH_MSG = `Must be at least ${MIN_SECRET_LENGTH} characters.`;

  function handleChange(e) {
    const { name, value } = e.target;
    if (name === "phoneNo") {
      const digitsOnly = value.replaceAll(/\D/g, "");
      if (digitsOnly.length > 10) return;
      setForm((prev) => ({ ...prev, [name]: digitsOnly }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
    if (fieldErrors[name]) setFieldErrors((prev) => ({ ...prev, [name]: "" }));
    if (error) setError("");
   }

  function validate() {
    const errors = {};
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!form.name.trim()) errors.name = "Full name is required.";
    if (!form.username.trim()) {
      errors.username = "Email address is required.";
    } else if (!emailRegex.test(form.username.trim())) {
      errors.username = "Please enter a valid email address.";
    }
    if (!form.phoneNo.trim()) {
      errors.phoneNo = "Phone number is required.";
    } else if (form.phoneNo.trim().length !== 10) {
      errors.phoneNo = "Phone number must be exactly 10 digits.";
    }
    if (roles.length === 0) errors.roles = "Please select at least one role.";
    if (!form.password) {
      errors.password = SECRET_REQUIRED_MSG;
    } else if (form.password.length < MIN_SECRET_LENGTH) {
      errors.password = SECRET_LENGTH_MSG;
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
    }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    if (!validate()) return;
    setLoading(true);
    try {
      const res = await api.post("/user/register", {
        name: form.name.trim(),
        username: form.username.trim().toLowerCase(),
        phoneNo: form.phoneNo.trim(),
        password: form.password,
        roles,
      });
      const data = res.data;
      if (data.success === false) {
        setError(data.message || "Username already exists.");
        if (data.message?.toLowerCase().includes("username") || data.message?.toLowerCase().includes("email")) {
          setFieldErrors((prev) => ({ ...prev, username: data.message }));
        }
        return;
      }
      setSuccess("User added successfully!");
      setTimeout(() => router.back(), 1500);
    } catch (err) {
      setError(err.response?.data?.message || "Unable to connect to server.");
    } finally {
      setLoading(false);
    }
  }

  const handleRolesChange = useCallback((val) => {
    setRoles(val);
    setFieldErrors((prev) => ({ ...prev, roles: "" }));
  }, []);

  return (
    <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f9fafb] font-sans flex flex-col overflow-hidden">
      <div className="flex-1 p-5 md:p-6 flex flex-col overflow-hidden">
        <div className="flex items-center gap-3 mb-4 shrink-0 relative">
          <button
            type="button"
            className="py-2 px-4 bg-transparent text-brand border-[1.5px] border-brand rounded-lg text-xs md:text-sm font-semibold cursor-pointer shrink-0 transition-colors hover:bg-brand/5"
            onClick={() => router.back()}
          >
            &larr; Back
          </button>
          <h2 className="absolute left-1/2 -translate-x-1/2 m-0 text-xl font-bold text-brand whitespace-nowrap">
            Add User
          </h2>
        </div>
        <div className="flex-1 flex items-center justify-center overflow-hidden">
          <div className="bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.06)] p-6 md:p-8 w-full max-w-[720px] max-h-full overflow-auto">
            <p className="text-base md:text-[17px] font-bold text-[#1a1a1a] m-0 mb-1">New User</p>
            <p className="text-xs md:text-sm text-gray-400 mb-5">Fill in the details below</p>

            {error && (
              <div className={`${alertPanelClass} bg-[#fff5f5] border-[#fca5a5] text-[#c53030]`}>{error}</div>
            )}
            {success && (
              <div className={`${alertPanelClass} bg-[#f0fff4] border-[#9ae6b4] text-[#276749]`}>{success}</div>
            )}

            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-3.5">
              <FormField id="name" label="Full Name" error={fieldErrors.name}>
                <TextInput id="name" name="name" type="text" placeholder="Enter full name" value={form.name} onChange={handleChange} fieldErrors={fieldErrors} />
              </FormField>

              <FormField id="username" label="Email Address" error={fieldErrors.username}>
                <TextInput id="username" name="username" type="email" placeholder="Enter email address" value={form.username} onChange={handleChange} fieldErrors={fieldErrors} autoComplete="username" />
              </FormField>

              <FormField id="phoneNo" label="Phone Number" error={fieldErrors.phoneNo}>
                <TextInput id="phoneNo" name="phoneNo" type="text" inputMode="numeric" placeholder="10-digit mobile number" value={form.phoneNo} onChange={handleChange} fieldErrors={fieldErrors} />
              </FormField>

              <FormField id="password" label="Password" error={fieldErrors.password}>
                <TextInput id="password" name="password" type="password" placeholder="Min. 4 characters" value={form.password} onChange={handleChange} fieldErrors={fieldErrors} autoComplete="new-password" />
              </FormField>

              <div className={colSpanClass}>
                <MultiDropDown
                  label="Assign Role(s)"
                  apiUrl="/role/findByStatus"
                  valueField="identifier"
                  labelField="identifier"
                  selectedValues={roles}
                  onChange={handleRolesChange}
                />
                {fieldErrors.roles && <span className={errorTextClass}>{fieldErrors.roles}</span>}
              </div>

              <div className="flex flex-row gap-3 mt-2 col-span-1 md:col-span-2">
                <button
                  type="button"
                  className="flex-1 p-2.5 bg-gray-100 text-gray-600 border-none rounded-lg text-sm font-semibold cursor-pointer transition-colors hover:bg-gray-200"
                  onClick={() => router.back()}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className={`flex-1 p-2.5 text-white border-none rounded-lg text-sm font-semibold transition-all ${
                    loading ? "bg-gray-400 cursor-not-allowed" : "bg-brand cursor-pointer hover:bg-brand-hover"
                  }`}
                  disabled={loading}
                >
                  {loading ? "Saving…" : "Add User"}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}