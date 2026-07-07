"use client";

import { useParams } from "next/navigation";
import Update from "../../../../components/edit";
import { useAuditField } from "../../../../utils/useAuditField";

export default function EditUnit() {
  const params = useParams();
  const auditField = useAuditField();

  const extraFields = [auditField];

  return (
    <Update
      identifier={decodeURIComponent(params.identifier)}
      apiPath="unit"
      title="Unit"
      extraFields={extraFields}
      showDescription={true}
    />
  );
}
