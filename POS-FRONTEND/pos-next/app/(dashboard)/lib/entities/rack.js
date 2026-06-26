import { getStandardColumns, getStandardFormFields } from "./baseSchema";

export const rack = {
  endpoint: "/api/rack/list",
  toggleEndpoint: "/api/rack/toggle",
  addEndpoint: "/api/rack/add",
  updateEndpoint: "/api/rack/update",
  deleteEndpoint: "/api/rack/delete",
  getEndpoint: "/api/rack/get",
  getParam: "identifier",
  entityName: "rack",
  columns: getStandardColumns("rack Name"),
  formFields: getStandardFormFields("rack Name")
};