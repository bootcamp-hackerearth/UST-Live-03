"use client";
import CommonEdit from "@/components/common/CommonEdit";

export default function EditUnit() {
  return (
    <CommonEdit
      title="Edit Unit"
      subtitle="Update unit details"
      entityType="unit"
      redirectPath="/units"
      cancelPath="/units"
    />
  );
}