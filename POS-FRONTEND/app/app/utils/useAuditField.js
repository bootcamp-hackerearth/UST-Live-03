import { useState } from "react";
import AuditSection from "@/components/AuditSection";

export const useAuditField = () => {
  const [audit, setAudit] = useState({});

  const auditField = {
    key: "audit",
    type: "custom",
    onLoad: (_, full) => {
      setAudit({
        createdBy: full.createdBy,
        createdOn: full.createdOn
          ? new Date(full.createdOn).toLocaleString()
          : "-",
        modifiedBy: full.modifiedBy,
        modifiedOn: full.modifiedOn
          ? new Date(full.modifiedOn).toLocaleString()
          : "-",
      });
    },
    component: <AuditSection data={audit} />,
  };

  return auditField;
};
