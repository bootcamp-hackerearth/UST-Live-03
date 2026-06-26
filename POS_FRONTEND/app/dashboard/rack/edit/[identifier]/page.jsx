"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";

export default function EditRackPage() {

  const { identifier } = useParams();

  const fields = [
    {
      name: "identifier",
      label: "Rack Name",
      type: "text",
      readOnly: true,
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
          Edit Rack
        </h1>

        <CommonForm
          api="/rack"
          mode="edit"
          identifier={identifier}
          fields={fields}
        />

      </div>

    </div>
  );
}