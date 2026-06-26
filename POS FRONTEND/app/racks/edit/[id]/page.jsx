import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { racksFields } from "../../racksFields";

export default function RacksUpdate() {
  return (
    <CommonUpdateTemplate
      title="Rack"
      apiPath="racks"
      recordParam="id"
      extraFields={racksFields}
      onSuccessPath="/racks"
      showDescription={false}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
      showAuditSummary={true}
    />
  );
}