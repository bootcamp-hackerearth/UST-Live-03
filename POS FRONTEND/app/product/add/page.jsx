import CommonAddTemplate from "../../components/CommonAddTemplate";
import { productFields } from "../productFields";

export default function ProductAdd() {
  return (
    <CommonAddTemplate
      title="Product"
      apiPath="product"
      extraFields={productFields}
      onSuccessPath="/product"
    />
  );
}