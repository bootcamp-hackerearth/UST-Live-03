"use client";

import CommonForm from "@/components/CommonForm";

export default function AddShelfPage() {

  const fields = [
    {
      name: "identifier",
      label: "Shelf Name",
      type: "text",
      required: true
    },
    {
      name: "description",
      label: "Description",
      type: "text",
      required: true
    }
  ];

  return (
    <div className="min-h-screen flex justify-center items-center bg-slate-100 p-6">

      <div className="w-full max-w-md bg-white p-6 rounded-xl shadow">

        <h2 className="text-xl font-semibold mb-4 text-center">
          Add Shelf
        </h2>

        <CommonForm
          api="/shelf"
          fields={fields}
        />

      </div>

    </div>
  );
}