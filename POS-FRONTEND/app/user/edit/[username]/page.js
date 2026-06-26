"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
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

export default function UserEditPage() {
  const fetchUser = async (username) => {
    try {
      const response = await api.get("/api/user/get", {
        params: {
          username: decodeURIComponent(username),
        },
      });

      const data = response.data?.data ?? response.data;

      return {
        data: {
          username: data.username || "",
          name: data.name || "",
          phoneNo: data.phoneNo || "",
          roles: data.roles || [],

          createdBy: data.createdBy || "",
          createdOn: data.createdOn || "",
          modifiedBy: data.modifiedBy || "",
          modifiedOn: data.modifiedOn || "",
        },
      };
    } catch (err) {
      console.error("Fetch User Error:", err);
      throw err;
    }
  };

  const handleUpdate = async (data) => {
    try {
      const response = await api.put(
        "/api/user/update",
        {
          username: data.username,
          name: data.name,
          phoneNo: data.phoneNo,
          roles: data.roles,
        },
        {
          params: {
            oldUsername: data.username,
          },
        }
      );

      const res = response.data;

      if (res.success === false) {
        alert(res.message);
        return false;
      }

      alert(res.message || "User updated successfully");
      return true;
    } catch (err) {
      console.error("Update User Error:", err);
      alert("Server error");
      return false;
    }
  };

  return (
    <CommonEditPage
      title="Edit User"
      fetchApi={fetchUser}
      updateApi={handleUpdate}
      redirectRoute="/user/list"
      identifierParam="username"
      submitButtonText="Update User"
      fields={[
        {
          label: "Username",
          name: "username",
          type: "text",
          readOnly: true,
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
      ]}
    />
  );
}