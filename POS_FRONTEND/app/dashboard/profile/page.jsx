"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import CommonForm from "@/components/CommonForm";

const fields = [
  {
    name: "username",
    label: "Email",
    type: "text",
    required: true
  },
  {
    name: "name",
    label: "Full Name",
    type: "text",
    required: true
  },
  {
    name: "phoneNo",
    label: "Phone Number",
    type: "text",
    required: true
  },
  {
    name: "roles",
    label: "Roles",
    type: "select",
    multiple: true,
    required: true,
    api: "/role/list"
  }
];

export default function ProfilePage() {
  const [username, setUsername] = useState("");
  const router = useRouter();

  useEffect(() => {
    setUsername(localStorage.getItem("username"));
  }, []);

  const handleProfileUpdate = (updatedData) => {
    const oldUsername = localStorage.getItem("username");

    if (updatedData.username !== oldUsername) {
      alert("Login with your new email");
      localStorage.clear();
      router.push("/login");
      return;
    }

    alert("Profile updated successfully");
  };

  if (!username) return null;

  return (
    <div className="min-h-screen bg-[#F6F7F9] flex items-center justify-center p-6">

      <div className="w-full max-w-3xl bg-white rounded-2xl shadow-lg border border-gray-200 p-8">

        <div className="mb-6 border-b pb-4">
          <h1 className="text-2xl font-semibold text-gray-800">
            My Profile
          </h1>
          <p className="text-sm text-gray-500 mt-1">
            View and update your personal details
          </p>
        </div>

        <div className="mt-4">
          <CommonForm
            api="/user"
            mode="edit"
            identifier={username}
            identifierParam="username"
            fields={fields}
            onSuccess={handleProfileUpdate}
          />
        </div>
      </div>
    </div>
  );
}