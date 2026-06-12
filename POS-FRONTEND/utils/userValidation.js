import {
  validateEmail,
  validatePhone,
  validatePassword
} from "@/utils/validation";

import { AUTH_MESSAGES } from "@/constants/messages";

export function validateUserForm({
  name,
  username,
  phoneNo,
  password,
  confirmPassword,
  roles
}) {

  if (
    name.trim() === "" ||
    username.trim() === "" ||
    phoneNo.trim() === "" ||
    password.trim() === "" ||
    confirmPassword.trim() === ""
  ) {
    return AUTH_MESSAGES.ALL_FIELDS_REQUIRED;
  }

  if (!validateEmail(username)) {
    return AUTH_MESSAGES.INVALID_EMAIL;
  }

  if (!validatePhone(phoneNo)) {
    return AUTH_MESSAGES.INVALID_PHONE;
  }

  if (!validatePassword(password)) {
    return AUTH_MESSAGES.PASSWORD_MIN;
  }

  if (password !== confirmPassword) {
    return AUTH_MESSAGES.PASSWORD_MISMATCH;
  }

  if (roles.length === 0) {
    return AUTH_MESSAGES.ROLE_REQUIRED;
  }

  return null;
}