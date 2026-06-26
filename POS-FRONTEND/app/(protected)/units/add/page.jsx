"use client";
import CommonAdd from "@/components/common/CommonAdd";

export default function AddUnit() {
  return (
    <CommonAdd
      title="Add Unit"
      subtitle="Create a new unit"
      entityConfig={{ entityName: "unit", successMessage: "Unit added successfully" }}
      cancelPath="/units"
    />
  );
}