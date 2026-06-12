"use client";

import EditFormSkeleton from "../../../components/EditFormSkeleton";

export default function EditRole() {
  const extraFields = [
    {
      key: "description",
      label: "Description",
      type: "text",
      required: true,
    },
  ];

  return (
    <EditFormSkeleton
      title="Role"
      apiPath="role"
      extraFields={extraFields}
    />
  );
}