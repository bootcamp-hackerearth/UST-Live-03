import CommonAddTemplate from "../../components/CommonAddTemplate";
import { categoryFields } from "../categoryFields";

export default function CategoryAdd() {
  return (
    <CommonAddTemplate
      title="Category"
      apiPath="category"
      extraFields={categoryFields}
      onSuccessPath="/category"
      showDescription={false}
    />
  );
}