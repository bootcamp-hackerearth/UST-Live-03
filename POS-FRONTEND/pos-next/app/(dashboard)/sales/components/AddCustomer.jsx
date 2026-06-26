"use client";

import React, { useState } from "react";
import PropTypes from "prop-types";

export default function CustomerAdd({ isOpen, onClose, onCustomerAdded }) {
  const [name, setName] = useState("");
  const [mobile, setMobile] = useState("");
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await fetch("/api/add-entity", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          endpoint: "/api/customer/add",
          payload: { name, phoneNo: mobile, identifier: email }
        }),
      });

      const textData = await response.text();
      let data;
      
      try {
        data = textData ? JSON.parse(textData) : {};
      } catch {
        data = { message: `Server error code ${response.status}`, identifier: email, name };
      }

      if (!response.ok) {
        throw new Error(typeof data.message === "string" && data.message.includes("<!DOCTYPE")
          ? `Backend Error (${response.status}): Failed to save.`
          : data.message || "Failed to save customer account."
        );
      }

      if (onCustomerAdded) onCustomerAdded(data);

      setName("");
      setMobile("");
      setEmail("");
      onClose();
    } catch (err) {
      setError(err.message || "Something went wrong while saving the customer.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
      <div className="w-full max-w-md bg-white rounded-xl shadow-2xl border border-slate-200 overflow-hidden transform transition-all">
        <div className="px-6 py-4 bg-slate-50 border-b border-slate-200 flex justify-between items-center">
          <div>
            <span className="text-[10px] font-black text-black uppercase tracking-widest block mb-0.5">CRM System</span>
            <h3 className="text-base font-black text-slate-900 tracking-tight">Create Customer Profile</h3>
          </div>
          <button type="button" onClick={onClose} className="w-8 h-8 rounded-lg flex items-center justify-center border border-slate-200 bg-white text-slate-500 hover:text-slate-800 font-bold">&times;</button>
        </div>

        {error && (
          <div className="mx-6 mt-4 p-3 border-2 border-rose-200 bg-rose-50 text-rose-700 font-bold text-xs rounded-xl flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-4 h-4 shrink-0">
              <path fillRule="evenodd" d="M9.401 3.003c1.155-2 4.043-2 5.197 0l7.355 12.748c1.154 2-.29 4.5-2.599 4.5H4.645c-2.309 0-3.752-2.5-2.598-4.5L9.4 3.003zM12 8.25a.75.75 0 01.75.75v3.75a.75.75 0 01-1.5 0V9a.75.75 0 01.75-.75zm0 8.25a.75.75 0 100-1.5.75.75 0 000 1.5z" clipRule="evenodd" />
            </svg>
            <span>{error}</span>
          </div>
        )}  

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label htmlFor="fullName" className="block text-xs font-black text-slate-700 uppercase tracking-wider mb-1.5">Full Name <span className="text-rose-500">*</span></label>
            <input id="fullName" type="text" required disabled={loading} value={name} onChange={(e) => setName(e.target.value)} placeholder="e.g. John Doe" className="w-full text-sm font-semibold text-slate-900 placeholder-slate-400 bg-white border border-slate-300 rounded-xl px-3.5 py-2.5 outline-none focus:border-slate-900 focus:ring-4 focus:ring-slate-900/5 transition-all disabled:opacity-50" />
          </div>

          <div>
            <label htmlFor="mobileNumber" className="block text-xs font-black text-slate-700 uppercase tracking-wider mb-1.5">Mobile Number <span className="text-rose-500">*</span></label>
            <input id="mobileNumber" type="tel" required disabled={loading} value={mobile} onChange={(e) => setMobile(e.target.value)} placeholder="e.g. 9876543210" className="w-full text-sm font-semibold text-slate-900 placeholder-slate-400 bg-white border border-slate-300 rounded-xl px-3.5 py-2.5 outline-none focus:border-slate-900 focus:ring-4 focus:ring-slate-900/5 transition-all disabled:opacity-50" />
          </div>

          <div>
            <label htmlFor="email" className="block text-xs font-black text-slate-700 uppercase tracking-wider mb-1.5">Email Address</label>
            <input id="email" type="email" disabled={loading} value={email} onChange={(e) => setEmail(e.target.value)} placeholder="e.g. john.doe@example.com" className="w-full text-sm font-semibold text-slate-900 placeholder-slate-400 bg-white border border-slate-300 rounded-xl px-3.5 py-2.5 outline-none focus:border-slate-900 focus:ring-4 focus:ring-slate-900/5 transition-all disabled:opacity-50" />
          </div>

          <div className="flex justify-end gap-3 pt-3 border-t border-slate-100 mt-6">
            <button type="button" onClick={onClose} disabled={loading} className="px-4 py-2.5 text-xs font-bold border border-slate-300 text-slate-700 rounded-xl bg-white hover:bg-slate-50 transition disabled:opacity-40">Cancel</button>
            <button type="submit" disabled={loading} className="text-xs font-bold bg-blue-600 hover:bg-blue-700 text-white px-5 py-2.5 rounded-xl transition flex items-center gap-1.5 shadow disabled:opacity-50">
              {loading ? (
                <>
                  <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  <span>Saving...</span>
                </>
              ) : "Save Customer"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

CustomerAdd.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onCustomerAdded: PropTypes.func,
};