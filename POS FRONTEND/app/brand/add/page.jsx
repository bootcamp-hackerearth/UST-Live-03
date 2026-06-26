import CommonAddTemplate from "../../components/CommonAddTemplate";
import { brandFields } from "../brandFields";

export default function BrandAdd() {
  return (
    <CommonAddTemplate
      title="Brand"
      apiPath="brand"
      extraFields={brandFields}
      onSuccessPath="/brand"
      showDescription={false}
    />
  );
}
