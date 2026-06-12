export const validateForm = (
  fields,
  formData,
  skipReadOnly = false
) => {
  const newErrors = {};

  fields.forEach((field) => {
    if (skipReadOnly && field.readOnly) return;

    const value = formData[field.name];

    if (
      value === undefined ||
      value === null ||
      value.toString().trim() === ""
    ) {
      newErrors[field.name] =
        `${field.label} is required`;
      return;
    }

    if (
      field.type === "number" &&
      Number(value) < 0
    ) {
      newErrors[field.name] =
        `${field.label} must be greater than 0`;
    }

   if (field.type === "email") {
    const email = value.trim();
    const atIndex = email.indexOf("@");
    const dotIndex = email.lastIndexOf(".");
    const isValid =
    atIndex > 0 &&
    dotIndex > atIndex + 1 &&
    dotIndex < email.length - 1 && !email.includes(" ");
    if (!isValid) {
      newErrors[field.name] = "Invalid email format";
    }
  }

    if (
      field.type === "password" &&
      value.length < 6
    ) {
      newErrors[field.name] =
        "Password must be at least 6 characters";
    }

    if (field.type === "tel") {
      const phone = value.toString().trim();

      if (!/^\d{10}$/.test(phone)) {
        newErrors[field.name] =
          "Phone number must be exactly 10 digits";
      }
    }
  });

  return newErrors;
};