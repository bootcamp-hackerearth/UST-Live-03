export const validateForm = (
  fields,
  formData,
  skipReadOnly = false
) => {
  const newErrors = {};

  fields.forEach((field) => {
    if (skipReadOnly && field.readOnly) {
      return;
    }

    const value = formData[field.name];

    const error =
      validateRequired(field, value) ||
      validateName(field, value) ||
      validatePath(field, value) ||
      validateNumber(field, value) ||
      validateEmailField(field, value) ||
      validatePassword(field, value) ||
      validatePhoneField(field, value);

    if (error) {
      newErrors[field.name] = error;
    }
  });

  return newErrors;
};

const validateRequired = (
  field,
  value
) => {
  if (
    field.required !== false &&
    isEmpty(value)
  ) {
    return `${field.label} is required`;
  }

  return null;
};

const validateName = (
  field,
  value
) => {
  if (
    field.name !== "name" ||
    isEmpty(value)
  ) {
    return null;
  }

  const trimmed =
    value.toString().trim();

  const valid =
    /^[A-Za-z ]{3,50}$/.test(
      trimmed
    );

  if (!valid) {
    return "Name must contain only letters and be 3–50 characters";
  }

  return null;
};

const validatePath = (
  field,
  value
) => {
  if (
    field.name !== "path" ||
    isEmpty(value)
  ) {
    return null;
  }

  const path =
    value.toString();

  if (
    path.startsWith(" ")
  ) {
    return "Path should not start with space";
  }

  const valid =
    /^\/[a-zA-Z0-9/_-]*$/.test(
      path
    );

  if (!valid) {
    return "Path must start with '/' and contain only letters, numbers, -, _";
  }

  return null;
};

const validateNumber = (
  field,
  value
) => {
  if (
    field.type !== "number" ||
    isEmpty(value)
  ) {
    return null;
  }

  const number =
    Number(value);

  if (
    Number.isNaN(number)
  ) {
    return `${field.label} must be a valid number`;
  }

  if (number < 0) {
    return `${field.label} must be greater than 0`;
  }

  return null;
};

const validateEmailField = (
  field,
  value
) => {
  const isEmailField =
    field.type === "email" ||
    field.name === "username";

  if (
    !isEmailField ||
    isEmpty(value)
  ) {
    return null;
  }

  if (
    !isValidEmail(value)
  ) {
    return "Enter a valid email address";
  }

  return null;
};

const validatePassword = (
  field,
  value
) => {
  if (
    field.type !== "password" ||
    isEmpty(value)
  ) {
    return null;
  }

  const password =
    value.toString();

  const strongPassword =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/;

  if (
    !strongPassword.test(
      password
    )
  ) {
    return "Password must contain uppercase, lowercase, number and special character";
  }

  return null;
};

const validatePhoneField = (
  field,
  value
) => {
  if (
    field.name !== "phoneNo" ||
    isEmpty(value)
  ) {
    return null;
  }

  if (
    !isValidPhone(value)
  ) {
    return "Phone number must be exactly 10 digits";
  }

  return null;
};

const isEmpty = (
  value
) => {
  return (
    value === undefined ||
    value === null ||
    (
      typeof value ===
        "string" &&
      value.trim() === ""
    ) ||
    (
      Array.isArray(
        value
      ) &&
      value.length === 0
    )
  );
};

const isValidEmail = (
  value
) => {
  return /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(
    value.toString().trim()
  );
};

const isValidPhone = (
  value
) => {
  return /^\d{10}$/.test(
    value.toString().trim()
  );
};