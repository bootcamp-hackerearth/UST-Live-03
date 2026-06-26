import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { shelfFields } from "../../shelfFields";

export default function ShelfUpdate() {
  return (
    <CommonUpdateTemplate
      title="Shelf"
      apiPath="shelf"
      recordParam="identifier"
      extraFields={shelfFields}
      onSuccessPath="/shelf"
      showDescription={false}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
      showAuditSummary={true}
    />
  );
}