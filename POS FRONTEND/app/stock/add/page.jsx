import CommonAddTemplate from "../../components/CommonAddTemplate";
import { stockFields } from "../stockFields";

export default function StockAdd() {
  return (
    <CommonAddTemplate
      title="Stock"
      apiPath="stock"
      extraFields={stockFields}
      onSuccessPath="/stock"
      showDescription={false}
    />
  );
}