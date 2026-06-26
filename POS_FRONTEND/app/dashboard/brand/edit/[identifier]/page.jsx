"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";

export default function BrandEditPage() {

  const { identifier } = useParams();

  const fields = [
    {
      name: "identifier",
      label: "Brand Name",
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

      <div className="w-full max-w-3xl bg-white border border-[#E5E7EB] rounded-2xl shadow-md p-6">

        <div className="mb-6">
          <h1 className="text-xl font-semibold text-[#111827]">
            Edit Brand
          </h1>

          <p className="text-sm text-gray-500">
            Update brand details
          </p>
        </div>

        <CommonForm
          api="/brand"
          mode="edit"
          identifier={identifier}
          identifierParam="identifier"
          fields={fields}
        />

      </div>

    </div>
  );
}