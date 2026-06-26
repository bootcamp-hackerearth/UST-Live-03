import CommonAddTemplate from "../../components/CommonAddTemplate";
import { warehouseFields } from "../warehouseFields";

export default function WarehouseAdd() {
  return (
    <CommonAddTemplate
      title="Warehouse"
      apiPath="warehouse"
      extraFields={warehouseFields}
      onSuccessPath="/warehouse"
      showDescription={false}
    />
  );
}