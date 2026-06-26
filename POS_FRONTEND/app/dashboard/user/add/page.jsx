"use client";

import CommonForm from "@/components/CommonForm";

export default function AddUserPage() {
  const passwordPolicyValidator = (value) => {
    const password = String(value ?? "");

    const hasUppercase = /[A-Z]/.test(password);
    const hasDigit = /\d/.test(password);
    const hasSpecial = /[^A-Za-z0-9]/.test(password);

    return hasUppercase && hasDigit && hasSpecial
      ? ""
      : "Password must include 1 uppercase letter, 1 number, and 1 special character";
  };

  const fields = [
    {
      name: "name",
      label: "Full Name",
      type: "text",
      required: true,
      rules: [
        {
          type: "pattern",
          value: /^[A-Za-z\s]+$/,
          message: "Name must contain only letters and spaces",
        },
      ],
    },
    {
      name: "username",
      label: "Email",
      type: "text",
      required: true,
      rules: [
        {
          type: "pattern",
          value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
          message: "Invalid email format",
        },
      ],
    },
    {
      name: "phoneNo",
      label: "Phone Number",
      type: "text",
      required: true,
      rules: [
        {
          type: "pattern",
          value: /^\d{10}$/,
          message: "Phone Number must be 10 digits",
        },
      ],
    },
    {
      name: "password",
      label: "Password",
      type: "text",
      required: true,
      rules: [
        {
          type: "minLength",
          value: 6,
          message: "Password must be at least 6 characters",
        },
        {
          type: "custom",
          validator: (value) => passwordPolicyValidator(value),
          message: "Password must include 1 uppercase letter, 1 number, and 1 special character",
        },
      ],
    },
    {
      name: "roles",
      label: "Roles",
      type: "select",
      required: true,
      multiple: true,
      api: "/role/list"
    }
  ];

  return (
    <div className="
      min-h-screen bg-slate-100
      flex justify-center items-center
      p-6
    ">

      <div className="
        w-full max-w-4xl
        bg-white border rounded-2xl
        shadow-xl p-6
      ">

        <div className="mb-6">
          <h1 className="text-xl font-semibold text-slate-800">
            Add User
          </h1>
          <p className="text-sm text-slate-500">
            Create a new system user and assign roles
          </p>
        </div>

        <CommonForm
          api="/user"
          mode="add"
          fields={fields}
        />

      </div>

    </div>
  );
}