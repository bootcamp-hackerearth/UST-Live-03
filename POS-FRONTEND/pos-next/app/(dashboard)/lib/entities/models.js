import { getStandardColumns, getStandardFormFields } from "./baseSchema";

export const models = {
  endpoint: "/api/models/list",
  toggleEndpoint: "/api/models/toggle",
  addEndpoint: "/api/models/add",
  updateEndpoint: "/api/models/update",
  deleteEndpoint: "/api/models/delete",
  getEndpoint: "/api/models/get",
  getParam: "identifier",
  entityName: "models",
  columns: getStandardColumns("models Name"),
  formFields: getStandardFormFields("models Name")
};