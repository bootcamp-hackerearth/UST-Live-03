"use client";

import { useSearchParams } from "next/navigation";
import EditFormSkeleton from "@/components/CommonEditForm";

export default function EditRole() {
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