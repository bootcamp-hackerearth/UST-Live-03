import CommonAddTemplate from "../../components/CommonAddTemplate";
import { unitFields } from "../unitFields";

export default function UnitAdd() {
  return (
    <CommonAddTemplate
      title="Unit"
      apiPath="unit"
      extraFields={unitFields}
      onSuccessPath="/unit"
      showDescription={false}
    />
  );
}