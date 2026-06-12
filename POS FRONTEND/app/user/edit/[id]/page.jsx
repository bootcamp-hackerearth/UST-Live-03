import CommonUpdateTemplate from "../../../components/CommonUpdateTemplate";
import { STATUS_FIELD } from "../../../lib/fieldUtils";

const extraFields = [
  {
    key: "name",
    label: "Name",
    type: "text",
    required: true,
  },
  {
    key: "phoneNo",
    label: "Phone Number",
    type: "text",
    required: true,
  },
  {
    key: "roles",
    label: "Roles",
    type: "multiselect",
    apiPath: "role",
    required: true,
  },
  STATUS_FIELD,
];

export default function UserUpdate() {
  return (
    <CommonUpdateTemplate
      title="User"
      apiPath="user"
      recordParam="username"
      showIdentifier={false}
      showDescription={false}
      showIdentifierFallback={true}
      extraFields={extraFields}
      onSuccessPath="/user"
    />
  );
}