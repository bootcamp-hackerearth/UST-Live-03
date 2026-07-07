"use client";

import { useParams } from "next/navigation";
import Update from "../../../../components/edit";
import { useAuditField } from "../../../../utils/useAuditField";

export default function EditBrand() {
  const params = useParams();
  const auditField = useAuditField();

  const extraFields = [auditField];

  return (
    <Update
      identifier={params.identifier}
      apiPath="brand"
      title="Brand"
      extraFields={extraFields}
      showDescription={true}
      urlMethod={"post"}
    />
  );
}
