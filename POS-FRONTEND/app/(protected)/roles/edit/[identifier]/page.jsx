"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import {getItem, updateItem, } from "@/services/api";

export default function EditRole() {
  const router = useRouter();

  const params = useParams();

  const identifier =
    decodeURIComponent(
      params.identifier
    );

  const [description, setDescription] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [pageLoading, setPageLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [successMessage, setSuccessMessage] =
    useState("");

  useEffect(() => {
    loadRole();
  }, []);

  const loadRole = async () => {
    try {
      setPageLoading(true);

      setError("");

      const response =
        await getItem(
          "role",
          identifier
        );

      setDescription(
        response.description || ""
      );
    } catch (error) {
      console.log(error);

      setError(
        "Failed to load role"
      );
    } finally {
      setPageLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);

      setError("");

      setSuccessMessage("");

      const response =
        await updateItem(
          "role",
          {
            identifier,
            description,
          }
        );

      if (!response.success) {
        setError(
          response.message ||
            "Failed to update role"
        );

        return;
      }

      setSuccessMessage(
        "Role updated successfully"
      );

      setTimeout(() => {
        router.push("/roles");
      }, 1000);
    } catch (error) {
      console.log(error);

      setError(
        "Failed to update role"
      );
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <p className="text-[#667085]">
          Loading role...
        </p>
      </div>
    );
  }

  return (
    <CommonEdit
      title="Edit Role"
      subtitle="Update Role"
      identifier={identifier}
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