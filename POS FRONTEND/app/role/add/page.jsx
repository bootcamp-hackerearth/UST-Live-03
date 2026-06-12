import CommonAddTemplate from "../../components/CommonAddTemplate";
import { STATUS_FIELD } from "../../lib/fieldUtils";

export default function RoleAdd() {
  return (
    <CommonAddTemplate
      title="Role"
      apiPath="role"
      extraFields={[STATUS_FIELD]}
      onSuccessPath="/role"
      showDescription={false}
    />
  );
}