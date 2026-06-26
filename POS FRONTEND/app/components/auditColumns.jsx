import { formatAuditDate } from "../lib/fieldUtils";

export function auditColumns() {
  return [
    {
      label: "Created",
      field: "createdOn",
      render: (item) => (
        <div className="min-w-40">
          <p className="font-medium text-slate-900">{formatAuditDate(item.createdOn)}</p>
          <p className="text-xs text-slate-500">{item.createdBy || "system"}</p>
        </div>
      ),
    },
    {
      label: "Modified",
      field: "modifiedOn",
      render: (item) => (
        <div className="min-w-40">
          <p className="font-medium text-slate-900">{formatAuditDate(item.modifiedOn)}</p>
          <p className="text-xs text-slate-500">{item.modifiedBy || "-"}</p>
        </div>
      ),
    },
  ];
}
