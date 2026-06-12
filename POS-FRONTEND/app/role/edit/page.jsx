"use client";

import { useMemo } from "react";
import { useSearchParams } from "next/navigation";
import CommonUpdateTemplate from "../../components/CommonUpdateTemplate";

export default function RoleEdit() {
  const searchParams = useSearchParams();
  const identifier = searchParams?.get("identifier") || "";

  const extraFields = useMemo(
    () => [
      {
        key: "description",
        label: "Description",
        type: "text",
        placeholder: "Enter description",
        required: false,
      },
    ],
    []
  );

  if (!identifier) {
    return (
      <div className="w-full max-w-3xl mx-auto py-16 text-center text-slate-600">
        No role selected for editing.
      </div>
    );
  }

  return (
    <CommonUpdateTemplate
      title="Role"
      apiPath="role"
      recordId={identifier}
      recordParam="identifier"
      identifierKey="identifier"
      identifierLabel="Identifier"
      extraFields={extraFields}
      showDescription={false}
      identifierEditable={false}
      onSuccessPath="/role"
    />
  );
}