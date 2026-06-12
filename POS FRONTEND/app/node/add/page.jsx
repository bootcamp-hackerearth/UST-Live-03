import CommonAddTemplate from "../../components/CommonAddTemplate";
import { nodeFields } from "../nodeFields";

export default function NodeAdd() {
  return (
    <CommonAddTemplate
      title="Node"
      apiPath="node"
      extraFields={nodeFields}
      onSuccessPath="/node"
      showDescription={false}
    />
  );
}