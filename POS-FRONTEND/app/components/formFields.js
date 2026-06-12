
export const createDropdownField = (
  label,
  name,
  apiUrl,
  customOptions = {}
) => {
  return {
    label,
    name,
    type: "dropdown",
    api: apiUrl,
    payload: {
      page: 0,
      sizePerPage: 100,
      sortField: "identifier",
      sortDirection: "ASC",
    },
    optionLabel: "identifier",
    optionValue: "identifier",
    placeholder: `Select ${label}`,
    ...customOptions,
  };
};
