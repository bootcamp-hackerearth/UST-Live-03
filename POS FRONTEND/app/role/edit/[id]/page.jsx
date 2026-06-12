import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { STATUS_FIELD } from "../../../lib/fieldUtils";

export default function RoleUpdate() {
  return (
    <CommonUpdateTemplate
      title="Role"
      apiPath="role"
      recordParam="identifier"
      extraFields={[STATUS_FIELD]}
      onSuccessPath="/role"
      showDescription={false}
    />
  );
}