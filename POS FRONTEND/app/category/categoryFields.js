import { STATUS_FIELD } from "../lib/fieldUtils";

export const categoryFields = [
  { key: "supercategory", label: "Supercategory", type: "select", apiPath: "category", required: false, placeholder: "None (Top-level category)" },
  STATUS_FIELD,
];