"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";

export default function EditUserPage() {
  const { username } = useParams();

  const fields = [
    {
      name: "username",
      label: "Email",
      type: "text",
      required: true,
      readOnly: true
    },
    {
      name: "name",
      label: "Full Name",
      type: "text"
    },
    {
      name: "phoneNo",
      label: "Phone Number",
      type: "text"
    },
    {
      name: "roles",
      label: "Roles",
      type: "select",
      multiple: true,
      api: "/role/list"
    }
  ];

  return (
    <div className="min-h-screen bg-slate-100 flex justify-center items-center p-6">

      <div className="w-full max-w-4xl bg-white rounded-2xl shadow-xl border p-6">

        <div className="mb-6">
          <h1 className="text-xl font-semibold">Edit User</h1>
          <p className="text-sm text-gray-500">
            Update user details and permissions
          </p>
        </div>

        <CommonForm
          api="/user"
          mode="edit"
          identifier={username}
          identifierParam="username"
          fields={fields}
        />

      </div>
    </div>
  );
}