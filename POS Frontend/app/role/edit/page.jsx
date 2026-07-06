"use client";

import { Suspense } from "react";
import { useSearchParams } from "next/navigation";
import EditFormSkeleton from "@/components/CommonEditForm";

function EditRoleContent() {
  const searchParams = useSearchParams();
  const identifier = searchParams.get("identifier") || "";

  return (
    <EditFormSkeleton
      title="Role"
      apiPath="role"
      paramName="identifier"
      identifierField="identifier"
      overrideIdentifier={identifier}
      extraFields={[
        {
          key: "description",
          label: "Description",
          type: "text",
        },
      ]}
    />
  );
}

export default function EditRole() {
  return (
    <Suspense fallback={null}>
      <EditRoleContent />
    </Suspense>
  );
}