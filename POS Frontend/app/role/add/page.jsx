"use client";
 
import AddFormSkeleton from "@/components/CommonAddForm";
 
export default function AddRole() {
  return (
    <AddFormSkeleton
      title="Role"
      apiPath="role"
      identifierLabel="Role Name"
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