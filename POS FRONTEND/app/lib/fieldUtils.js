export function toArray(val) {
  if (Array.isArray(val)) return val;
  if (val) return [val];
  return [];
}

export function applyFieldTransforms(payload, extraFields) {
  extraFields.forEach((field) => {
    if (field.asArray) {
      payload[field.key] = toArray(payload[field.key]);
    }
    if (field.valueType === "boolean") {
      payload[field.key] = payload[field.key] === true || payload[field.key] === "true";
    }
  });
}

export const STATUS_FIELD = {
  key: "status",
  label: "Status",
  type: "select",
  valueType: "boolean",
  required: true,
  options: [
    { label: "Active", value: "true" },
    { label: "Inactive", value: "false" },
  ],
};