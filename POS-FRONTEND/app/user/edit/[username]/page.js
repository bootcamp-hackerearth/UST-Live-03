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
  return (
    <CommonEditPage
      title="Edit User"

      fetchApi={(username) =>
        api.get("/api/user/get", {
          params: {
            username: decodeURIComponent(username),
          },
        })
      }

      updateApi={(data) =>
        api.post("/api/user/update", data, {
          params: {
            oldUsername: data.username, 
          },
        })
      }

      redirectRoute="/user/list"
      identifierParam="username"

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