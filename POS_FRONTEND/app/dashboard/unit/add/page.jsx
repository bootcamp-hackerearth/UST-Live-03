"use client";

import CommonForm from "@/components/CommonForm";

export default function AddUnitPage() {

  const fields = [
    {
      name: "identifier",
      label: "Unit Name",
      type: "text",
      required: true
    },
    {
      name: "description",
      label: "Description",
      type: "text"
    }
  ];

  return (
    <div className="min-h-screen flex justify-center items-center bg-slate-100 p-6">

      <div className="w-full max-w-md bg-white p-6 rounded-xl shadow">

        <h2 className="text-xl font-semibold mb-4 text-center">
          Add Unit
        </h2>

        <CommonForm
          api="/unit"
          fields={fields}
        />

      </div>

    </div>
  );
}
