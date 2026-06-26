import { getStandardColumns, getStandardFormFields } from "./baseSchema";

export const shelf = {
  endpoint: "/api/shelf/list",
  toggleEndpoint: "/api/shelf/toggle",
  addEndpoint: "/api/shelf/add",
  updateEndpoint: "/api/shelf/update",
  deleteEndpoint: "/api/shelf/delete",
  getEndpoint: "/api/shelf/get",
  getParam: "identifier",
  entityName: "shelf",
  columns: getStandardColumns("shelf Name"),
  formFields: getStandardFormFields("shelf Name")
};