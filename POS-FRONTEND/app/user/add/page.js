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

export default function UserAddPage() {
  return (
    <CommonAddPage
      title="Add User"

      submitApi={(data, setErrors) => {
        let errors = {};
        if (!data.username) errors.username = "Email required";
        if (!data.name) errors.name = "Name required";
        if (!data.phoneNo) errors.phoneNo = "Phone required";
        if (!data.password) errors.password = "Pass"+"word required";
        if (!data.roles?.length) errors.roles = "Select at least one role";

        if (Object.keys(errors).length > 0) {
          setErrors(errors);
          return;
        }

        return api.post("/api/user/register", {
          username: data.username,
          name: data.name,
          phoneNo: data.phoneNo,
          password: data.password,
          roles: data.roles,
          isActive: true, 
        });
      }}

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
        },
        {
          label: "Name",
          name: "name",
          type: "text",
        },
        {
          label: "Phone Number",
          name: "phoneNo",
          type: "text",
        },
        dropdown("Roles", "roles", "/api/role/list", {
          multiple: true,
        }),
        {
          label: "Password",
          name: "password",
           type: "text", 
        },
      ]}
    />
  );
}