"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";
import { validators } from "@/lib/security";

function validateEmail(val) {
  if (!String(val ?? "").trim()) return "Email is required.";
  if (!validators.email(val)) return "Enter a valid email address.";
  return null;
}

function validateName(val) {
  if (!String(val ?? "").trim()) return "Full name is required.";
  return null;
}

function validatePhone(val) {
  if (!String(val ?? "").trim()) return "Phone number is required.";
  if (!validators.phone(val)) return "Phone must be exactly 10 digits.";
  return null;
}

function validatePassword(val) {
  const v = String(val ?? "");
  if (!v) return "Password is required.";
  if (!validators.passwordStrength(v))
    return "Password must be 8+ chars with uppercase, lowercase, digit, and special character.";
  return null;
}

function validateRoles(val) {
  if (!Array.isArray(val) || val.length === 0)
    return "Please select at least one Role.";
  return null;
}

export default function UsersPage() {
  const config = crudConfigs.users();
  config.fields = config.fields.map((f) => {
    if (f.key === "username") return { ...f, validate: validateEmail };
    if (f.key === "name") return { ...f, validate: validateName };
    if (f.key === "phoneNo") return { ...f, validate: validatePhone };
    if (f.key === "password") return { ...f, validate: validatePassword };
    if (f.key === "roles") return { ...f, validate: validateRoles };
    return f;
  });
  return <CrudPage config={config} />;
}
