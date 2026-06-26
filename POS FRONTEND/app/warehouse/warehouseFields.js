import { STATUS_FIELD } from "../lib/fieldUtils";

export const warehouseFields = [
  { key: "code", label: "Code", type: "text", required: true },
  { key: "location", label: "Location", type: "text", required: true },
  { key: "address", label: "Address", type: "text", required: false },
  STATUS_FIELD,
];