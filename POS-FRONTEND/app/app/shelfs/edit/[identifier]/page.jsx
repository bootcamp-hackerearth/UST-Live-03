"use client";

import { useParams } from "next/navigation";
import Update from "../../../../components/edit";
import { useAuditField } from "../../../../utils/useAuditField";

export default function EditShelfs() {
  const params = useParams();
  const auditField = useAuditField();

  return (
    <Update
      identifier={params.identifier}
      apiPath="shelfs"
      title="Shelf"
      extraFields={[auditField]}
    />
  );
}
