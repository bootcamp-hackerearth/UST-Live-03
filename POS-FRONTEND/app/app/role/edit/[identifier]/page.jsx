"use client";

import { useParams } from "next/navigation";
import Update from "../../../../components/edit";
import { useAuditField } from "../../../../utils/useAuditField";

export default function EditRole() {
  const params = useParams();
  const auditField = useAuditField();

  const extraFields = [auditField];

  return (
    <Update
      identifier={decodeURIComponent(params.identifier)}
      apiPath="role"
      title="Role"
      showDescription={true}
      extraFields={extraFields}
    />
  );
}
