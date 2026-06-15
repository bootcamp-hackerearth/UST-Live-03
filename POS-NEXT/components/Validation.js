export const Validation = (data, type) => {

  const VALIDATION_MESSAGES = {
    CREDENTIALS_MESSAGE:
      "Must be at least 8 characters, include uppercase, lowercase, number and special character",
  };

  let newErrors = {};

  if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(data.password)) {
    newErrors.passwordMessage = VALIDATION_MESSAGES.CREDENTIALS_MESSAGE;
  }

  if (type === "login") {
    if (!data.username?.includes("@")) {
      newErrors.username = "Invalid email";
    }
  }

  if (type === "register") {
    if (
      !data.name ||
      data.name.length < 3 ||
      !/^[A-Za-z\s]+$/.test(data.name)
    ) {
      newErrors.name =
        "Name must be at least 3 characters and contain only letters";
    }

    if (!data.username || !/^[^\s@]+@(ust\.com|gmail\.com)$/i.test(data.username)) {
      newErrors.username = "Enter a valid UST or Gmail email";
    }

    if (!data.roles) {
      newErrors.roles = "Please select a role";
    }

    if (!/^\d{10}$/.test(data.phoneNo)) {
      newErrors.phoneNo = "Phone number must be exactly 10 digits";
    }

    if (!data.roles) {
      newErrors.roles = "Select a role";
    }
  }

  return newErrors;
}
