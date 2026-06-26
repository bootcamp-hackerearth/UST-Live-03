export function formatAuditDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return new Intl.DateTimeFormat("en-IN", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(date);
}

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

export function digitsOnly(str) {
  if (!str || typeof str !== "string") return "";
  let out = "";
  for (let i = 0; i < str.length; i++) {
    const code = str.codePointAt(i);
    if (code >= 48 && code <= 57) out += str.charAt(i);
  }
  return out;
}