"use client";

import { useState } from "react";
import EditFormSkeleton from "@/components/EditSkeleton";
import MultiDropDown from "@/components/dropdowns/MultiDropDown";
import { labelStyle, inputStyle, inputErrorStyle, errText } from "@/components/sharedStyles";

const PHONE_PATTERN = /^[6-9]\d{9}$/;

export default function EditUser() {

  const [roles, setRoles] = useState([]);
  const [phoneNo, setPhoneNo] = useState("");
  const [phoneError, setPhoneError] = useState("");

  function handlePhoneChange(value) {
    setPhoneNo(value);
    const trimmed = value.trim();
    if (trimmed && PHONE_PATTERN.test(trimmed)) {
      setPhoneError("");
    } else if (trimmed) {
      setPhoneError("Enter a valid 10-digit phone number.");
    } else {
      setPhoneError("Phone number is required.");
    }
  }

  function validatePhone() {
    const trimmed = phoneNo.trim();
    if (trimmed && PHONE_PATTERN.test(trimmed)) {
      return {};
    }
    if (trimmed) {
      setPhoneError("Enter a valid 10-digit phone number.");
      return { phoneNo: "Enter a valid 10-digit phone number." };
    }
    setPhoneError("Phone number is required.");
    return { phoneNo: "Phone number is required." };
  }

  const extraFields = [
    {
      key: "name",
      label: "Full Name",
      type: "text",
    },
    {
      key: "phoneNo",
      type: "custom",
      label: "Phone Number",
      component: (
        <div style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
          <label htmlFor="phoneNo" style={labelStyle}>Phone Number</label>
          <input
            id="phoneNo"
            style={{ ...inputStyle, ...(phoneError ? inputErrorStyle : {}) }}
            type="text"
            placeholder="Enter Phone Number"
            value={phoneNo}
            onChange={(e) => handlePhoneChange(e.target.value)}
          />
          {phoneError && <span style={errText}>{phoneError}</span>}
        </div>
      ),
    },
    {
      key: "roles",
      type: "custom",
      label: "Roles",
      component: (
        <MultiDropDown
          label="Assign Role(s)"
          apiUrl="/role/findByStatus"
          valueField="identifier"
          labelField="identifier"
          selectedValues={roles}
          onChange={(val) => setRoles(val)}
        />
      ),
    },
  ];

  return (
    <EditFormSkeleton
      title="User"
      apiPath="user"
      paramName="username"
      identifierField="username"
      extraFields={extraFields}
      extraData={{ roles, phoneNo }}
      setters={{ roles: setRoles, phoneNo: setPhoneNo }}
      onValidate={validatePhone}
    />
  );
}