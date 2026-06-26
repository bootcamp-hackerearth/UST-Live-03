"use client";
import CommonEdit from "@/components/common/CommonEdit";

export default function EditRole() {
  return (
    <CommonEdit
      title="Edit Role"
      subtitle="Update Role"
      entityType="role"
      redirectPath="/roles"
      checkSuccess={true}
      cancelPath="/roles"
    />
  );
}