"use client";
import CommonEdit from "@/components/common/CommonEdit";

export default function EditBrandPage() {
  return (
    <CommonEdit
      title="Edit Brand"
      subtitle="Update brand details"
      entityType="brand"
      redirectPath="/brands"
      checkSuccess={true}
      buildPayload={({identifier,description}) => ({identifier,description:description.trim()})}
      cancelPath="/brands"
    />
  );
}