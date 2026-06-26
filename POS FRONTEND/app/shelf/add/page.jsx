import CommonAddTemplate from "../../components/CommonAddTemplate";
import { shelfFields } from "../shelfFields";

export default function ShelfAdd() {
  return (
    <CommonAddTemplate
      title="Shelf"
      apiPath="shelf"
      extraFields={shelfFields}
      onSuccessPath="/shelf"
      showDescription={false}
    />
  );
}