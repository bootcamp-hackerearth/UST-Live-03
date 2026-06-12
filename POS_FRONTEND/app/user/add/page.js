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
 
      // ✅ SUBMIT API
      submitApi={(data) => api.post("/api/user/add", data)}
 
      redirectRoute="/user/list"
 
      // ✅ INITIAL VALUES
      initialValues={{
        username: "",
        name: "",
        phoneNo: "",
        roles: [],
        password: "",
        status: true,
      }}
 
      fields={[
        // ✅ USERNAME (IDENTIFIER)
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
 
        // ✅ ROLES (MULTI SELECT ✅)
        dropdown("Roles", "roles", "/api/role/list", {
          multiple: true,
        }),
 
        {
          label: "Password",
          name: "password",
          type: "text", // you can later change to "password"
        },
       
      ]}
    />
  );
}