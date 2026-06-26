"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import { entitySubmit } from "@/app/components/EntitySubmitHelper";
import {
  DEFAULT_ENTITY_VALUES,
  getEntityFields,
} from "@/app/components/entityFields";

export default function AddUnitPage() {
  const handleSubmit = async (data) =>
    entitySubmit(
      "/api/unit/add",
      {
        identifier: data.identifier,
        description: data.description,
        status: data.status === true || data.status === "true",
      },
      "Unit added successfully"
    );

  return (
    <CommonAddPage
      title="Add Unit"
      submitApi={handleSubmit}
      redirectRoute="/unit/list"
      initialValues={DEFAULT_ENTITY_VALUES}
      fields={getEntityFields("Unit")}
    />
  );
}