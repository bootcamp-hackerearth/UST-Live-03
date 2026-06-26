import CommonAddTemplate from "../../components/CommonAddTemplate";
import { modelsFields } from "../modelsFields";

export default function ModelsAdd() {
  return (
    <CommonAddTemplate
      title="Model"
      apiPath="models"
      extraFields={modelsFields}
      onSuccessPath="/models"
      showDescription={false}
    />
  );
}