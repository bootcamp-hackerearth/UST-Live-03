import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { brandFields } from "../../brandFields";

export default function BrandUpdate() {
  return (
    <CommonUpdateTemplate
      title="Brand"
      apiPath="brand"
      recordParam="identifier"
      extraFields={brandFields}
      onSuccessPath="/brand"
      showDescription={false}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
      showAuditSummary={true}
    />
  );
}
