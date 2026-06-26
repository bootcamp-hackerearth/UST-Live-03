import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { stockFields } from "../../stockFields";

export default function StockUpdate() {
  return (
    <CommonUpdateTemplate
      title="Stock"
      apiPath="stock"
      recordParam="identifier"
      extraFields={stockFields}
      onSuccessPath="/stock"
      showDescription={false}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
      showAuditSummary={true}
    />
  );
}