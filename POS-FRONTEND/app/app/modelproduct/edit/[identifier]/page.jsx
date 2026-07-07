"use client";

import { useParams } from "next/navigation";
import Update from "../../../../components/edit";
import { useAuditField } from "../../../../utils/useAuditField";

export default function EditModelProduct() {
  const params = useParams();
  const auditField = useAuditField();

  const extraFields = [auditField];

  return (
    <Update
      identifier={params.identifier}
      apiPath="modelProduct"
      title="Model Product"
      extraFields={extraFields}
      showDescription={false}
    />
  );
}
