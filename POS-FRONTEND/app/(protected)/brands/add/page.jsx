"use client";
import CommonAdd from "@/components/common/CommonAdd";

export default function AddBrand() {
  return (
    <CommonAdd
      title="Add Brand"
      subtitle="Create a new product brand"
      entityConfig={{ entityName: "brand", successMessage: "Brand created successfully!" }}
      cancelPath="/brands"
    />
  );
}