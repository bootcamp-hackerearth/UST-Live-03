// app/components/ProfileDropdownCard.jsx

"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import axios from "../app/api/axios";
import { Shield, Mail, Phone, Check, X, Loader2 } from "lucide-react";

export default function ProfileDropdownCard({ onClose }) {
  const [profileData, setProfileData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [editMode, setEditMode] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    phoneNo: ""
  });

  const fetchUserProfile = async () => {
    try {
      setLoading(true);
      setError("");
      const token = localStorage.getItem("token");
      const currentUsername = localStorage.getItem("username") || "nulluser";
      const res = await axios.get(`${axios.defaults.baseURL}/user/get`, {
        params: { identifier: currentUsername },
        headers: { Authorization: `Bearer ${token}` }
      });

      if (res.data) {
        setProfileData(res.data);
        setFormData({
          name: res.data.name || "",
          phoneNo: res.data.phoneNo || ""
        });
      }
    } catch (err) {
      console.error("Profile was not able to Load:", err);
      setError("Could not retrieve User Profile information.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUserProfile();
  }, []);

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      setError("");
      setSuccess("");
      const token = localStorage.getItem("token");

      const payload = {
        ...profileData,
        name: formData.name,
        phoneNo: formData.phoneNo
      };

      await axios.post(`${axios.defaults.baseURL}/user/update`, payload, {
        headers: { Authorization: `Bearer ${token}` }
      });

      setSuccess("Account configurations persisted successfully.");
      setEditMode(false);
      
      await fetchUserProfile();
    } catch (err) {
      console.error("Backend state updating transaction failure:", err);
      setError("Failed to save identity modifications.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="p-12 flex flex-col items-center justify-center text-slate-400 gap-3 min-h-[300px]">
        <Loader2 size={32} className="animate-spin text-[#006E74]" />
        <span className="text-sm font-semibold tracking-wide text-slate-600">Syncing database records...</span>
      </div>
    );
  }

  return (
    <div className="w-96 bg-white border border-[#231F20]/15 rounded-xl shadow-2xl overflow-hidden transform transition-all duration-200 ease-out origin-top-right animate-in fade-in zoom-in-95">
      
      <div className="bg-slate-950 p-5 text-white flex justify-between items-center">
        <div>
          <h2 className="text-sm font-bold tracking-wide text-white uppercase tracking-wider">User Profile</h2>
          <p className="text-xs text-[#0097AC] font-mono tracking-tight mt-1 font-semibold">
            {profileData?.username || "fetching username..."}
          </p>
        </div>
        <span className={`text-[11px] uppercase font-bold tracking-widest px-2.5 py-1 rounded-md border ${
          profileData?.status 
            ? "bg-emerald-500/20 text-emerald-400 border-emerald-500/30" 
            : "bg-amber-500/20 text-amber-400 border-amber-500/30"
        }`}>
          {profileData?.status ? "Active" : "InActive"}
        </span>
      </div>

      {error && <div className="bg-red-50 text-red-700 text-xs font-bold px-5 py-3 border-b border-red-100">{error}</div>}
      {success && <div className="bg-emerald-50 text-emerald-700 text-xs font-bold px-5 py-3 border-b border-emerald-100">{success}</div>}

      <div className="p-6 space-y-5">
        
        <div>
          <span className="text-xs font-bold text-[#231F20]/50 uppercase tracking-widest block mb-2">
            Security Access
          </span>
          <div className="flex flex-wrap gap-2">
            {profileData?.roles && profileData.roles.length > 0 ? (
              profileData.roles.map((role) => (
                <span key={`profile-role-${role}`} className="inline-flex items-center gap-1.5 text-xs font-bold bg-[#006E74]/10 text-[#006E74] border border-[#006E74]/15 px-3 py-1 rounded-md">
                  <Shield size={12} />
                  {role}
                </span>
              ))
            ) : (
              <span className="text-xs font-medium text-slate-400 italic">No explicit roles mapping.</span>
            )}
          </div>
        </div>

        <hr className="border-[#231F20]/10" />

        <form onSubmit={handleUpdateProfile} className="space-y-4">
          
          <div>
            <span className="text-xs font-bold text-[#231F20]/50 uppercase tracking-widest block mb-1.5">
              System Username (Non-Editable)
            </span>
            <div className="flex items-center gap-2.5 px-3.5 py-2.5 bg-slate-50 border border-[#231F20]/10 rounded-lg text-sm font-mono text-[#231F20]/70 select-none font-semibold">
              <Mail size={14} className="text-slate-400" />
              <span>{profileData?.username || "N/A"}</span>
            </div>
          </div>

          <div>
            {editMode ? (
              <>
                <label htmlFor="display-name-field" className="text-xs font-bold text-[#231F20]/50 uppercase tracking-widest block mb-1.5">
                  Display Name
                </label>
                <input
                  id="display-name-field"
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="w-full text-sm px-3.5 py-2.5 border border-[#006E74] focus:ring-2 focus:ring-[#006E74]/10 rounded-lg outline-none font-bold text-[#231F20]"
                />
              </>
            ) : (
              <>
                <span className="text-xs font-bold text-[#231F20]/50 uppercase tracking-widest block mb-1.5">
                  Display Name
                </span>
                <div className="flex items-center gap-2.5 px-1 py-1 text-sm font-bold text-[#231F20]">
                  <span>{profileData?.name || "Unassigned Operator Name"}</span>
                </div>
              </>
            )}
          </div>

          <div>
            {editMode ? (
              <>
                <label htmlFor="phone-no-field" className="text-xs font-bold text-[#231F20]/50 uppercase tracking-widest block mb-1.5">
                  Phone No
                </label>
                <input
                  id="phone-no-field"
                  type="text"
                  value={formData.phoneNo}
                  onChange={(e) => setFormData({ ...formData, phoneNo: e.target.value })}
                  className="w-full text-sm px-3.5 py-2.5 border border-[#006E74] focus:ring-2 focus:ring-[#006E74]/10 rounded-lg outline-none font-bold text-[#231F20]"
                />
              </>
            ) : (
              <>
                <span className="text-xs font-bold text-[#231F20]/50 uppercase tracking-widest block mb-1.5">
                  Phone No
                </span>
                <div className="flex items-center gap-2.5 px-1 py-1 text-sm font-semibold text-[#231F20]/80">
                  <Phone size={14} className="text-[#231F20]/40" />
                  <span>{profileData?.phoneNo || "No contact digits registered"}</span>
                </div>
              </>
            )}
          </div>

          <div className="border-t border-[#231F20]/10 pt-4 mt-5 flex items-center justify-between gap-3">
            {editMode ? (
              <>
                <button
                  type="button"
                  onClick={() => {
                    setEditMode(false);
                    setError("");
                  }}
                  className="flex-1 flex items-center justify-center gap-2 text-xs font-bold uppercase tracking-wider bg-slate-100 hover:bg-slate-200 text-[#231F20]/80 py-2.5 rounded-lg transition-colors"
                >
                  <X size={14} /> Cancel
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="flex-1 flex items-center justify-center gap-2 text-xs font-bold uppercase tracking-wider bg-[#006E74] hover:bg-[#00555a] text-white py-2.5 rounded-lg transition-colors disabled:opacity-50 shadow-sm"
                >
                  {saving ? <Loader2 size={14} className="animate-spin" /> : <Check size={14} />} Save Changes
                </button>
              </>
            ) : (
              <>
                <button
                  type="button"
                  onClick={() => setEditMode(true)}
                  className="flex-1 text-xs font-bold uppercase tracking-wider text-[#006E74] hover:bg-[#006E74]/5 border border-[#006E74]/20 py-2.5 rounded-lg transition-all text-center animate-none"
                >
                  Update Account Details
                </button>
                <button
                  type="button"
                  onClick={onClose}
                  className="text-xs font-bold uppercase tracking-wider bg-slate-100 hover:bg-slate-200 text-[#231F20]/70 px-4 py-2.5 rounded-lg transition-colors"
                >
                  Close
                </button>
              </>
            )}
          </div>

        </form>
      </div>
    </div>
  );
}

ProfileDropdownCard.propTypes = {
  onClose: PropTypes.func.isRequired,
};