"use client"
import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation'; // Next.js router
import { UserIcon, PhoneIcon, EnvelopeIcon, LockClosedIcon, ShieldCheckIcon } from '@heroicons/react/24/outline';
import api from '../api/axios';


const Register = () => {
    const [formData, setFormData] = useState({
        name: '',
        phoneNo: '',
        username: '',
        password: '',
        roles: []
    });
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const router = useRouter();

    const [roles, setRoles] = useState('');
    const [showRoleDropDown, setShowRoleDropDown] = useState(false);
    const [roleSearchTerm, setRoleSearchTerm] = useState('');
    const [isSearchingRoles, setIsSearchingRoles] = useState(false);

    const searchRoles = async (term) => {
        setIsSearchingRoles(true);
        try{
            const response = await api.post('/api/role/list', {
                page: 0,
                sizePerPage: 10,
                sortField: 'identifier',
                filter: term
            });
            setRoles(response.data.dtoList || []);
        }
        catch(err)
        {
            console.error('Error searching roles:', err);
        }
        finally
        {
            setIsSearchingRoles(false);
        }
    };

    useEffect(() => {
        const delayDebounceFn = setTimeout(() => {
            if(roleSearchTerm)
            {
                searchRoles(roleSearchTerm);
            }
            else
            {
                setRoles([]);
            }
        }, 300);
        return () => clearTimeout(delayDebounceFn); 
    }, [roleSearchTerm]);

    useEffect(() => {
        const closeMenu = () => setShowRoleDropDown(false);
        globalThis.addEventListener('click', closeMenu);
        return () => globalThis.removeEventListener("click", closeMenu);
    }, []);

    const validate = () => {
        const { name, phoneNo, username, password, roles } = formData;

        if(name.trim().length < 3) return "Name must be at least 3 characters long.";
        if (!/^\d{10}$/.test(phoneNo)) {
          return "Phone number must be exactly 10 digits.";
        }
        const emailRegex = /^[^\s@]+@[^\s@.]+\.[^\s@]+$/;
        if ( !emailRegex.test(username) || username.length < 4)
        {
            return "please enter a valid email";
        }
        const passwordRegex =
          /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/;

        if (!passwordRegex.test(password)) {
          return "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number, and one special character.";
        }

        if (roles.length === 0) {
          return "Please select a role.";
        }

        return null;
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        const validationError = validate();
        if(validationError)
        {
            setMessage("Error: "+validationError);
            return;
        }
        setLoading(true);
        setMessage('');
        try {
            const response = await api.post('/api/registeruser/register', formData);
            setMessage("Success: " + (response.data.message || "Account created!"));
            alert('Registration successful!');
            router.push('/login'); 
        } catch (err) {
            setMessage("Error: " + (err.response?.data?.message || "Registration failed"));
        } finally {
            setLoading(false);
        }
  };
  let roleContent;

  if (isSearchingRoles) {
    roleContent = (
      <div className="p-3 text-sm text-gray-500 italic">Searching roles...</div>
    );
  } else if (roles.length > 0) {
    roleContent = (
      <ul className="py-1">
        {roles.map((role, idx) => (
          <li key={role.id || idx}>
            <button
              type="button"
              className="w-full text-left px-4 py-2.5 text-sm text-gray-700 hover:bg-[#2563eb] hover:text-white transition-colors"
              onClick={() => {
                setFormData({
                  ...formData,
                  roles: [role.identifier],
                });
                setRoleSearchTerm(role.identifier);
                setShowRoleDropDown(false);
              }}
            >
              {role.identifier}
            </button>
          </li>
        ))}
      </ul>
    );
  } else if (roleSearchTerm) {
    roleContent = (
      <div className="p-3 text-sm text-gray-400">No roles found</div>
    );
  }

    return (
      <div className="min-h-screen bg-gradient-to-br from-[#f0f4f8] to-[#e2e8f0] flex items-center justify-center p-5">
        <div className="bg-white w-full max-w-[440px] p-10 rounded-[24px] shadow-[0_20px_40px_rgba(0,0,0,0.1)] text-center">
          {/* Icon Header */}
          <div className="bg-[#2563eb] w-[60px] h-[60px] rounded-full flex items-center justify-center mx-auto mb-5 shadow-[0_10px_15px_rgba(37,99,235,0.3)]">
            <UserIcon className="w-8 text-white" />
          </div>

          <h2 className="text-[#1f2937] text-2xl font-semibold m-0">
            Create Account
          </h2>
          <p className="text-[#6b7280] text-sm mb-[30px]">
            Join us to manage your business efficiently
          </p>

          {/* Status Messages */}
          {message && (
            <div
              className={`p-[10px] rounded-lg text-[13px] mb-5 border ${
                message.startsWith("Success")
                  ? "bg-[#f0fdf4] border-[#dcfce7] text-[#16a34a]"
                  : "bg-[#fef2f2] border-[#fee2e2] text-[#dc2626]"
              }`}
            >
              {message}
            </div>
          )}

          <form onSubmit={handleRegister} className="text-left space-y-[18px]">
            {/* Full Name */}
            <div>
              <label
                htmlFor="fullName"
                className="text-[13px] font-medium text-[#374151] ml-1 mb-1.5 block"
              >
                Full Name
              </label>
              <div className="relative flex items-center">
                <UserIcon className="absolute left-3 w-5 text-[#9ca3af]" />
                <input
                  id="fullName"
                  type="text"
                  placeholder="Enter full name"
                  required
                  className="w-full py-3 pl-10 pr-3 border border-[#d1d5db] rounded-xl text-sm outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-[#2563eb1a] transition-all text-gray-900"
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                />
              </div>
            </div>

            {/* Phone Number */}
            <div>
              <label
                htmlFor="phoneNo"
                className="text-[13px] font-medium text-[#374151] ml-1 mb-1.5 block"
              >
                Phone Number
              </label>
              <div className="relative flex flex-col">
                {" "}
                {/* Added flex-col for message spacing */}
                <div className="relative flex items-center">
                  <PhoneIcon className="absolute left-3 w-5 text-[#9ca3af]" />
                  <input
                    id="phoneNo"
                    type="tel"
                    placeholder="10-digit number"
                    required
                    maxLength="10" // Prevents typing more than 10 digits
                    className={`w-full py-3 pl-10 pr-3 border rounded-xl text-sm outline-none transition-all text-gray-900 ${
                      formData.phoneNo && !/^\d{10}$/.test(formData.phoneNo)
                        ? "border-red-500 focus:ring-red-100"
                        : "border-[#d1d5db] focus:border-[#2563eb] focus:ring-[#2563eb1a] focus:ring-4"
                    }`}
                    onChange={(e) => {
                      // Only allow numbers to be typed
                      const value = e.target.value.replace(/\D/g, "");
                      setFormData({ ...formData, phoneNo: value });
                    }}
                    value={formData.phoneNo}
                  />
                </div>
                {/* Validation Message */}
                {formData.phoneNo && !/^\d{10}$/.test(formData.phoneNo) && (
                  <p className="text-[11px] text-red-500 mt-1 ml-1 animate-pulse">
                    {formData.phoneNo.length < 10
                      ? `Need ${10 - formData.phoneNo.length} more digits...`
                      : "Please enter a valid 10-digit number"}
                  </p>
                )}
              </div>
            </div>

            {/* Username */}
            <div>
              <label
                htmlFor="email"
                className="text-[13px] font-medium text-[#374151] ml-1 mb-1.5 block"
              >
                Email
              </label>
              <div className="relative flex items-center">
                <EnvelopeIcon className="absolute left-3 w-5 text-[#9ca3af]" />
                <input
                  id="email"
                  type="email"
                  placeholder="user@gmail.com"
                  required
                  className="w-full py-3 pl-10 pr-3 border border-[#d1d5db] rounded-xl text-sm outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-[#2563eb1a] transition-all text-gray-900"
                  onChange={(e) =>
                    setFormData({ ...formData, username: e.target.value })
                  }
                />
              </div>
            </div>

            {/* Password with Toggle */}
            <div>
              <label
                htmlFor="password"
                className="text-[13px] font-medium text-[#374151] ml-1 mb-1.5 block"
              >
                Password
              </label>
              <div className="relative flex items-center">
                <LockClosedIcon className="absolute left-3 w-5 text-[#9ca3af]" />
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="••••••••"
                  required
                  minLength={8}
                  value={formData.password}
                  className={`w-full py-3 pl-10 pr-12 border rounded-xl text-sm outline-none transition-all text-gray-900 ${
                    formData.password &&
                    !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/.test(
                      formData.password,
                    )
                      ? "border-red-500 focus:ring-red-100"
                      : "border-[#d1d5db] focus:border-[#2563eb] focus:ring-[#2563eb1a] focus:ring-4"
                  }`}
                  onChange={(e) =>
                    setFormData({ ...formData, password: e.target.value })
                  }
                />
                <button
                  type="button"
                  className="absolute right-3 top-1/2 -translate-y-1/2 p-2 text-gray-400 hover:text-blue-600 focus:outline-none transition-all duration-200"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    /* Simplified "Eye Slash" Icon - Prevents the 'half-eye' glitch */
                    <svg
                      xmlns="http://w3.org"
                      width="20"
                      height="20"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                      <line x1="1" y1="1" x2="23" y2="23"></line>
                    </svg>
                  ) : (
                    /* Standard "Eye" Icon */
                    <svg
                      xmlns="http://w3.org"
                      width="20"
                      height="20"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                      <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                  )}
                </button>
              </div>
              {formData.password &&
                !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/.test(
                  formData.password,
                ) && (
                  <p className="text-[11px] text-red-500 mt-1 ml-1">
                    Password must contain:
                    <br />
                    • 8+ characters
                    <br />
                    • 1 uppercase letter
                    <br />
                    • 1 lowercase letter
                    <br />
                    • 1 number
                    <br />• 1 special character
                  </p>
                )}
            </div>

            {/* Role */}
            <div className="relative">
              <label
                htmlFor="role"
                className="text-[13px] font-medium text-[#374151] ml-1 mb-1.5 block"
              >
                Role
              </label>
              <div className="relative flex items-center">
                <ShieldCheckIcon className="absolute left-3 w-5 text-[#9ca3af]" />
                <input
                  id="role"
                  type="text"
                  placeholder="Search and select roles"
                  required
                  className="w-full py-3 pl-10 pr-3 border border-[#d1d5db] rounded-xl text-sm outline-none focus:border-[#2563eb] focus:ring-4 focus:ring-[#2563eb1a] transition-all text-gray-900"
                  value={roleSearchTerm} // Use the search term for the input display
                  onClick={(e) => {
                    e.stopPropagation();
                    setShowRoleDropDown(true);
                  }}
                  onFocus={() => setShowRoleDropDown(true)}
                  onChange={(e) => {
                    const val = e.target.value;
                    setRoleSearchTerm(val);
                    setShowRoleDropDown(true);
                  }}
                />
              </div>

              {showRoleDropDown && (
                <div className="absolute z-[100] w-full bg-white border border-[#d1d5db] rounded-xl mt-2 shadow-xl max-h-52 overflow-y-auto overflow-x-hidden">
                  {roleContent}
                </div>
              )}
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3.5 mt-2.5 bg-gradient-to-r from-[#2563eb] to-[#1e40af] text-white rounded-xl font-semibold text-base shadow-md hover:shadow-lg hover:-translate-y-0.5 active:scale-[0.98] transition-all disabled:opacity-50"
            >
              {loading ? "Processing..." : "Register"}
            </button>
          </form>

          <div className="mt-[25px] text-sm text-[#4b5563]">
            Already have an account?{" "}
            <Link
              href="/login"
              className="text-[#2563eb] font-semibold hover:underline"
            >
              Sign in here
            </Link>
          </div>
        </div>
      </div>
    );
};


export default Register;
