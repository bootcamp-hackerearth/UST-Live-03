"use client";
import CommonAdd from "@/components/common/CommonAdd";

export default function AddShelf() {
  return (
    <CommonAdd
      title="Add Shelf"
      subtitle="Create a new shelf"
      entityConfig={{ entityName: "shelf", successMessage: "Shelf added successfully" }}
      cancelPath="/shelves"
    />
  );
}