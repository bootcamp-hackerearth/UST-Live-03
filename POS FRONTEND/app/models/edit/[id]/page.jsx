import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { modelsFields } from "../../modelsFields";

export default function ModelsUpdate() {
  return (
    <CommonUpdateTemplate
      title="Model"
      apiPath="models"
      recordParam="id"
      extraFields={modelsFields}
      onSuccessPath="/models"
      showDescription={false}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
      showAuditSummary={true}
    />
  );
}
