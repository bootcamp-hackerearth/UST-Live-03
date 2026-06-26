import { getStandardColumns, getStandardFormFields } from "./baseSchema";

export const unit = {
  endpoint: "/api/unit/list",
  toggleEndpoint: "/api/unit/toggle",
  addEndpoint: "/api/unit/add",
  updateEndpoint: "/api/unit/update",
  deleteEndpoint: "/api/unit/delete",
  getEndpoint: "/api/unit/get",
  getParam: "identifier",
  entityName: "unit",
  columns: getStandardColumns("unit Name"),
  formFields: getStandardFormFields("unit Name")
};