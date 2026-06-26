"use client";
import CommonEdit from "@/components/common/CommonEdit";

export default function EditModel() {
  return (
    <CommonEdit
      title="Edit Model"
      subtitle="Update model details"
      entityType="model"
      redirectPath="/models"
      cancelPath="/models"
    />
  );
}