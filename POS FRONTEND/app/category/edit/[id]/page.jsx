import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { categoryFields } from "../../categoryFields";

export default function CategoryUpdate() {
  return (
    <CommonUpdateTemplate
      title="Category"
      apiPath="category"
      recordParam="id"
      extraFields={categoryFields}
      onSuccessPath="/category"
      showDescription={false}
      showIdentifier={true}
      identifierLabel="Identifier"
      identifierReadOnly={true}
    />
  );
}