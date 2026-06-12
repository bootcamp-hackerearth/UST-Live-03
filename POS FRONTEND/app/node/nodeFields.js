import { STATUS_FIELD } from "../lib/fieldUtils";

export const nodeFields = [
  { key: "path", label: "Path", type: "text", placeholder: "e.g. brand/list", required: true },
  { key: "roles", label: "Roles", type: "multiselect", apiEndpoint: "/role/findAllActive", asArray: true, required: true },
  STATUS_FIELD,
];