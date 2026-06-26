import CommonAddTemplate from "../../components/CommonAddTemplate";
import { racksFields } from "../racksFields";

export default function RacksAdd() {
  return (
    <CommonAddTemplate
      title="Rack"
      apiPath="racks"
      extraFields={racksFields}
      onSuccessPath="/racks"
      showDescription={false}
    />
  );
}
