"use client";
import PropTypes from "prop-types";
import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import api from "../../../components/Axios";
import MultiDropDown from "../../../components/MultiDropDown";

const inputContainerBlock = "flex flex-col gap-1.25";
const statusBannerStyle = "border rounded-lg p-2.5 md:p-3.5 text-xs md:text-sm mb-4 text-center md:col-span-2";
const textInputFieldClass = "py-2 px-3 border-[1.5px] rounded-lg text-sm outline-none bg-[#fafaf8] box-border w-full transition-all focus:border-brand";
const bottomActionControl = "flex-1 p-2.5 border-none rounded-lg text-sm font-semibold transition-all";
const fieldErrorClass = "text-[11px] text-[#e53e3e] mt-0.5";

function StatusBanner({ type, message }) {
  const styles =
    type === "error"
      ? "bg-[#fff5f5] border-[#fca5a5] text-[#c53030]"
      : "bg-[#f0fff4] border-[#9ae6b4] text-[#276749]";
  return <div className={`${styles} ${statusBannerStyle}`}>{message}</div>;
}

StatusBanner.propTypes = {
  type: PropTypes.oneOf(["error", "success"]).isRequired,
  message: PropTypes.string.isRequired,
};

function FormField({ id, name, label, value, placeholder, inputMode, error, onChange }) {
  const borderClass = error ? "border-[#e53e3e]" : "border-gray-300";
  return (
    <div className={inputContainerBlock}>
      <label htmlFor={id} className="text-xs md:text-sm font-semibold text-gray-600">
        {label}
      </label>
      <input
        id={id}
        name={name}
        className={`${textInputFieldClass} ${borderClass}`}
        type="text"
        inputMode={inputMode}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
      />
      {error && <span className={fieldErrorClass}>{error}</span>}
    </div>
  );
}

