"use client";

import { useParams } from "next/navigation";
import Update from "../../../../components/edit";
import { useAuditField } from "../../../../utils/useAuditField";

export default function EditWarehouse() {
  const params = useParams();

  const identifier = decodeURIComponent(params.identifier);
  const auditField = useAuditField();

  const extraFields = [
    {
      key: "country",
      label: "Country",
      required: true,
    },
    {
      key: "pincode",
      label: "Pincode",
      required: true,
    },
    {
      key: "address",
      label: "Address",
      required: true,
    },
    auditField,
  ];

  return (
    <Update
      identifier={identifier}
      apiPath="warehouse"
      title="Warehouse"
      extraFields={extraFields}
      showDescription={false}
    />
  );
}
