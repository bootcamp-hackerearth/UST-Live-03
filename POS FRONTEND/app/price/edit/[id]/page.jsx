import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { priceFields } from "../../priceFields";

export default function PriceUpdate() {
  return (
    <CommonUpdateTemplate
      title="Price"
      apiPath="price"
      recordParam="id"
      extraFields={priceFields}
      onSuccessPath="/price"
      showDescription={false}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
      showAuditSummary={true}
    />
  );
}