FormField.propTypes = {
  id: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
  placeholder: PropTypes.string.isRequired,
  inputMode: PropTypes.string,
  error: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

FormField.defaultProps = {
  inputMode: "text",
  error: "",
};

function formatDateTime(raw) {
  if (!raw) return "—";
  const d = new Date(raw);
  return d.toLocaleString("en-IN", {
    day: "2-digit", month: "short", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
}

function RecordInfoPanel({ auditInfo }) {
  const rows = [
    ["Created By", auditInfo.createdBy || "—"],
    ["Created On", formatDateTime(auditInfo.createdOn)],
    ["Modified By", auditInfo.modifiedBy || "—"],
    ["Modified On", formatDateTime(auditInfo.modifiedOn)],
  ];
  return (
    <div className="mt-6 pt-5 border-t border-gray-100">
      <p className="text-[11px] font-semibold text-gray-400 uppercase tracking-wider mb-3">
        Record Info
      </p>
      <div className="grid grid-cols-2 gap-x-6 gap-y-3">
        {rows.map(([label, value]) => (
          <div key={label} className="flex flex-col gap-0.5">
            <span className="text-[11px] font-semibold text-gray-400">{label}</span>
            <span className="text-[13px] text-gray-700">{value}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

RecordInfoPanel.propTypes = {
  auditInfo: PropTypes.object.isRequired,
};

export default function EditUser() {
  const router = useRouter();
  const params = useParams();
  const username = params.username ? decodeURIComponent(params.username) : "";
  const [userId, setUserId] = useState(null);
  const [form, setForm] = useState({ name: "", phoneNo: "" });
  const [roles, setRoles] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const [auditInfo, setAuditInfo] = useState({
    createdBy: null,
    createdOn: null,
    modifiedBy: null,
    modifiedOn: null,
  });

  function clearFieldError(name) {
    setFieldErrors((prev) => ({ ...prev, [name]: "" }));
  }

  useEffect(() => {
    if (!username) return;
    async function loadUser() {
      try {
        const res = await api.get(`/user/get?username=${encodeURIComponent(username)}`);
        const data = res.data;
        setUserId(data.id || null);
        setForm({ name: data.name || "", phoneNo: data.phoneNo || "" });
        setRoles(data.roles || []);
        setAuditInfo({
          createdBy: data.createdBy || null,
          createdOn: data.createdOn || null,
          modifiedBy: data.modifiedBy || null,
          modifiedOn: data.modifiedOn || null,
        });
      } catch (err) {
        console.error("Failed to load user data:", err);
        setError(
          err?.response?.data?.message ||
          "Could not load user data. Please go back and try again."
        );
      } finally {
        loading && setLoading(false);
      }
    }
    loadUser();
  }, [username]);

  function handleChange(e) {
    const { name, value } = e.target;
    if (name === "phoneNo") {
      const digitsOnly = value.replaceAll(/\D/g, "");
      if (digitsOnly.length > 10) return;
      setForm((prev) => ({ ...prev, [name]: digitsOnly }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
    if (fieldErrors[name]) clearFieldError(name);
  }

  function validate() {
    const errors = {};
    if (!form.name.trim()) {
      errors.name = "Full name is required.";
    }
    if (!form.phoneNo.trim()) {
      errors.phoneNo = "Phone number is required.";
    } else if (form.phoneNo.trim().length !== 10) {
      errors.phoneNo = "Phone number must be exactly 10 digits.";
    }
    if (roles.length === 0) {
      errors.roles = "Please select at least one role.";
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    if (!validate()) return;
    setSubmitting(true);
    try {
      const res = await api.put("/user/update", {
        id: userId,
        username,
        name: form.name.trim(),
        phoneNo: form.phoneNo.trim(),
        roles,
      });
      const data = res.data;
      if (data?.username) {
        setSuccess("User updated successfully!");
        setTimeout(() => router.push("/user/list"), 1500);
      } else {
        setError("Update failed. Please try again.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Unable to connect to server.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f9fafb] font-sans flex flex-col overflow-hidden">
      <div className="flex-1 p-5 md:p-6 flex flex-col overflow-hidden">
        <div className="flex items-center gap-3 mb-4 shrink-0 relative">
          <button
            type="button"
            className="py-2 px-4 bg-transparent text-brand border-[1.5px] border-brand rounded-lg text-xs md:text-sm font-semibold cursor-pointer shrink-0 transition-colors hover:bg-brand/5"
            onClick={() => router.push("/user/list")}
          >
            &larr; Back
          </button>
          <h2 className="absolute left-1/2 -translate-x-1/2 m-0 text-xl font-bold text-brand whitespace-nowrap">
            Edit User
          </h2>
        </div>
        <div className="flex-1 flex items-center justify-center overflow-hidden">
          <div className="bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.06)] p-6 md:p-8 w-full max-w-[720px] max-h-full overflow-auto">
            <p className="text-base md:text-[17px] font-bold text-[#1a1a1a] m-0 mb-1">
              Update User
            </p>
            <p className="text-xs md:text-sm text-gray-400 mb-5">
              Update the details below
            </p>
            {error && <StatusBanner type="error" message={error} />}
            {success && <StatusBanner type="success" message={success} />}
            {loading ? (
              <p className="text-center text-gray-400 text-sm py-10">
                Loading user data&hellip;
              </p>
            ) : (
              <>
                <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-3.5">
                  <div className={inputContainerBlock}>
                    <label htmlFor="username" className="text-xs md:text-sm font-semibold text-gray-500">
                      Username
                    </label>
                    <input
                      id="username"
                      className="py-2 px-3 border-[1.5px] border-gray-200 rounded-lg text-sm bg-gray-100 text-gray-400 box-border w-full cursor-not-allowed outline-none"
                      type="text"
                      value={username}
                      disabled
                    />
                  </div>
                  <FormField
                    id="name"
                    name="name"
                    label="Full Name"
                    value={form.name}
                    placeholder="Enter full name"
                    error={fieldErrors.name}
                    onChange={handleChange}
                  />
                  <FormField
                    id="phoneNo"
                    name="phoneNo"
                    label="Phone Number"
                    value={form.phoneNo}
                    placeholder="10-digit mobile number"
                    inputMode="numeric"
                    error={fieldErrors.phoneNo}
                    onChange={handleChange}
                  />
                  <div className={`${inputContainerBlock} col-span-1 md:col-span-2`}>
                    <MultiDropDown
                      label="Assign Role(s)"
                      apiUrl="/role/findByStatus"
                      valueField="identifier"
                      labelField="identifier"
                      selectedValues={roles}
                      onChange={(val) => {
                        setRoles(val);
                        if (fieldErrors.roles) clearFieldError("roles");
                      }}
                    />
                    {fieldErrors.roles && <span className={fieldErrorClass}>{fieldErrors.roles}</span>}
                  </div>
                  <div className="flex gap-3 mt-2 col-span-1 md:col-span-2">
                    <button
                      type="button"
                      className={`${bottomActionControl} bg-gray-100 text-gray-600 hover:bg-gray-200 cursor-pointer`}
                      onClick={() => router.push("/user/list")}
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className={`${bottomActionControl} text-white ${submitting
                        ? "bg-gray-400 cursor-not-allowed"
                        : "bg-brand cursor-pointer hover:bg-brand-hover"
                        }`}
                      disabled={submitting}
                    >
                      {submitting ? "Saving\u2026" : "Update User"}
                    </button>
                  </div>
                </form>

                <RecordInfoPanel auditInfo={auditInfo} />
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}