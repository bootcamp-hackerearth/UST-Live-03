"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import CommonAdd from "@/components/common/CommonAdd";
import { addItem } from "@/services/api";

export default function AddRole() {
  const router = useRouter();

  const [identifier, setIdentifier] =
    useState("");

  const [description, setDescription] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const [successMessage, setSuccessMessage] =
    useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);

      setError("");

      setSuccessMessage("");

      const response = await addItem(
        "role",
        {
          identifier,
          description,
          status: true,
        }
      );

      if (!response.success) {
        setError(
          response.message ||
            "Failed to create Role"
        );

        return;
      }

      setSuccessMessage(
        "Role created successfully"
      );

      setTimeout(() => {
        router.push("/roles");
      }, 1000);
    } catch (error) {
      console.log(error);

      setError(
        "Failed to create Role"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <CommonAdd
      title="Add Role"
      subtitle="Create a new Role for the system"
      identifier={identifier}
      setIdentifier={setIdentifier}
      description={description}
      setDescription={setDescription}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/roles"
    />
  );
}