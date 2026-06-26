import { STATUS_FIELD } from "../lib/fieldUtils";

export const modelsFields = [
  { key: "brand", label: "Brand", type: "select", apiPath: "brand", optionValue: "identifier", optionLabel: "identifier", required: true },
  STATUS_FIELD,
];