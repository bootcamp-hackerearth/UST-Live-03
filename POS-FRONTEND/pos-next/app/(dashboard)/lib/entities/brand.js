import { getStandardColumns, getStandardFormFields } from "./baseSchema";

export const brand = {
  endpoint: "/api/brand/list",
  toggleEndpoint: "/api/brand/toggle",
  addEndpoint: "/api/brand/add",
  updateEndpoint: "/api/brand/update",
  deleteEndpoint: "/api/brand/delete",
  getEndpoint: "/api/brand/get",
  getParam: "identifier",
  entityName: "Brand",
  columns: getStandardColumns("Brand Name"),
  formFields: getStandardFormFields("Brand Name")
};