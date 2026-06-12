"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";

export default function RoleEditPage() {

  const { identifier } = useParams();

  const fields = [
    {
      name: "identifier",
      label: "Role Name",
      type: "text",
      required: true,
      readOnly: true
    },
    {
      name: "description",
      label: "Description",
      type: "text",
      required: true
    }
  ];

  return (
    <div className="min-h-screen bg-[#F6F7F9] flex justify-center items-center p-6">

      <div className="w-full max-w-2xl bg-white border border-[#E5E7EB] rounded-2xl shadow-md p-6">

        <div className="mb-6">
          <h1 className="text-xl font-semibold text-[#111827]">
            Edit Role
          </h1>
          <p className="text-sm text-gray-500">
            Update role details
          </p>
        </div>

        <CommonForm
          api="/role"
          mode="edit"
          identifier={identifier}
          fields={fields}
        />

      </div>

    </div>
  );
}