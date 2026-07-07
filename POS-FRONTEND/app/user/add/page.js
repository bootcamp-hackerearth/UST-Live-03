"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";

const dropdown = (label, name, apiUrl, extra = {}) => ({
  label,
  name,
  type: "dropdown",
  api: apiUrl,
  payload: {
    page: 0,
    sizePerPage: 100,
    sortField: "identifier",
    sortDirection: "ASC",
  },
  optionLabel: "identifier",
  optionValue: "identifier",
  placeholder: `Select ${label}`,
  ...extra,
});

const VALIDATION_MESSAGES = {
  USERNAME_REQUIRED: "Email required",
  NAME_REQUIRED: "Name required",
  PHONE_REQUIRED: "Phone required",
  CREDENTIAL_REQUIRED: "Password required",
  ROLE_REQUIRED: "Select at least one role",
};

export default function UserAddPage() {
  const handleSubmit = async (data) => {
    const errors = {};

    if (!data.username) {
      errors.username = VALIDATION_MESSAGES.USERNAME_REQUIRED;
    }

    if (!data.name) {
      errors.name = VALIDATION_MESSAGES.NAME_REQUIRED;
    }

    if (!data.phoneNo) {
      errors.phoneNo = VALIDATION_MESSAGES.PHONE_REQUIRED;
    }

    if (!data.password) {
      errors.password = VALIDATION_MESSAGES.CREDENTIAL_REQUIRED;
    }

    if (!data.roles?.length) {
      errors.roles = VALIDATION_MESSAGES.ROLE_REQUIRED;
    }

    if (Object.keys(errors).length > 0) {
      alert("Please fill all required fields");
      return false;
    }

    try {
      const response = await api.post("/api/user/register", {
        username: data.username,
        name: data.name,
        phoneNo: data.phoneNo,
        password: data.password,
        roles: data.roles,
        isActive: true,
      });

      console.log("USER RESPONSE:", response.data);

      const res = response.data;

      if (res.success === false) {
        alert(res.message);
        return false;
      }

      alert(res.message || "User added successfully");
      return true;
    } catch (error) {
      console.error("ERROR:", error);
      alert("Server error");
      return false;
    }
  };

  return (
    <CommonAddPage
      title="Add User"
      submitApi={handleSubmit}
      redirectRoute="/user/list"
      initialValues={{
        username: "",
        name: "",
        phoneNo: "",
        roles: [],
        password: "",
        status: true,
      }}
      fields={[
        {
          label: "Username",
          name: "username",
          type: "text",
          required: true,
        },
        {
          label: "Name",
          name: "name",
          type: "text",
          required: true,
        },
        {
          label: "Phone Number",
          name: "phoneNo",
          type: "text",
          required: true,
        },
        dropdown("Roles", "roles", "/api/role/list", {
          multiple: true,
        }),
        {
          label: "Password",
          name: "password",
          type: "text",
          required: true,
        },
      ]}
    />
  );
}
