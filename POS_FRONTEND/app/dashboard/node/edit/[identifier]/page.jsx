"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";

export default function NodeEditPage() {

  const { identifier } = useParams();

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      required: true,
      readOnly: true
    },
    {
      name: "path",
      label: "Path",
      type: "text",
      required: true
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
    <div className="min-h-screen bg-[#F6F7F9] flex justify-center items-center p-6">

      <div className="w-full max-w-3xl bg-white border border-[#E5E7EB] rounded-2xl shadow-md p-6">

        <div className="mb-6">
          <h1 className="text-xl font-semibold text-[#111827]">
            Edit Node
          </h1>
          <p className="text-sm text-gray-500">
            Update node details
          </p>
        </div>

        <CommonForm
          api="/node"
          mode="edit"
          identifier={identifier}
          fields={fields}
        />

      </div>

    </div>
  );
}