"use client";
import AddFormSkeleton from "../../components/AddFormSkeleton";

export default function AddUser() {
  const fields = [
    {
      name: "name",
      label: "Full Name",
      type: "text",
      required: true,
      placeholder: "Enter full user name...",
    },
    {
      name: "username",
      label: "Username",
      type: "text",
      required: true,
      placeholder: "e.g., janesmith_pos",
    },
    {
      name: "password",
      label: "Password",
      type: "password",
      required: true,
      placeholder: "Enter secure initial password...",
      // Rules: At least 8 characters, 1 uppercase, 1 lowercase, 1 number
      validation: {
        regex: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/,
        message: "Password must be at least 8 characters long and contain an uppercase letter, a lowercase letter, and a number.",
      }
    },
    {
      name: "phoneNo",
      label: "Phone No",
      type: "text",
      required: true,
      placeholder: "Enter contact number...",
      // Rules: Exactly 10 digits
      validation: {
        regex: /^\d{10}$/,
        message: "Phone number must be exactly 10 digits.",
      }
    },
    {
      name: "roles", 
      label: "Security Roles",
      type: "select",
      api: "role", 
      optionLabel: "identifier",
      optionValue: "identifier",
      required: true,
      multiple: true, 
    },
  ];

  return (
    <AddFormSkeleton
      title="User"
      apiPath="user"
      endpoint="register" 
      fields={fields}
    />
  );
}