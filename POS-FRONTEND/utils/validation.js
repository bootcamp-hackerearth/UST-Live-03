
export const isMissing = (field, value) => {
  if (!field.required) return false;
  if (Array.isArray(value)) return value.length === 0;
  return value === undefined || value === null || String(value).trim() === "";
};

const validationRules = {
  phoneNo: {
    validate: (value) => !/^\d{10}$/.test(String(value).trim()),
    message: "Phone number must be exactly 10 digits.",
  },
  password: {
    validate: (value) => {
      const password = String(value);
      if (/\s/.test(password)) return "space";
      const passwordPolicy =
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+\-=])[A-Za-z\d@$!%*?&#^()_+\-=]{8,}$/;
      return passwordPolicy.test(password) ? "" : "true";
    },
    messages: {
      true: "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.",
      space: "Password must not contain spaces.",
    },
  },
  username: {
    validate: (value) => !String(value).trim().endsWith("@gmail.com"),
    message: "Username must be a valid Gmail address (e.g. example@gmail.com).",
  },
};

export const validatePhoneNo = (value) => {
  if (!value) return "";
  const rule = validationRules.phoneNo;
  return rule.validate(value) ? rule.message : "";
};

export const validatePassword = (value) => {
  if (!value) return "";
  const rule = validationRules.password;
  const result = rule.validate(value);
  return result ? rule.messages[result] || rule.messages.true : "";
};

export const validateUsername = (value) => {
  if (!value) return "";
  const rule = validationRules.username;
  return rule.validate(value) ? rule.message : "";
};

export const validateField = (field, value) => {
  if (isMissing(field, value)) {
    return `${field.label || field.key} is required.`;
  }

  const rule = validationRules[field.key];
  if (rule) {
    const result = rule.validate(value);
    if (!result) return "";
    if (result === true) return rule.message;
    return rule.messages?.[result] || rule.message;
  }

  return "";
};
