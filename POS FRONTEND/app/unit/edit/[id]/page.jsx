import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { unitFields } from "../../unitFields";

export default function UnitUpdate() {
  return (
    <CommonUpdateTemplate
      title="Unit"
      apiPath="unit"
      recordParam="id"
      extraFields={unitFields}
      onSuccessPath="/unit"
      showDescription={false}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
    />
  );
}