"use client";
import CommonList from "@/components/table/CommonList";

const modelColumns = [
  {header: "Model Name",field: "identifier"},
  {header: "Description",field: "description"},
];

export default function ModelsPage() {
  
  return (
    <CommonList
      title="Models"
      subtitle="Manage vehicle models"
      entity="model"
      addPath="/models/add"
      editPath="/models/edit"
      columns={modelColumns}
      showToggle={true}
    />
  );
}