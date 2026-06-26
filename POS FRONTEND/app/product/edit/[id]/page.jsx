import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { productFields } from "../../productFields";

export default function ProductUpdate() {
  return (
    <CommonUpdateTemplate
      title="Product"
      apiPath="product"
      recordParam="id"
      extraFields={productFields}
      onSuccessPath="/product"
      showDescription={true}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
      showAuditSummary={true}
    />
  );
}
