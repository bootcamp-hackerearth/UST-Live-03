import CommonAddTemplate from "../../components/CommonAddTemplate";
import { priceFields } from "../priceFields";

export default function PriceAdd() {
  return (
    <CommonAddTemplate
      title="Price"
      apiPath="price"
      identifierApiEndpoint="/product/findAllActive"
      identifierOptionValue="identifier"
      identifierOptionLabel="identifier"
      extraFields={priceFields}
      onSuccessPath="/price"
      showDescription={false}
    />
  );
}