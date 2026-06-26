"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";

export default function EditWarehousePage() {

  const { identifier } = useParams();

  const fields = [
    {
      name: "identifier",
      label: "Warehouse Name",
      type: "text",
      readOnly: true
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
      type: "text",
      required: true,
    },

  ];

  return (
    <div className="min-h-screen flex justify-center items-center bg-slate-100 p-6">

      <div className="w-full max-w-md bg-white p-6 rounded-xl shadow">

        <h2 className="text-xl font-semibold mb-4 text-center">
          Update Warehouse
        </h2>

        <CommonForm
          api="/warehouse"
          mode="edit"
          identifier={identifier}
          fields={fields}
        />

      </div>

    </div>
  );
}