"use client";
import CommonAdd from "@/components/common/CommonAdd";

export default function AddModel() {
  return (
    <CommonAdd
      title="Add Model"
      subtitle="Create a new model"
      entityConfig={{ entityName: "model", successMessage: "Model added successfully" }}
      cancelPath="/models"
    />
  );
}