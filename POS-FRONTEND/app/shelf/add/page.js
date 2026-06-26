"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import { entitySubmit } from "@/app/components/EntitySubmitHelper";
import {
  DEFAULT_ENTITY_VALUES,
  getEntityFields,
} from "@/app/components/entityFields";

export default function ShelfAddPage() {
  const handleSubmit = async (data) =>
    entitySubmit(
      "/api/shelf/add",
      {
        identifier: data.identifier,
        description: data.description,
        status: data.status === true || data.status === "true",
      },
      "Shelf added successfully"
    );

  return (
    <CommonAddPage
      title="Add Shelf"
      submitApi={handleSubmit}
      redirectRoute="/shelf/list"
      initialValues={DEFAULT_ENTITY_VALUES}
      fields={getEntityFields("Shelf")}
    />
  );
}