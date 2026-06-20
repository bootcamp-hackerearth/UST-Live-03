"use client";
import CrudPage from "@/components/CrudPage";
import { fetchWithAuth } from "../../lib/api";
import { validators } from "@/lib/security";
function validateEmail(val) {
  if (!String(val ?? "").trim()) return "Email is required.";
  if (!validators.email(val)) return "Enter a valid email address.";
  return null;
}

function validateName(val) {
  if (!String(val ?? "").trim()) return "Full name is required.";
  return null;
}

function validatePhone(val) {
  if (!String(val ?? "").trim()) return "Phone number is required.";
  if (!validators.phone(val)) return "Phone must be exactly 10 digits.";
  return null;
}

function validatePassword(val) {
  const v = String(val ?? "");
  if (!v) return "Password is required.";
  if (!validators.passwordStrength(v))
    return "Password must be 8+ chars with uppercase, lowercase, digit, and special character.";
  return null;
}

function validateRoles(val) {
  if (!Array.isArray(val) || val.length === 0)
    return "Please select at least one Role.";
  return null;
}

export default function UsersPage() {
  return (
    <CrudPage
      config={{
        title: "User Management",
        singularTitle: "User",
        listEndpoint: "/api/user/list",
        getEndpoint: (u) => `/api/user/${u}`,
        saveEndpoint: "/api/security/register",
        updateEndpoint: (u) => `/api/user/update/${u}`,
        deleteEndpoint: (u) => `/api/user/delete/${u}`,
        idKey: "username",
        onDeleteSelf: () => {
          globalThis.window?.localStorage.removeItem("token");
          globalThis.window?.localStorage.removeItem("username");
          globalThis.window.location.href = "/login";
        },
        getCurrentUserId: () =>
          globalThis.window?.localStorage.getItem("username") ?? null,
        loadOptions: async () => {
          const response = await fetchWithAuth("/api/role/list", {
            method: "POST",
            body: JSON.stringify({ page: 0, sizePerPage: 100 }),
          });
          const list = Array.isArray(response) ? response : (response?.dtoList ?? []);
          return {
            roles: list.map((role) => ({
              value: role.identifier,
              label: role.identifier,
            })),
          };
        },
        fields: [
          {
            key: "username",
            label: "Email",
            type: "email",
            required: true,
            readOnlyOnEdit: true,
            validate: validateEmail,
          },
          {
            key: "name",
            label: "Full Name",
            type: "text",
            required: true,
            validate: validateName,
          },
          {
            key: "phoneNo",
            label: "Phone",
            type: "tel",
            required: true,
            maxLength: 10,
            validate: validatePhone,
          },
          {
            key: "password",
            label: "Password",
            type: "password",
            required: true,
            hideInList: true,
            hideOnEdit: true,
            validate: validatePassword,
          },
          {
            key: "roles",
            label: "Roles",
            type: "select",
            required: true,
            multiple: true,
            options: [],
            validate: validateRoles,
          },
          {
            key: "createdBy",
            label: "Created By",
            type: "text",
            hideInForm: true,
            hideInList: true,
          },
          {
            key: "createdAt",
            label: "Created At",
            type: "text",
            hideInForm: true,
            hideInList: true,
          },
          {
            key: "modifiedBy",
            label: "Modified By",
            type: "text",
            hideInForm: true,
            hideInList: true,
          },
          {
            key: "modifiedAt",
            label: "Modified At",
            type: "text",
            hideInForm: true,
            hideInList: true,
          },
        ],
      }}
    />
  );
}
