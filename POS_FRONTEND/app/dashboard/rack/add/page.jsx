"use client";

import CommonForm from "@/components/CommonForm";

export default function AddRackPage() {

  const fields = [
    {
      name: "identifier",
      label: "Rack Name",
      type: "text",
      required: true
    },
    {
      name: "shelves",
      label: "Shelves",
      type: "select",
      multiple: true,
      api: "/shelf/list",
      required: true
    }
  ];

  return (
    <div className="min-h-screen flex justify-center items-center bg-slate-100 p-6">

      <div className="w-full max-w-3xl bg-white p-6 rounded-xl shadow">

        <h1 className="text-xl font-semibold mb-4">
          Add Rack
        </h1>

        <CommonForm
          api="/rack"
          fields={fields}
        />

      </div>

    </div>
  );
}