"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";

export default function EditModelPage() {

  const { identifier } = useParams();

  const fields = [
    {
      name: "identifier",
      label: "Model Name",
      type: "text",
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
    <div className="min-h-screen flex justify-center items-center bg-slate-100 p-6">

      <div className="w-full max-w-md bg-white p-6 rounded-xl shadow">

        <h2 className="text-xl font-semibold mb-4 text-center">
          Update Model
        </h2>

        <CommonForm
          api="/model"
          mode="edit"
          identifier={identifier}
          fields={fields}
        />

      </div>

    </div>
  );
}