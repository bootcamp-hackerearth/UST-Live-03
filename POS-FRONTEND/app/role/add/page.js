"use client";

import AddFormSkeleton from "../../components/AddFormSkeleton";

export default function AddRole() {
  const extraFields = [
    { key: "description", label: "Description", type: "text", required: true },
  ];

  return (
    <AddFormSkeleton
      title="Role"
      apiPath="role"
      extraFields={extraFields}
    />
  );
}