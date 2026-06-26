"use client";

import CommonForm from "@/components/CommonForm";

export default function NodeAddPage() {

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      required: true
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
      api: "/role/list",
      required: true
    }
  ];

  return (
    <div className="min-h-screen bg-[#F6F7F9] flex justify-center items-center p-6">

      <div className="w-full max-w-3xl bg-white border border-[#E5E7EB] rounded-2xl shadow-md p-6">

        <div className="mb-6">
          <h1 className="text-xl font-semibold text-[#111827]">
            Add Node
          </h1>
          <p className="text-sm text-gray-500">
            Create navigation node and assign roles
          </p>
        </div>

        <CommonForm
          api="/node"
          mode="add"
          fields={fields}
        />

      </div>

    </div>
  );
}