"use client";

import { useEffect, useState } from "react";
import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";

const emailRegex = /^[^\s@]{1,64}@[^\s@]{1,255}\.[^\s@]{2,10}$/;
const phoneRegex = /^[6-9]\d{9}$/;
const passwordRegex =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,64}$/;

export default function UserAddPage() {
  const [roleOptions, setRoleOptions] = useState([]);

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const res = await api.post("/api/role/list", {
          page: 0,
          sizePerPage: 100,
          sortField: "identifier",
          sortDirection: "ASC",
        });

        const roles = res.data?.dtoList || [];

        const formatted = roles
          .filter((r) => r?.identifier)
          .map((r) => ({
            label: r.identifier,
            value: r.identifier,
          }));

        setRoleOptions(formatted);

      } catch (err) {
        console.error("ROLE FETCH ERROR ", err);
      }
    };

    fetchRoles();
  }, []);

  return (
    <CommonAddPage
      title="Add User"

      submitApi={(data) => {
        console.log("Submitting User:", data);

        if (
          !data.username ||
          !data.name ||
          !data.phoneNo ||
          !data.password ||
          !data.roles?.length
        ) {
          alert("Please fill all required fields");
          return;
        }

        if (!emailRegex.test(data.username)) {
          alert("Invalid email format");
          return;
        }

        if (!phoneRegex.test(String(data.phoneNo))) {
          alert("Invalid phone number");
          return;
        }

        if (!passwordRegex.test(data.password)) {
          alert(
            "Password must be 8+ chars with uppercase, lowercase, number & special char"
          );
          return;
        }

        return api.post("/api/user/register", {
          username: data.username,
          name: data.name,
          phoneNo: data.phoneNo,
          password: data.password,
          roles: data.roles,
          isActive:
            data.isActive === true ||
            data.isActive === "true",
        });
      }}

      redirectRoute="/user/list"

      initialValues={{
        username: "",
        name: "",
        phoneNo: "",
        password: "",
        roles: [],
        isActive: true,
      }}

      fields={[
        {
          name: "username",
          label: "Email",
          type: "text",
        },
        {
          name: "name",
          label: "Full Name",
          type: "text",
        },
        {
          name: "phoneNo",
          label: "Phone Number",
          type: "number",
        },
        {
          name: "password",
          label: "Password",
          type: "password",
        },

        {
          name: "roles",
          label: "Roles",
          type: "dropdown",
          multiple: true,
          options: roleOptions,
        },

        {
          name: "isActive",
          label: "Status",
          type: "radio",
          options: [
            { label: "Active", value: true },
            { label: "Inactive", value: false },
          ],
        },
      ]}
    />
  );
}
