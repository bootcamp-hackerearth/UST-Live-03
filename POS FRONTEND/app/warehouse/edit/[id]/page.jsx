import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { warehouseFields } from "../../warehouseFields";

export default function WarehouseUpdate() {
  return (
    <CommonUpdateTemplate
      title="Warehouse"
      apiPath="warehouse"
      recordParam="id"
      extraFields={warehouseFields}
      onSuccessPath="/warehouse"
      showDescription={false}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
      showAuditSummary={true}
    />
  );
}