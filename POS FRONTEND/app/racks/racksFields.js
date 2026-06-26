import { STATUS_FIELD } from "../lib/fieldUtils";

export const racksFields = [
  {
    key: "shelf",
    label: "Shelves",
    type: "multiselect",
    apiEndpoint: "/shelf/findAllActive",
    asArray: true,
    required: false,
  },
  STATUS_FIELD,
];