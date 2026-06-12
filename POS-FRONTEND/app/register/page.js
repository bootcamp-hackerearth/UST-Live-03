"use client";
import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import api from "../components/Axios";
import MultiDropdown from "../components/MultiDropDown";

export default function Register() {
  const [form, setForm] = useState({
    name: "",
    username: "",
    phoneNo: "",
    password: "",
  });

  const MIN_SECRET_LENGTH = 4;
  const SECRET_REQUIRED_MSG = "This field is required.";
  const SECRET_LENGTH_MSG = `Must be at least ${MIN_SECRET_LENGTH} characters.`;

  const [selectedRoles, setSelectedRoles] = useState([]);
  const [fieldErrors, setFieldErrors] = useState({});
  const [message, setMessage] = useState({ text: "", type: "" });
  const [loading, setLoading] = useState(false);

  const router = useRouter();

  const fields = [
    ["name", "Full Name", "text", "e.g. John Smith"],
    ["username", "Email Address", "email", "e.g. john@retailpos.com"],
    ["phoneNo", "Phone Number", "text", "10-digit mobile number"],
    ["password", "Password", "password", "Min. 4 characters"],
  ];

  const renderInput = ([id, label, type, placeholder]) => (
    <div key={id} className="mb-3">
      <label className="block text-[12px] font-semibold text-gray-700 mb-1" htmlFor={id}>
        {label}
      </label>
      <input
        id={id}
        name={id}
        type={type}
        placeholder={placeholder}
        value={form[id]}
        autoComplete={id === "password" ? "new-password" : id}
        className={`w-full px-3 py-2 border-[1.5px] border-solid rounded-lg text-xs outline-none bg-[#fafaf8] ${
          fieldErrors[id] ? "border-red-500 focus:border-red-600" : "border-gray-300 focus:border-brand"
        }`}
        onChange={handleChange}
      />
      {fieldErrors[id] && (
        <p className="text-[11px] text-red-500 mt-0.5 m-0">{fieldErrors[id]}</p>
      )}
    </div>
  );

  const renderMessage = () =>
    message.text && (
      <div
        className={`border rounded-lg py-2 px-3 text-xs mb-3 text-center ${
          message.type === "success"
            ? "bg-[#f0fff4] border-green-300 text-green-800"
            : "bg-[#fff5f5] border-red-200 text-red-700"
        }`}
      >
        {message.text}
      </div>
    );

  function handleChange(e) {
    const { name, value } = e.target;

    if (name === "phoneNo") {
      const digitsOnly = value.replaceAll(/\D/g, "");
      if (digitsOnly.length > 10) return;
      setForm((prev) => ({ ...prev, [name]: digitsOnly }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }

    if (fieldErrors[name]) {
      setFieldErrors((prev) => ({ ...prev, [name]: "" }));
    }
  }

  function handleRolesChange(values) {
    setSelectedRoles(values);
    if (fieldErrors.roles) {
      setFieldErrors((prev) => ({ ...prev, roles: "" }));
    }
  }

  function validate() {
    const errors = {};
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (!form.name.trim()) errors.name = "Full name is required.";
    if (!form.username.trim()) errors.username = "Email address is required.";
    else if (!emailRegex.test(form.username.trim()))
      errors.username = "Please enter a valid email address.";

    if (!form.phoneNo.trim()) errors.phoneNo = "Phone number is required.";
    else if (form.phoneNo.trim().length !== 10)
      errors.phoneNo = "Phone number must be exactly 10 digits.";

    if (selectedRoles.length === 0)
      errors.roles = "Please select at least one role.";

    if (!form.password) {
      errors.password = SECRET_REQUIRED_MSG;
    } else if (form.password.length < MIN_SECRET_LENGTH) {
    errors.password = SECRET_LENGTH_MSG;
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
    }

  async function handleRegister(e) {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    setMessage({ text: "", type: "" });

    const payload = {
      name: form.name.trim(),
      username: form.username.trim().toLowerCase(),
      phoneNo: form.phoneNo.trim(),
      password: form.password,
      roles: selectedRoles,
    };

    try {
      const response = await api.post("/user/register", payload);
      const data = response.data;

      if (data.success === true) {
        setMessage({ text: "Registration successful! Redirecting...", type: "success" });
        setTimeout(() => router.push("/login"), 1500);
      } else {
        setMessage({ text: data.message || "Registration failed.", type: "error" });
      }
    } catch (err) {
      const msg = err.response?.data?.message || "Server error. Please try again.";
      setMessage({ text: msg, type: "error" });
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="h-screen bg-[#f5f5f0] flex items-center justify-center font-sans overflow-hidden p-4">
      <div className="bg-white rounded-xl py-6 px-8 w-full max-w-[440px] shadow-[0_4px_24px_rgba(0,0,0,0.08)] box-border">

        <div className="text-center mb-4">
          <div className="text-2xl mb-1">👤</div>
          <h1 className="text-xl font-bold text-[#1a1a1a] m-0 mb-0.5">Create Account</h1>
          <p className="text-xs text-gray-500 m-0">Register a new RetailPOS user</p>
        </div>
        {renderMessage()}

        <form onSubmit={handleRegister} noValidate>
          {fields.slice(0, 3).map(renderInput)}
          <div className="mb-3">
            <MultiDropdown
              label="Assign Role(s)"
              apiUrl="/role/findByStatus"
              valueField="identifier"
              labelField="identifier"
              selectedValues={selectedRoles}
              onChange={handleRolesChange}
            />
            {fieldErrors.roles && (
              <p className="text-[11px] text-red-500 mt-0.5 m-0">{fieldErrors.roles}</p>
            )}
          </div>
          {renderInput(fields[3])}

         <button
            type="submit"
            disabled={loading}
            className={`w-full p-2.5 rounded-lg text-xs font-semibold mt-1 ${
              loading ? "bg-gray-400 cursor-not-allowed" : "bg-brand text-white cursor-pointer"
            }`}
          >
            {loading ? "Registering…" : "Register User"}
          </button>

        </form>

        <div className="text-center mt-3 text-xs text-gray-500">
          Already have an account?
          <Link href="/login" className="ml-1 text-brand font-semibold">
            Sign in
          </Link>
        </div>

      </div>
    </div>
  );
}