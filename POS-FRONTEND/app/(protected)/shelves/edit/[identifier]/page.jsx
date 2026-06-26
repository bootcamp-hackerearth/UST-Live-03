"use client";
import CommonEdit from "@/components/common/CommonEdit";

export default function EditShelf() {
  return (
    <CommonEdit
      title="Edit Shelf"
      subtitle="Update shelf details"
      entityType="shelf"
      redirectPath="/shelves"
      cancelPath="/shelves"
    />
  );
}