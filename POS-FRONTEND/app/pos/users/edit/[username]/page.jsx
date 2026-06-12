"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { AlertCircle } from "lucide-react";
import BaseEditForm from "../../../../../components/edit/BaseEditForm";
import MultiDropDown from "../../../../../components/dropDowns/multiDropDown";

export default function EditUserPage() {
  const params = useParams();
  const [userId, setUserId] = useState(null);  // ✅ NEW: Store ID here
  const [roles, setRoles] = useState([]);
  const [status, setStatus] = useState(true);

  let username = null;
    if (params?.username) {
      const rawUsername = Array.isArray(params.username) ? params.username[0] : params.username;
      username = decodeURIComponent(rawUsername);
    }

  const renderEmailBlock = () => (
    <div className="flex items-start gap-2 bg-blue-50 border border-blue-200 rounded-lg p-3 mt-1">
      <AlertCircle size={15} className="text-blue-500 flex-shrink-0 mt-0.5" />
      <p className="text-xs text-blue-700">Email addresses are unique system identifiers and cannot be altered.</p>
    </div>
  );

  const extraFields = [
    {
      key: "username",
      label: "Email Address",
      type: "custom",
      component: renderEmailBlock(),
    },
    {
      key: "name",
      label: "Full Name",
      type: "text",
    },
    {
      key: "phoneNo",
      label: "Phone Number",
      type: "tel",
    },
    {
      key: "roles",
      label: "User Roles",
      type: "custom",
      component: (
        <MultiDropDown
          label="User Roles"
          entity="role"
          selectedValues={roles}
          onChange={setRoles}
          valueField="identifier"
          labelField="identifier"
        />
      ),
    },
    {
      key: "status",
      label: "Account Status",
      type: "custom",
      component: (
        <label className="flex items-center gap-2.5 mt-3">
          <input
            type="checkbox"
            checked={status}
            onChange={() => setStatus((prev) => !prev)}
            className="w-4 h-4 rounded accent-[#006E74] cursor-pointer"
          />
          <span className="text-sm font-medium text-[#231F20]">Active Account</span>
        </label>
      ),
    },
  ];

  if (!username) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white text-sm text-gray-500">
        Loading user configuration context...
      </div>
    );
  }

  return (
    <BaseEditForm
      title="User"
      apiPath="user"
      extraFields={extraFields}
      extraData={{ 
        id: userId,  // ✅ PASS ID (hidden from user)
        roles: roles.map((r) => r.startsWith("ROLE_") ? r : `ROLE_${r}`), 
        status 
      }}
      setters={{ 
        roles: setRoles, 
        status: setStatus,
        id: setUserId  // ✅ NEW: Set ID when data loads
      }}
      identifierKey="username" 
    />
  );
}