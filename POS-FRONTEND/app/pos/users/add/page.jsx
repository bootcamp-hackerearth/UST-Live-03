// app/pos/users/add/page.jsx

"use client";

import { useState } from "react";
import BaseAddForm from "../../../../components/add/BaseAddForm";
import MultiDropDown from "../../../../components/dropDowns/multiDropDown";

export default function UserAdd() {
  const [roles, setRoles] = useState([]);
  const [confirmPassword, setConfirmPassword] = useState("");

  const status = true;

  const extraFields = [
    {
      key: "name",
      label: "Full Name",
      type: "text",
      required: true,
    },
    {
      key: "phoneNo",
      label: "Phone Number",
      type: "tel",
      required: true,
    },
    {
      key: "password",
      label: "Password",
      type: "password",
      required: true,
    },
    {
      key: "confirmPassword",
      type: "custom",
      component: (
        <div className="flex flex-col gap-1.5 text-left">
          <label
            htmlFor="confirm-password-field"
            className="block text-xs font-bold text-slate-700 uppercase tracking-wider"
          >
            Confirm Password *
          </label>
          <input
            id="confirm-password-field"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Re-enter password"
            className="w-full px-4 py-2.5 bg-white border border-slate-200 rounded-lg text-sm outline-none focus:border-slate-400 focus:ring-2 focus:ring-slate-900/5 placeholder-slate-400 text-slate-800 font-medium shadow-sm"
          />
          <p className="text-xs text-slate-400">Must match the password entered</p>
        </div>
      ),
    },
    {
      key: "roles",
      type: "custom",
      component: (
        <MultiDropDown
          label="Assign Roles *"
          entity="role"
          selectedValues={roles}
          onChange={setRoles}
          valueField="identifier"
          labelField="identifier"
        />
      ),
    },
  ];

  const extraData = {
    roles,
    status,
    confirmPassword,
  };

  return (
    <BaseAddForm
      title="User"
      apiPath="user"
      customAction="register"
      identifierKey="username"
      extraFields={extraFields}
      extraData={extraData}
    />
  );
}