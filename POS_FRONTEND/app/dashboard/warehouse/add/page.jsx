"use client";

import CommonForm from "@/components/CommonForm";

export default function AddWarehousePage() {

  const fields = [
    {
      name: "identifier",
      label: "Warehouse Name",
      type: "text",
      required: true
    },
    {
      name: "region",
      label: "Region",
      type: "text",
      required: true
    },
    {
      name: "country",
      label: "Country",
      type: "text",
      required: true
    },
    {
      name: "location",
      label: "Location",
      type: "text",
      required: true
    },
    {
      name: "contactName",
      label: "Contact Name",
      type: "text",
      required: true
    },
    {
      name: "contactNumber",
      label: "Contact Number",
      type: "tel",
      maxLength: 10,
      onInput: (e) => {
        e.target.value = e.target.value.replaceAll(/\D/g, "").slice(0, 10);
      }
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
          Add Warehouse
        </h2>

        <CommonForm
          api="/warehouse"
          fields={fields}
        />

      </div>

    </div>
  );
}
