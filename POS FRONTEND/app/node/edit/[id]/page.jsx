import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { nodeFields } from "../../nodeFields";

export default function NodeUpdate() {
  return (
    <CommonUpdateTemplate
      title="Node"
      apiPath="node"
      recordParam="identifier"
      extraFields={nodeFields}
      onSuccessPath="/node"
      showDescription={false}
    />
  );
}